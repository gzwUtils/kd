package com.gzw.kd.websocket;

import com.alibaba.fastjson.JSON;
import com.gzw.kd.common.entity.MsgVo;
import com.gzw.kd.common.entity.OnlineUser;
import com.gzw.kd.common.entity.Room;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 现代WebSocket聊天服务器
 * 支持私聊、房间聊天、文件传输、主题切换等功能
 *
 * @author gaozw
 * @since 2024
 */
@ServerEndpoint("/websocket/{uid}")
@Component
@Slf4j
@SuppressWarnings({"unused"})
public class WebsocketServer implements DisposableBean {

    // ==================== 配置常量 ====================
    private static final int MAX_CONNECTIONS = 2000;
    private static final int HEARTBEAT_INTERVAL_SECONDS = 25;
    private static final int HEARTBEAT_TIMEOUT_SECONDS = 60;
    private static final long MAX_SESSION_IDLE_TIMEOUT = 300000L;
    private static final int MAX_MESSAGE_LENGTH = 2000;
    private static final int MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final int CLEANUP_INTERVAL_SECONDS = 30;
    private static final String ROOM_ID_PREFIX = "room_";
    private static final String TYPE_FIELD = "type";
    private static final String ROOM_ID = "roomId";
    private static final String ROOM_NAME = "roomName";
    private static final String MEMBERS = "members";

    // ==================== 数据存储 ====================
    private static final ConcurrentMap<String, OnlineUser> ONLINE_MAP = new ConcurrentHashMap<>(256);
    private static final ConcurrentMap<String, Room> ROOM_MAP = new ConcurrentHashMap<>(64);
    private static final ConcurrentMap<String, Boolean> CLEANING_MAP = new ConcurrentHashMap<>(128);

    // 新增：账户到用户ID列表的映射（用于根据账户名查找用户）
    private static final ConcurrentMap<String, Set<String>> ACCOUNT_TO_UIDS = new ConcurrentHashMap<>(256);

    // ==================== 线程池引用 ====================
    private static final AtomicReference<ThreadPoolTaskScheduler> HEARTBEAT_SCHEDULER_REF = new AtomicReference<>();
    private static final AtomicReference<ThreadPoolTaskExecutor> MESSAGE_EXECUTOR_REF = new AtomicReference<>();

    // ==================== 统计信息 ====================
    private static final AtomicInteger ACTIVE_CONNECTIONS = new AtomicInteger(0);
    private static final AtomicInteger TOTAL_CONNECTIONS = new AtomicInteger(0);
    private static final AtomicBoolean INITIALIZED = new AtomicBoolean(false);

    // 线程安全的实例引用
    private static final AtomicReference<WebsocketServer> INSTANCE_REF = new AtomicReference<>();

    // ==================== 构造函数 ====================
    public WebsocketServer() {
        INSTANCE_REF.set(this);
    }

    // ==================== 依赖注入 ====================
    @Autowired
    @Lazy
    public void setMessageExecutor(ThreadPoolTaskExecutor messageExecutor) {
        MESSAGE_EXECUTOR_REF.set(messageExecutor);
        initIfReady();
    }

    @Autowired
    @Lazy
    public void setHeartbeatScheduler(ThreadPoolTaskScheduler heartbeatScheduler) {
        HEARTBEAT_SCHEDULER_REF.set(heartbeatScheduler);
        initIfReady();
    }

    // ==================== 初始化方法 ====================
    private static void initIfReady() {
        if (HEARTBEAT_SCHEDULER_REF.get() != null
                && MESSAGE_EXECUTOR_REF.get() != null
                && INITIALIZED.compareAndSet(false, true)) {

            startCleanupTask();
            log.info("现代WebSocket服务初始化完成");
        }
    }

    private static void startCleanupTask() {
        try {
            ThreadPoolTaskScheduler scheduler = HEARTBEAT_SCHEDULER_REF.get();
            if (scheduler != null) {
                scheduler.scheduleWithFixedDelay(
                        WebsocketServer::cleanupInactiveConnections,
                        Duration.ofSeconds(CLEANUP_INTERVAL_SECONDS)
                );
            }
        } catch (Exception e) {
            log.error("启动清理任务失败", e);
            INITIALIZED.set(false);
        }
    }

    // ==================== WebSocket 事件处理 ====================

    /**
     * 连接打开事件
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("uid") String uid) {
        if (uid == null || session == null) {
            return;
        }

        try {
            handleConnectionOpen(session, uid);
        } catch (Exception e) {
            log.error("处理连接异常, uid={}", uid, e);
            safeRemoveConnection(uid);
        }
    }

    private void handleConnectionOpen(Session session, String uid) {
        // 1. 验证参数
        if (!validateConnection(uid, session)) {
            return;
        }

        // 2. 解析用户信息
        UserInfo userInfo = parseUserInfo(uid);
        if (userInfo == null) {
            closeSessionSafely(session, uid, "用户信息解析失败");
            return;
        }

        // 3. 配置session
        configureSession(session);

        // 4. 创建并注册用户
        OnlineUser newUser = createAndRegisterUser(uid, userInfo.account, session);

        // 5. 更新账户映射
        updateAccountMapping(userInfo.account, uid);

        // 6. 启动心跳检测
        startHeartbeat(newUser);

        // 7. 发送初始消息
        sendInitialMessages(session, uid, userInfo.account);

        // 8. 刷新用户和房间列表
        refreshUserAndRoomLists();

        log.info("用户连接成功: {}, 活跃连接: {}", userInfo.account, ACTIVE_CONNECTIONS.get());
    }

    /**
     * 连接关闭事件
     */
    @OnClose
    public void onClose(@PathParam("uid") String uid) {
        log.info("连接关闭: {}", uid);
        safeRemoveConnection(uid);
    }

    /**
     * 连接错误事件
     */
    @OnError
    public void onError(@PathParam("uid") String uid, Throwable error) {
        if (error instanceof IOException && "Connection reset".equals(error.getMessage())) {
            log.warn("连接重置: {}", uid);
        } else {
            log.error("WebSocket错误, uid={}", uid, error);
        }
        safeRemoveConnection(uid);
    }

    /**
     * 接收文本消息事件
     */
    @OnMessage
    public void onMessage(String message, @PathParam("uid") String uid) {
        if (message == null || message.isEmpty()) {
            return;
        }

        ThreadPoolTaskExecutor executor = getMessageExecutor();
        if (executor == null) {
            log.error("消息执行器未初始化:{}", uid);
            return;
        }

        executor.execute(() -> {
            try {
                MsgVo msg = JSON.parseObject(message, MsgVo.class);
                if (msg == null || msg.getType() == null) {
                    log.warn("收到无效消息格式, uid={}, message={}", uid, message);
                    return;
                }

                OnlineUser user = ONLINE_MAP.get(uid);
                if (user != null) {
                    user.updateActiveTime();
                    processMessage(msg, user);
                }
            } catch (Exception e) {
                log.error("处理消息失败, uid={}", uid, e);
            }
        });
    }

    /**
     * 接收Pong消息事件
     */
    @OnMessage
    public void onPong(PongMessage pongMessage, @PathParam("uid") String uid) {
        OnlineUser user = ONLINE_MAP.get(uid);
        if (user != null) {
            user.updateActiveTime();
        }
    }

    // ==================== 核心业务逻辑 ====================

    /**
     * 安全移除连接
     */
    private static void safeRemoveConnection(String uid) {
        if (uid == null || uid.isEmpty()) {
            return;
        }

        // 防止重复清理
        if (CLEANING_MAP.putIfAbsent(uid, Boolean.TRUE) != null) {
            return;
        }

        try {
            String userName = removeUserFromOnlineMap(uid);

            // 广播用户离开通知
            if (userName != null) {
                WebsocketServer instance = INSTANCE_REF.get();
                if (instance != null) {
                    instance.broadcastUserLeave(userName);
                }
                refreshUserList();
                refreshRoomList();
            }

        } catch (Exception e) {
            log.error("移除连接异常, uid={}", uid, e);
        } finally {
            CLEANING_MAP.remove(uid);
        }
    }

    private static String removeUserFromOnlineMap(String uid) {
        String userName = null;

        // 从在线列表中移除
        OnlineUser user = ONLINE_MAP.remove(uid);
        if (user != null) {
            userName = user.getName();

            // 从账户映射中移除
            removeFromAccountMapping(userName, uid);

            // 离开所有房间
            leaveAllRooms(uid);

            // 清理用户资源
            user.cleanup();

            // 更新统计
            ACTIVE_CONNECTIONS.decrementAndGet();

            log.info("用户离开: {}, 剩余活跃连接: {}", userName, ACTIVE_CONNECTIONS.get());
        }

        return userName;
    }

    /**
     * 清理无响应连接
     */
    private static void cleanupInactiveConnections() {
        try {
            long now = System.currentTimeMillis();
            List<String> toRemove = collectInactiveConnections(now);

            // 批量清理
            if (!toRemove.isEmpty()) {
                removeConnections(toRemove);
                log.info("清理完成，移除 {} 个无响应连接", toRemove.size());
            }

        } catch (Exception e) {
            log.error("清理连接异常", e);
        }
    }

    private static List<String> collectInactiveConnections(long now) {
        List<String> toRemove = new ArrayList<>();

        for (Map.Entry<String, OnlineUser> entry : ONLINE_MAP.entrySet()) {
            String uid = entry.getKey();
            OnlineUser user = entry.getValue();

            long inactiveTime = TimeUnit.MILLISECONDS.toSeconds(now - user.getLastActiveTime());
            if (inactiveTime > HEARTBEAT_TIMEOUT_SECONDS) {
                toRemove.add(uid);
                log.warn("检测到无响应连接: {}, 无响应时间: {}s", uid, inactiveTime);
            }
        }

        return toRemove;
    }

    private static void removeConnections(List<String> connections) {
        for (String uid : connections) {
            safeRemoveConnection(uid);
        }
    }

    /**
     * 处理各种类型的消息
     */
    private void processMessage(MsgVo msg, OnlineUser user) {
        if (msg == null || user == null) {
            return;
        }

        try {
            String messageType = msg.getType();
            switch (messageType) {
                case "private":
                    handlePrivateMessage(msg, user);
                    break;
                case "room":
                    handleRoomMessage(msg, user);
                    break;
                case "createRoom":
                    handleCreateRoom(msg, user);
                    break;
                case "joinRoom":
                    handleJoinRoom(msg, user);
                    break;
                case "leaveRoom":
                    handleLeaveRoom(msg, user);
                    break;
                case "getUserList":
                    handleGetUserList(user);
                    break;
                case "getRoomList":
                    handleGetRoomList(user);
                    break;
                case "ping":
                    handlePing(user);
                    break;
                case "file":
                    handleFileMessage(msg, user);
                    break;
                case "connect":
                    refreshUserAndRoomLists();
                    break;
                case "disconnect":
                    safeRemoveConnection(user.getUid());
                    break;
                case "login":
                    // 处理登录消息，前端连接成功后会自动发送
                    handleLoginMessage(msg, user);
                    break;
                default:
                    log.warn("未知消息类型: {}", messageType);
                    sendErrorMessage(user.getUid(), "未知消息类型");
            }
        } catch (Exception e) {
            log.error("处理消息异常, type={}, uid={}", msg.getType(), user.getUid(), e);
            sendErrorMessage(user.getUid(), "消息处理失败");
        }
    }

    // ==================== 消息处理具体实现 ====================

    private void handleLoginMessage(MsgVo msg, OnlineUser user) {
        // 更新用户信息（如果消息中有额外信息）
        log.info("用户登录成功: {} {}", user.getName(),msg.getContent());
        // 可以在这里处理额外的用户信息更新
    }

    private void handlePrivateMessage(MsgVo msg, OnlineUser user) {
        String toAccount = msg.getTo(); // 前端发送的是账户名，不是完整的uid
        if (toAccount == null) {
            sendErrorMessage(user.getUid(), "请选择私聊对象");
            return;
        }

        // 检查是否是给自己发送消息
        if (toAccount.equals(user.getName())) {
            // 可以给自己发送消息，直接返回
            String content = truncateMessage(msg.getContent());
            String selfMsg = buildMsg("private", user.getName(), user.getName(), content);
            sendToUser(user.getUid(), selfMsg);
            return;
        }

        // 根据账户名查找所有在线的用户（可能有多个会话）
        Set<String> targetUids = ACCOUNT_TO_UIDS.get(toAccount);
        if (targetUids == null || targetUids.isEmpty()) {
            sendErrorMessage(user.getUid(), "目标用户已离线");
            return;
        }

        String content = truncateMessage(msg.getContent());

        // 构建消息：发送者显示为账户名，接收者为目标账户
        MsgVo privateMsg = new MsgVo();
        privateMsg.setType("private");
        privateMsg.setFrom(user.getName()); // 使用账户名作为发送者
        privateMsg.setTo(toAccount); // 使用账户名作为接收者
        privateMsg.setContent(content);
        privateMsg.setTime(System.currentTimeMillis());

        String jsonMsg = JSON.toJSONString(privateMsg);

        // 发送给目标用户的所有会话
        for (String targetUid : targetUids) {
            sendToUser(targetUid, jsonMsg);
        }

        // 也发送给发送者（用于在前端显示）
        sendToUser(user.getUid(), jsonMsg);

        log.debug("私聊消息: {} -> {}: {}", user.getName(), toAccount, content);
    }

    private void handleRoomMessage(MsgVo msg, OnlineUser user) {
        String roomId = msg.getTo();
        if (roomId == null) {
            sendErrorMessage(user.getUid(), "请选择房间");
            return;
        }

        // 检查用户是否在房间中
        Room room = ROOM_MAP.get(roomId);
        if (room == null) {
            sendErrorMessage(user.getUid(), user.getName()+"房间不存在");
            return;
        }

        if (!room.getMembers().contains(user.getUid())) {
            sendErrorMessage(user.getUid(), user.getName()+"您未加入该房间");
            return;
        }

        String content = truncateMessage(msg.getContent());
        String roomMsg = buildMsg("room", user.getName(), roomId, content);
        broadcastToRoom(roomId, roomMsg);

        log.debug("房间消息: {} -> {}: {}", user.getName(), room.getRoomName(), content);
    }

    private void handleFileMessage(MsgVo msg, OnlineUser user) {
        String targetId = msg.getTo(); // 可能是账户名或房间ID
        if (targetId == null) {
            sendErrorMessage(user.getUid(), "请选择发送对象");
            return;
        }

        // 验证文件大小
        if (msg.getFileData() != null && msg.getFileData().length() > MAX_FILE_SIZE) {
            sendErrorMessage(user.getUid(), "文件大小不能超过10MB");
            return;
        }

        // 判断是私聊文件还是房间文件
        if (targetId.startsWith(ROOM_ID_PREFIX)) {
            // 房间文件
            handleRoomFileMessage(msg, user, targetId);
        } else {
            // 私聊文件
            handlePrivateFileMessage(msg, user, targetId);
        }
    }

    private void handleRoomFileMessage(MsgVo msg, OnlineUser user, String roomId) {
        Room room = ROOM_MAP.get(roomId);
        if (room == null) {
            sendErrorMessage(user.getUid(), "房间不存在");
            return;
        }

        if (!room.getMembers().contains(user.getUid())) {
            sendErrorMessage(user.getUid(), "您未加入该房间");
            return;
        }

        MsgVo fileMsg = new MsgVo();
        fileMsg.setType("file");
        fileMsg.setFrom(user.getName());
        fileMsg.setTo(roomId);
        fileMsg.setContent("[文件] " + msg.getFileName());
        fileMsg.setTime(System.currentTimeMillis());
        fileMsg.setFileName(msg.getFileName());
        fileMsg.setFileType(msg.getFileType());
        fileMsg.setFileData(msg.getFileData());

        String jsonMsg = JSON.toJSONString(fileMsg);
        broadcastToRoom(roomId, jsonMsg);

        log.info("房间文件消息: {} -> {}: {}", user.getName(), room.getRoomName(), msg.getFileName());
    }

    private void handlePrivateFileMessage(MsgVo msg, OnlineUser user, String targetAccount) {
        // 检查是否是给自己发送文件
        if (targetAccount.equals(user.getName())) {
            // 给自己发送文件
            MsgVo selfFileMsg = new MsgVo();
            selfFileMsg.setType("file");
            selfFileMsg.setFrom(user.getName());
            selfFileMsg.setTo(user.getName());
            selfFileMsg.setContent("[文件] " + msg.getFileName());
            selfFileMsg.setTime(System.currentTimeMillis());
            selfFileMsg.setFileName(msg.getFileName());
            selfFileMsg.setFileType(msg.getFileType());
            selfFileMsg.setFileData(msg.getFileData());

            String jsonMsg = JSON.toJSONString(selfFileMsg);
            sendToUser(user.getUid(), jsonMsg);
            return;
        }

        // 查找目标用户的所有会话
        Set<String> targetUids = ACCOUNT_TO_UIDS.get(targetAccount);
        if (targetUids == null || targetUids.isEmpty()) {
            sendErrorMessage(user.getUid(), "目标用户已离线");
            return;
        }

        MsgVo fileMsg = new MsgVo();
        fileMsg.setType("file");
        fileMsg.setFrom(user.getName());
        fileMsg.setTo(targetAccount);
        fileMsg.setContent("[文件] " + msg.getFileName());
        fileMsg.setTime(System.currentTimeMillis());
        fileMsg.setFileName(msg.getFileName());
        fileMsg.setFileType(msg.getFileType());
        fileMsg.setFileData(msg.getFileData());

        String jsonMsg = JSON.toJSONString(fileMsg);

        // 发送给目标用户的所有会话
        for (String targetUid : targetUids) {
            sendToUser(targetUid, jsonMsg);
        }

        // 也发送给发送者
        sendToUser(user.getUid(), jsonMsg);

        log.info("私聊文件消息: {} -> {}: {}", user.getName(), targetAccount, msg.getFileName());
    }

    private void handleCreateRoom(MsgVo msg, OnlineUser user) {
        String roomName = truncateMessage(msg.getContent());
        if (roomName.isEmpty()) {
            sendErrorMessage(user.getUid(), "房间名称不能为空");
            return;
        }

        String roomId = generateRoomId(user.getUid());
        Room room = new Room(roomId, roomName, ConcurrentHashMap.newKeySet());
        room.getMembers().add(user.getUid());
        ROOM_MAP.put(roomId, room);

        // 发送创建成功响应
        Map<String, Object> response = createRoomResponse(roomId, roomName);
        sendToUser(user.getUid(), JSON.toJSONString(response));

        // 广播房间列表更新
        refreshRoomList();

        log.info("创建房间: {} -> {}", user.getName(), roomName);
    }

    private Map<String, Object> createRoomResponse(String roomId, String roomName) {
        Map<String, Object> response = new HashMap<>(4);
        response.put(TYPE_FIELD, "roomCreated");
        response.put(ROOM_ID, roomId);
        response.put(ROOM_NAME, roomName);
        response.put(MEMBERS, Collections.singletonList(roomId));
        return response;
    }

    private void handleJoinRoom(MsgVo msg, OnlineUser user) {
        String roomId = msg.getTo();
        if (roomId == null) {
            sendErrorMessage(user.getUid(), "房间ID不能为空");
            return;
        }

        Room room = ROOM_MAP.get(roomId);
        if (room == null) {
            sendErrorMessage(user.getUid(), "房间不存在");
            return;
        }

        // 检查是否已经在房间中
        if (room.getMembers().contains(user.getUid())) {
            sendErrorMessage(user.getUid(), "您已在房间中");
            return;
        }

        room.getMembers().add(user.getUid());

        // 发送加入成功响应
        Map<String, Object> response = createJoinRoomResponse(roomId, room);
        sendToUser(user.getUid(), JSON.toJSONString(response));

        // 通知房间内其他用户
        notifyRoomUsersAboutJoin(roomId, user.getName(), user.getUid());

        // 发送房间成员列表更新
        sendRoomMembersUpdate(roomId);

        // 更新房间列表
        refreshRoomList();

        log.info("用户加入房间: {} -> {}", user.getName(), room.getRoomName());
    }

    private Map<String, Object> createJoinRoomResponse(String roomId, Room room) {
        Map<String, Object> response = new HashMap<>(4);
        response.put(TYPE_FIELD, "roomJoined");
        response.put(ROOM_ID, roomId);
        response.put(ROOM_NAME, room.getRoomName());
        response.put(MEMBERS, new ArrayList<>(room.getMembers()));
        return response;
    }

    private void notifyRoomUsersAboutJoin(String roomId, String userName, String excludeUid) {
        Map<String, Object> notifyMsg = new HashMap<>(4);
        notifyMsg.put(TYPE_FIELD, "roomUserJoin");
        notifyMsg.put(ROOM_ID, roomId);
        notifyMsg.put("userName", userName);
        broadcastToRoomExclude(roomId, excludeUid, JSON.toJSONString(notifyMsg));
    }

    private void handleLeaveRoom(MsgVo msg, OnlineUser user) {
        String roomId = msg.getTo();
        if (roomId == null) {
            return;
        }

        Room room = ROOM_MAP.get(roomId);
        if (room == null) {
            return;
        }

        boolean removed = room.getMembers().remove(user.getUid());
        if (!removed) {
            return;
        }

        // 发送离开成功响应
        Map<String, Object> response = createLeaveRoomResponse(roomId, room);
        sendToUser(user.getUid(), JSON.toJSONString(response));

        // 通知房间内其他用户
        notifyRoomUsersAboutLeave(roomId, user.getName());

        // 如果房间为空，移除房间
        if (room.getMembers().isEmpty()) {
            ROOM_MAP.remove(roomId);
        } else {
            // 发送更新的成员列表
            sendRoomMembersUpdate(roomId);
        }

        // 更新房间列表
        refreshRoomList();

        log.info("用户离开房间: {} -> {}", user.getName(), room.getRoomName());
    }

    private Map<String, Object> createLeaveRoomResponse(String roomId, Room room) {
        Map<String, Object> response = new HashMap<>(4);
        response.put(TYPE_FIELD, "roomLeft");
        response.put(ROOM_ID, roomId);
        response.put(ROOM_NAME, room.getRoomName());
        return response;
    }

    private void notifyRoomUsersAboutLeave(String roomId, String userName) {
        Map<String, Object> notifyMsg = new HashMap<>(4);
        notifyMsg.put(TYPE_FIELD, "roomUserLeave");
        notifyMsg.put(ROOM_ID, roomId);
        notifyMsg.put("userName", userName);
        broadcastToRoom(roomId, JSON.toJSONString(notifyMsg));
    }

    private void handleGetUserList(OnlineUser user) {
        List<UserListVo> userList = new ArrayList<>();

        // 遍历账户映射，获取所有在线用户（去重）
        for (Map.Entry<String, Set<String>> entry : ACCOUNT_TO_UIDS.entrySet()) {
            String account = entry.getKey();
            Set<String> uids = entry.getValue();

            // 排除自己
            if (account.equals(user.getName())) {
                continue;
            }

            // 确保至少有一个有效的会话
            boolean hasValidSession = false;
            for (String uid : uids) {
                OnlineUser onlineUser = ONLINE_MAP.get(uid);
                if (onlineUser != null && onlineUser.isValid()) {
                    hasValidSession = true;
                    break;
                }
            }

            if (hasValidSession) {
                userList.add(new UserListVo(account, account));
            }
        }

        String json = JSON.toJSONString(new UserListDTO(userList));
        sendToUser(user.getUid(), json);
    }

    private void handleGetRoomList(OnlineUser user) {
        List<RoomListVo> roomList = new ArrayList<>();
        for (Room room : ROOM_MAP.values()) {
            roomList.add(new RoomListVo(
                    room.getRoomId(),
                    room.getRoomName(),
                    room.getMembers().size(),
                    new ArrayList<>(room.getMembers())
            ));
        }
        String json = JSON.toJSONString(new RoomListDTO(roomList));
        sendToUser(user.getUid(), json);
    }

    private void handlePing(OnlineUser user) {
        String pongMsg = buildMsg("pong", null, user.getUid(), "pong");
        sendToUser(user.getUid(), pongMsg);
    }

    // ==================== 工具方法 ====================

    private static boolean validateConnection(String uid, Session session) {
        if (!uid.contains(":")) {
            log.warn("无效的uid格式: {}", uid);
            closeSessionSafely(session, uid, "无效的用户标识格式");
            return false;
        }

        if (ACTIVE_CONNECTIONS.get() >= MAX_CONNECTIONS) {
            log.warn("连接数已达上限: {}", MAX_CONNECTIONS);
            closeSessionSafely(session, uid, "连接数已达上限");
            return false;
        }

        return true;
    }

    private static UserInfo parseUserInfo(String uid) {
        String[] parts = uid.split(":");
        if (parts.length < 2) {
            return null;
        }
        return new UserInfo(parts[0], parts[1]);
    }

    private static void configureSession(Session session) {
        session.setMaxIdleTimeout(MAX_SESSION_IDLE_TIMEOUT);
    }

    private static OnlineUser createAndRegisterUser(String uid, String account, Session session) {
        OnlineUser newUser = new OnlineUser(uid, account, session);

        OnlineUser oldUser = ONLINE_MAP.put(uid, newUser);
        if (oldUser != null) {
            oldUser.cleanup();
        } else {
            ACTIVE_CONNECTIONS.incrementAndGet();
        }
        TOTAL_CONNECTIONS.incrementAndGet();

        return newUser;
    }

    private static void updateAccountMapping(String account, String uid) {
        ACCOUNT_TO_UIDS.computeIfAbsent(account, k -> ConcurrentHashMap.newKeySet()).add(uid);
    }

    private static void removeFromAccountMapping(String account, String uid) {
        Set<String> uids = ACCOUNT_TO_UIDS.get(account);
        if (uids != null) {
            uids.remove(uid);
            if (uids.isEmpty()) {
                ACCOUNT_TO_UIDS.remove(account);
            }
        }
    }

    private void sendInitialMessages(Session session, String uid, String account) {
        // 发送用户信息
        Map<String, Object> selfInfo = new HashMap<>();
        selfInfo.put("type", "selfInfo");
        selfInfo.put("content", account);
        sendAsync(session, JSON.toJSONString(selfInfo));

        // 发送系统消息
        sendAsync(session, buildMsg("sys", null, null, "🎉 连接成功！欢迎使用现代聊天室"));
    }

    private static void closeSessionSafely(Session session, String uid, String reason) {
        if (session == null || !session.isOpen()) {
            return;
        }

        try {
            session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, reason));
        } catch (IOException e) {
            log.debug("关闭session异常, uid={}", uid, e);
        }
    }

    private void startHeartbeat(OnlineUser user) {
        if (user == null) {
            return;
        }

        ThreadPoolTaskScheduler scheduler = getHeartbeatScheduler();
        if (scheduler == null) {
            log.error("心跳调度器未初始化");
            return;
        }

        ScheduledFuture<?> future = scheduler.scheduleWithFixedDelay(() -> {
            try {
                performHeartbeatCheck(user);
            } catch (Exception e) {
                log.error("心跳任务异常", e);
            }
        }, Duration.ofSeconds(HEARTBEAT_INTERVAL_SECONDS));

        user.setPingFuture(future);
    }

    private void performHeartbeatCheck(OnlineUser user) throws IOException {
        String uid = user.getUid();

        // 检查连接是否还在
        if (!ONLINE_MAP.containsKey(uid)) {
            user.cancelPingFuture();
            return;
        }

        // 检查是否超时
        long inactiveTime = TimeUnit.MILLISECONDS.toSeconds(
                System.currentTimeMillis() - user.getLastActiveTime());

        if (inactiveTime > HEARTBEAT_TIMEOUT_SECONDS) {
            log.warn("心跳超时，移除用户: {}", uid);
            safeRemoveConnection(uid);
            return;
        }

        // 发送ping
        Session session = user.getSession();
        if (session != null && session.isOpen()) {
            session.getAsyncRemote().sendPing(ByteBuffer.wrap("PING".getBytes()));
        }
    }

    // ==================== 广播和发送方法 ====================

    private static void sendAsync(Session session, String message) {
        if (session == null || !session.isOpen() || message == null) {
            return;
        }

        ThreadPoolTaskExecutor executor = getMessageExecutor();
        if (executor == null) {
            log.error("消息执行器未初始化");
            return;
        }

        executor.execute(() -> {
            try {
                session.getAsyncRemote().sendText(message);
            } catch (Exception e) {
                log.debug("发送消息失败", e);
            }
        });
    }

    private static void sendToUser(String uid, String message) {
        OnlineUser user = ONLINE_MAP.get(uid);
        if (user != null && user.isValid()) {
            sendAsync(user.getSession(), message);
        }
    }

    private void sendErrorMessage(String uid, String errorMessage) {
        sendToUser(uid, buildMsg("error", null, uid, errorMessage));
    }

    private static void broadcastToAll(String message) {
        for (OnlineUser user : ONLINE_MAP.values()) {
            if (user.isValid()) {
                sendAsync(user.getSession(), message);
            }
        }
    }

    private static void broadcastToRoom(String roomId, String message) {
        Room room = ROOM_MAP.get(roomId);
        if (room == null) {
            return;
        }

        for (String uid : room.getMembers()) {
            sendToUser(uid, message);
        }
    }

    private static void broadcastToRoomExclude(String roomId, String excludeUid, String message) {
        Room room = ROOM_MAP.get(roomId);
        if (room == null) {
            return;
        }

        for (String uid : room.getMembers()) {
            if (!uid.equals(excludeUid)) {
                sendToUser(uid, message);
            }
        }
    }

    private void broadcastUserJoin(String userName) {
        String joinMsg = buildMsg("userJoin", null, null, userName);
        broadcastToAll(joinMsg);
    }

    private void broadcastUserLeave(String userName) {
        String leaveMsg = buildMsg("userLeave", null, null, userName);
        broadcastToAll(leaveMsg);
    }

    private static void refreshUserList() {
        List<UserListVo> userList = new ArrayList<>();

        // 遍历账户映射，获取所有在线用户
        for (Map.Entry<String, Set<String>> entry : ACCOUNT_TO_UIDS.entrySet()) {
            String account = entry.getKey();
            Set<String> uids = entry.getValue();

            // 确保至少有一个有效的会话
            boolean hasValidSession = false;
            for (String uid : uids) {
                OnlineUser user = ONLINE_MAP.get(uid);
                if (user != null && user.isValid()) {
                    hasValidSession = true;
                    break;
                }
            }

            if (hasValidSession) {
                userList.add(new UserListVo(account, account));
            }
        }

        String json = JSON.toJSONString(new UserListDTO(userList));
        broadcastToAll(json);
    }

    private static void refreshRoomList() {
        List<RoomListVo> roomList = new ArrayList<>();
        for (Room room : ROOM_MAP.values()) {
            roomList.add(new RoomListVo(
                    room.getRoomId(),
                    room.getRoomName(),
                    room.getMembers().size(),
                    new ArrayList<>(room.getMembers())
            ));
        }
        String json = JSON.toJSONString(new RoomListDTO(roomList));
        broadcastToAll(json);
    }

    private void refreshUserAndRoomLists() {
        refreshUserList();
        refreshRoomList();
    }

    private static void sendRoomMembersUpdate(String roomId) {
        Room room = ROOM_MAP.get(roomId);
        if (room == null) {
            return;
        }

        Map<String, Object> membersMsg = new HashMap<>(4);
        membersMsg.put(TYPE_FIELD, "roomMembers");
        membersMsg.put(ROOM_ID, roomId);
        membersMsg.put(MEMBERS, new ArrayList<>(room.getMembers()));
        membersMsg.put("memberCount", room.getMembers().size());

        String json = JSON.toJSONString(membersMsg);
        broadcastToRoom(roomId, json);
    }

    private static String buildMsg(String type, String from, String to, String content) {
        String safeContent = truncateMessage(content);
        MsgVo msg = new MsgVo(type, from, to, safeContent, System.currentTimeMillis());
        return JSON.toJSONString(msg);
    }

    private static String truncateMessage(String content) {
        if (content == null) {
            return "";
        }
        if (content.length() > MAX_MESSAGE_LENGTH) {
            return content.substring(0, MAX_MESSAGE_LENGTH);
        }
        return content;
    }

    private static String generateRoomId(String userId) {
        long hash = userId.hashCode() & 0xffffffffL;
        return ROOM_ID_PREFIX + System.currentTimeMillis() + "_" + hash;
    }

    // ==================== 线程池获取方法 ====================

    private static ThreadPoolTaskExecutor getMessageExecutor() {
        return MESSAGE_EXECUTOR_REF.get();
    }

    private static ThreadPoolTaskScheduler getHeartbeatScheduler() {
        return HEARTBEAT_SCHEDULER_REF.get();
    }

    // ==================== Spring 生命周期管理 ====================

    @Override
    public void destroy() {
        gracefulShutdown();
    }

    public static void gracefulShutdown() {
        log.info("开始优雅关闭现代WebSocket服务...");

        try {
            shutdownAllConnections();
        } catch (Exception e) {
            log.error("关闭WebSocket服务异常", e);
        }
    }

    private static void shutdownAllConnections() {
        int count = ONLINE_MAP.size();
        List<OnlineUser> users = new ArrayList<>(ONLINE_MAP.values());

        for (OnlineUser user : users) {
            try {
                user.cleanup();
            } catch (Exception e) {
                log.warn("关闭连接异常", e);
            }
        }

        ONLINE_MAP.clear();
        ROOM_MAP.clear();
        CLEANING_MAP.clear();
        ACCOUNT_TO_UIDS.clear();
        ACTIVE_CONNECTIONS.set(0);

        log.info("现代WebSocket服务关闭完成，已关闭 {} 个连接", count);
    }

    private static void leaveAllRooms(String uid) {
        Iterator<Map.Entry<String, Room>> iterator = ROOM_MAP.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Room> entry = iterator.next();
            Room room = entry.getValue();
            boolean removed = room.getMembers().remove(uid);
            if (removed) {
                log.debug("用户 {} 离开房间 {}", uid, room.getRoomName());
                if (room.getMembers().isEmpty()) {
                    iterator.remove();
                    log.debug("房间 {} 已空，移除", room.getRoomName());
                }
            }
        }
    }

    // ==================== 监控和统计方法 ====================

    /**
     * 获取服务状态信息
     */
    public static Map<String, Object> getServiceStatus() {
        Map<String, Object> status = new LinkedHashMap<>();

        status.put("activeConnections", ACTIVE_CONNECTIONS.get());
        status.put("totalConnections", TOTAL_CONNECTIONS.get());
        status.put("roomCount", ROOM_MAP.size());
        status.put("cleaningMapSize", CLEANING_MAP.size());
        status.put("accountCount", ACCOUNT_TO_UIDS.size());
        status.put("initialized", INITIALIZED.get());

        ThreadPoolTaskExecutor messageExecutor = getMessageExecutor();
        if (messageExecutor != null) {
            status.put("messageExecutorActiveCount", messageExecutor.getActiveCount());
            status.put("messageExecutorPoolSize", messageExecutor.getPoolSize());
        }

        addMemoryInfo(status);

        return status;
    }

    private static void addMemoryInfo(Map<String, Object> status) {
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024;
        long maxMemory = runtime.maxMemory() / 1024 / 1024;
        status.put("memoryUsedMB", usedMemory);
        status.put("memoryMaxMB", maxMemory);
    }

    // ==================== 内部辅助类 ====================

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

        public UserListDTO(List<UserListVo> list) {
            this.type = "userList";
            this.list = list;
        }
    }

    @Data
    @AllArgsConstructor
    public static class RoomListVo {
        private String id;
        private String name;
        private int memberCount;
        private List<String> members;
    }

    @Data
    @AllArgsConstructor
    public static class RoomListDTO {
        private String type;
        private List<RoomListVo> list;

        public RoomListDTO(List<RoomListVo> list) {
            this.type = "roomList";
            this.list = list;
        }
    }

    @Data
    private static final class UserInfo {
        private final String account;
        private final String sessionId;

        private UserInfo(String account, String sessionId) {
            this.account = account;
            this.sessionId = sessionId;
        }
    }
}