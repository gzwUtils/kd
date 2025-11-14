package com.gzw.kd.websocket;
import com.alibaba.fastjson.JSON;
import com.gzw.kd.common.entity.MsgVo;
import com.gzw.kd.common.entity.OnlineUser;
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

@ServerEndpoint("/websocket/{uid}")
@Component
@Slf4j
public class WebsocketServer {

    private static final Map<String, OnlineUser> ONLINE_MAP = new ConcurrentHashMap<>();

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
        List<UserListVo> list = ONLINE_MAP.values().stream()
                .map(u -> new UserListVo(u.getUid(), u.getName())).collect(Collectors.toList());
        broadCast(JSON.toJSONString(new UserListDTO(list)));
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

        if ("group".equals(vo.getType())) {
            broadCast(buildMsg("group", me.getName(), null, vo.getContent()));
        } else if ("private".equals(vo.getType())) {
            String msg = buildMsg("private", me.getName(), vo.getTo(), vo.getContent());
            sendTo(vo.getTo(), msg);
            sendObj(me.getSession(), msg);   // 回执
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