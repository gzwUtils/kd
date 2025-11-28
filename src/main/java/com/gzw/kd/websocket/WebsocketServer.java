package com.gzw.kd.websocket;
import com.alibaba.fastjson.JSON;
import com.gzw.kd.common.entity.MsgVo;
import com.gzw.kd.common.entity.OnlineUser;
import com.gzw.kd.common.entity.Room;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
@SuppressWarnings("all")
@ServerEndpoint("/websocket/{uid}")
@Component
@Slf4j
public class WebsocketServer {

    private static final Map<String, OnlineUser> ONLINE_MAP = new ConcurrentHashMap<>();

    private static final Map<String, Room> ROOM_MAP = new ConcurrentHashMap<>();


    /* ===== 工具 ===== */
    private void removeAndClose(String uid) {
        OnlineUser u = ONLINE_MAP.remove(uid);
        if (u != null && u.getSession() != null) {
            try {
                u.getSession().close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "duplicate"));
            } catch (IOException e) {
                log.error("uid {} removeAndClose error  {}",uid,e.getMessage());
            }
        }
    }

    private void sendObj(Session s, String json) {
        if (s != null && s.isOpen()) {
            try { s.getBasicRemote().sendText(json); } catch (IOException e) { log.error("send error", e); }
        }
    }

    private void broadCast(String json) {
        ONLINE_MAP.values().forEach(u -> sendObj(u.getSession(), json));
    }

    private void sendTo(String uid, String json) {
        OnlineUser u = ONLINE_MAP.get(uid);
        if (u != null) sendObj(u.getSession(), json);
    }

    private String buildMsg(String type, String from, String to, String content) {
        return JSON.toJSONString(new MsgVo(type, from, to, content, System.currentTimeMillis()));
    }

    private void refreshUsers() {
        List<UserListVo> users = ONLINE_MAP.values().stream()
                .map(u -> new UserListVo(u.getUid(), u.getName()))
                .collect(Collectors.toList());
        List<UserListVo> rooms = ROOM_MAP.values().stream()
                .map(r -> new UserListVo(r.getRoomId(), "【房间】"+r.getRoomName()))
                .collect(Collectors.toList());
        users.addAll(rooms);
        broadCast(JSON.toJSONString(new UserListDTO(users)));
    }

    /* ===== 生命周期 ===== */
    @OnOpen
    public void onOpen(Session session, @PathParam("uid") String uid) {
        removeAndClose(uid);                                     // 踢旧
        String name = uid.substring(0,uid.indexOf(":"));
        ONLINE_MAP.put(uid, new OnlineUser(uid, name, session));
        log.info("{} 上线，当前在线 {}", name, ONLINE_MAP.size());

        // 告诉前端自己是谁
        sendObj(session, buildMsg("selfInfo", null, uid, name));
        broadCast(buildMsg("sys", null, null, name + " 加入了群聊"));
        refreshUsers();

        // 定时心跳
        startPing(session);
    }

    @OnClose
    public void onClose(@PathParam("uid") String uid) {
        OnlineUser u = ONLINE_MAP.remove(uid);
        if (u != null) {
            log.info("{} 下线", u.getName());
            broadCast(buildMsg("sys", null, null, u.getName() + " 离开了群聊"));
            refreshUsers();
        }
    }

    @OnError
    public void onError(@PathParam("uid") String uid, Throwable t) {
        log.error("uid {} error {}", uid, t.getMessage());
        removeAndClose(uid);
    }

    @OnMessage
    public void onMessage(String json, @PathParam("uid") String uid) {
        MsgVo vo;
        try { vo = JSON.parseObject(json, MsgVo.class); } catch (Exception e) { return; }
        if (vo == null || vo.getType() == null || vo.getContent() == null) return;

        OnlineUser me = ONLINE_MAP.get(uid);
        if (me == null) return;

        /* 只贴 switch 新增部分 */
        switch (vo.getType()) {
            case "group":   // 原群聊
                broadCast(buildMsg("group", me.getName(), null, vo.getContent()));
                break;
            case "private": // 原私聊
                String m = buildMsg("private", me.getName(), vo.getTo(), vo.getContent());
                sendTo(vo.getTo(), m);
                sendObj(me.getSession(), m);
                break;

            case "createRoom":   // 创建房间
                String rid = createRoom(vo.getContent(), uid);
                sendObj(me.getSession(), buildMsg("selfRoom", null, uid, rid));
                refreshUsers();   // 把房间也当作用户列表的一种
                break;

            case "joinRoom":     // 加入房间
                boolean ok = joinRoom(vo.getTo(), uid);
                sendObj(me.getSession(),
                        buildMsg(ok ? "sys" : "error", null, uid, ok ? "已加入房间" : "房间不存在"));
                refreshUsers();
                break;

            case "room":         // 在房间里发言
                broadcastRoom(vo.getTo(), buildMsg("room", me.getName(), vo.getTo(), vo.getContent()));
                break;
        }
    }

    /* ===== 心跳 ===== */
    private void startPing(Session session) {
        new Thread(() -> {
            while (session.isOpen()) {
                try {
                    Thread.sleep(30000);
                    if (session.isOpen()) session.getAsyncRemote().sendPing(ByteBuffer.wrap("PING".getBytes()));
                } catch (Exception e) {
                    log.error("ping error {}",e.getMessage());
                }
            }
        }).start();
    }


    /* 推给某个房间所有人 */
    private void broadcastRoom(String roomId, String json) {
        Room room = ROOM_MAP.get(roomId);
        if (room == null) return;
        room.getMembers().forEach(uid -> sendTo(uid, json));
    }

    /* 创建房间 */
    private String createRoom(String roomName, String creatorUid) {
        String rid = "room_" + System.currentTimeMillis();
        Room r = new Room(rid, roomName, ConcurrentHashMap.newKeySet());
        r.getMembers().add(creatorUid);
        ROOM_MAP.put(rid, r);
        return rid;
    }

    /* 加入房间 */
    private boolean joinRoom(String roomId, String uid) {
        Room r = ROOM_MAP.get(roomId);
        if (r == null) return false;
        r.getMembers().add(uid);
        return true;
    }

    /* 离开房间 */
    private void leaveRoom(String roomId, String uid) {
        Room r = ROOM_MAP.get(roomId);
        if (r == null) return;
        r.getMembers().remove(uid);
        if (r.getMembers().isEmpty()) ROOM_MAP.remove(roomId);
    }

    /* ===== DTO ===== */
    @Data
    @AllArgsConstructor
    public static class UserListVo {
        private String uid;
        private String name;
    }
    @Data
    @AllArgsConstructor
    public static class UserListDTO {
        private String type;
        private List<UserListVo> list;

        public UserListDTO(List<UserListVo> list){
            this.list = list;
            this.type = "userList";
        }

    }
}