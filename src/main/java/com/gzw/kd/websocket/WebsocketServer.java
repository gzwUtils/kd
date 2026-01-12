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
import java.util.concurrent.locks.StampedLock;

@SuppressWarnings("unused")
@ServerEndpoint("/websocket/{uid}")
@Component
@Slf4j
public class WebsocketServer implements DisposableBean {

    // ===== 配置参数 =====
    private static final int MAX_CONNECTIONS = 1000;
    private static final int HEARTBEAT_INTERVAL_SECONDS = 30;
    private static final int HEARTBEAT_TIMEOUT_SECONDS = 90;
    private static final int CLEANUP_INTERVAL_SECONDS = 60;
    private static final long MAX_SESSION_IDLE_TIMEOUT = 300000L; // 5分钟
    private static final int MAX_TEXT_MESSAGE_SIZE = 8192; // 8KB
    private static final int MAX_BINARY_MESSAGE_SIZE = 8192;

    // ===== 连接管理 =====
    private static final ConcurrentMap<String, OnlineUser> ONLINE_MAP = new ConcurrentHashMap<>(512);
    private static final ConcurrentMap<String, Room> ROOM_MAP = new ConcurrentHashMap<>(64);
    private static final ConcurrentMap<String, Boolean> CLEANING_MAP = new ConcurrentHashMap<>(128);

    // ===== 线程池引用 =====
    private static final AtomicReference<ThreadPoolTaskScheduler> HEARTBEAT_SCHEDULER_REF =
            new AtomicReference<>();
    private static final AtomicReference<ThreadPoolTaskScheduler> CLEANUP_SCHEDULER_REF =
            new AtomicReference<>();
    private static final AtomicReference<ThreadPoolTaskExecutor> MESSAGE_EXECUTOR_REF =
            new AtomicReference<>();

    // ===== 统计和锁 =====
    private static final AtomicInteger ACTIVE_CONNECTIONS = new AtomicInteger(0);
    private static final AtomicInteger TOTAL_CONNECTIONS = new AtomicInteger(0);
    private static final StampedLock CONNECTION_LOCK = new StampedLock();

    // 初始化标志
    private static final AtomicBoolean INITIALIZED = new AtomicBoolean(false);

    // ===== Spring依赖注入 =====

    @Autowired
    @Lazy
    public void setHeartbeatScheduler(ThreadPoolTaskScheduler heartbeatScheduler) {
        HEARTBEAT_SCHEDULER_REF.set(heartbeatScheduler);
        initIfReady();
    }

    @Autowired
    @Lazy
    public void setCleanupScheduler(ThreadPoolTaskScheduler cleanupScheduler) {
        CLEANUP_SCHEDULER_REF.set(cleanupScheduler);
        initIfReady();
    }

    @Autowired
    @Lazy
    public void setMessageExecutor(ThreadPoolTaskExecutor messageExecutor) {
        MESSAGE_EXECUTOR_REF.set(messageExecutor);
        initIfReady();
    }

    /**
     * 初始化定时任务
     */
    private static void initIfReady() {
        ThreadPoolTaskScheduler heartbeatScheduler = HEARTBEAT_SCHEDULER_REF.get();
        ThreadPoolTaskScheduler cleanupScheduler = CLEANUP_SCHEDULER_REF.get();
        ThreadPoolTaskExecutor messageExecutor = MESSAGE_EXECUTOR_REF.get();

        if (heartbeatScheduler == null || cleanupScheduler == null || messageExecutor == null) {
            return;
        }

        if (INITIALIZED.compareAndSet(false, true)) {
            try {
                // 1. 定期清理无响应连接
                cleanupScheduler.scheduleWithFixedDelay(
                        WebsocketServer::cleanupInactiveConnections,
                        Duration.ofSeconds(CLEANUP_INTERVAL_SECONDS)
                );

                // 2. 定期打印统计信息
                cleanupScheduler.scheduleWithFixedDelay(
                        WebsocketServer::printStatistics,
                        Duration.ofSeconds(60)
                );

                log.info("WebSocket服务初始化完成");

            } catch (Exception e) {
                INITIALIZED.set(false);
                log.error("WebSocket服务初始化失败", e);
            }
        }
    }

    /**
     * 获取心跳调度器
     */
    private static ThreadPoolTaskScheduler getHeartbeatScheduler() {
        ThreadPoolTaskScheduler scheduler = HEARTBEAT_SCHEDULER_REF.get();
        if (scheduler == null) {
            throw new IllegalStateException("心跳调度器尚未初始化");
        }
        return scheduler;
    }

    /**
     * 获取清理调度器
     */
    private static ThreadPoolTaskScheduler getCleanupScheduler() {
        ThreadPoolTaskScheduler scheduler = CLEANUP_SCHEDULER_REF.get();
        if (scheduler == null) {
            throw new IllegalStateException("清理调度器尚未初始化");
        }
        return scheduler;
    }

    /**
     * 获取消息执行器
     */
    private static ThreadPoolTaskExecutor getMessageExecutor() {
        ThreadPoolTaskExecutor executor = MESSAGE_EXECUTOR_REF.get();
        if (executor == null) {
            throw new IllegalStateException("消息执行器尚未初始化");
        }
        return executor;
    }

    /**
     * 安全的连接移除方法
     */
    private static void safeRemoveConnection(String uid) {
        if (uid == null) {
            return;
        }

        // 使用CAS操作确保同一uid只被清理一次
        if (CLEANING_MAP.putIfAbsent(uid, Boolean.TRUE) != null) {
            return; // 已经在清理中
        }

        try {
            long stamp = CONNECTION_LOCK.writeLock();
            try {
                OnlineUser user = ONLINE_MAP.remove(uid);
                if (user != null) {
                    // 离开所有房间
                    leaveAllRooms(uid);

                    // 清理用户数据（会取消心跳任务和关闭session）
                    user.cleanup();

                    // 更新统计
                    ACTIVE_CONNECTIONS.decrementAndGet();

                    log.debug("连接移除完成: {}, 剩余活跃连接: {}", uid, ACTIVE_CONNECTIONS.get());
                }
            } finally {
                CONNECTION_LOCK.unlockWrite(stamp);
            }
        } catch (Exception e) {
            log.error("移除连接异常 uid={}: {}", uid, e.getMessage());
        } finally {
            CLEANING_MAP.remove(uid); // 清理完成
        }
    }

    /**
     * 安全关闭session
     */
    private static void closeSessionSafely(Session session, String uid) {
        if (session == null || !session.isOpen()) {
            return;
        }

        try {
            session.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "连接关闭"));
        } catch (IOException e) {
            log.debug("关闭session时发生IO异常: {}", e.getMessage());
        } catch (Exception e) {
            log.error("关闭session异常 uid={}: {}", uid, e.getMessage(), e);
        }
    }

    /**
     * 离开所有房间
     */
    private static void leaveAllRooms(String uid) {
        ROOM_MAP.entrySet().removeIf(entry -> {
            Room room = entry.getValue();
            boolean removed = room.getMembers().remove(uid);
            // 如果房间为空，移除房间
            return removed && room.getMembers().isEmpty();
        });
    }

    /**
     * 异步发送消息
     */
    private static void sendAsync(Session session, String message) {
        if (session == null || !session.isOpen() || message == null) {
            return;
        }

        getMessageExecutor().submit(() -> {
            try {
                session.getAsyncRemote().sendText(message);
            } catch (Exception e) {
                log.debug("异步发送消息失败: {}", e.getMessage());
            }
        });
    }

    /**
     * 智能广播（带连接数控制）
     */
    private static void smartBroadcast(String message) {
        if (message == null || message.isEmpty()) {
            return;
        }

        long stamp = CONNECTION_LOCK.tryOptimisticRead();
        int activeCount = ACTIVE_CONNECTIONS.get();

        if (!CONNECTION_LOCK.validate(stamp)) {
            stamp = CONNECTION_LOCK.readLock();
            try {
                activeCount = ACTIVE_CONNECTIONS.get();
            } finally {
                CONNECTION_LOCK.unlockRead(stamp);
            }
        }

        // 根据连接数选择广播策略
        if (activeCount <= 100) {
            // 小规模直接广播
            ONLINE_MAP.values().parallelStream()
                    .limit(100)
                    .forEach(user -> {
                        Session session = user.getSession();
                        if (session != null && session.isOpen()) {
                            sendAsync(session, message);
                        }
                    });
        } else {
            // 大规模抽样广播
            ONLINE_MAP.values().stream()
                    .filter(user -> System.currentTimeMillis() % 3 == 0) // 抽样1/3
                    .limit(100)
                    .forEach(user -> {
                        Session session = user.getSession();
                        if (session != null && session.isOpen()) {
                            sendAsync(session, message);
                        }
                    });
        }
    }

    /**
     * 刷新用户列表
     */
    private static void refreshUserList() {
        List<UserListVo> userList = new ArrayList<>(100);

        long stamp = CONNECTION_LOCK.readLock();
        try {
            ONLINE_MAP.values().stream()
                    .limit(100)
                    .map(user -> new UserListVo(user.getUid(), user.getName()))
                    .forEach(userList::add);
        } finally {
            CONNECTION_LOCK.unlockRead(stamp);
        }

        String json = JSON.toJSONString(new UserListDTO(userList));
        smartBroadcast(json);
    }

    /**
     * 清理无响应连接
     */
    private static void cleanupInactiveConnections() {
        try {
            long now = System.currentTimeMillis();
            List<String> toRemove = new ArrayList<>();

            // 第一步：收集需要清理的连接
            long stamp = CONNECTION_LOCK.readLock();
            try {
                ONLINE_MAP.forEach((uid, user) -> {
                    long inactiveTime = TimeUnit.MILLISECONDS.toSeconds(now - user.getLastActiveTime());
                    if (inactiveTime > HEARTBEAT_TIMEOUT_SECONDS * 2) {
                        toRemove.add(uid);
                    }
                });
            } finally {
                CONNECTION_LOCK.unlockRead(stamp);
            }

            // 第二步：批量清理
            if (!toRemove.isEmpty()) {
                toRemove.forEach(WebsocketServer::safeRemoveConnection);
                log.info("清理完成，移除 {} 个无响应连接", toRemove.size());
            }

        } catch (Exception e) {
            log.error("清理连接异常: {}", e.getMessage());
        }
    }

    /**
     * 打印统计信息
     */
    private static void printStatistics() {
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024;
        long maxMemory = runtime.maxMemory() / 1024 / 1024;

        log.info("WebSocket统计 - 活跃连接: {}, 总连接: {}, 房间数: {}, 内存使用: {}MB/{}MB ({}%)",
                ACTIVE_CONNECTIONS.get(),
                TOTAL_CONNECTIONS.get(),
                ROOM_MAP.size(),
                usedMemory,
                maxMemory,
                maxMemory > 0 ? (usedMemory * 100 / maxMemory) : 0);
    }

    // ===== 生命周期方法 =====

    @OnOpen
    public void onOpen(Session session, @PathParam("uid") String uid) {
        if (uid == null || session == null) {
            return;
        }

        String clientIp = getClientIp(session);
        long startTime = System.currentTimeMillis();

        try {
            // 1. 验证uid格式
            if (!uid.contains(":")) {
                log.warn("无效的uid格式: {}", uid);
                closeSessionSafely(session, uid);
                return;
            }

            // 2. 检查连接限制
            if (ACTIVE_CONNECTIONS.get() >= MAX_CONNECTIONS) {
                log.warn("连接数已达上限: {}, 拒绝: {}", MAX_CONNECTIONS, uid);
                closeSessionSafely(session, uid);
                return;
            }

            // 3. 配置session
            session.setMaxIdleTimeout(MAX_SESSION_IDLE_TIMEOUT);
            session.setMaxTextMessageBufferSize(MAX_TEXT_MESSAGE_SIZE);
            session.setMaxBinaryMessageBufferSize(MAX_BINARY_MESSAGE_SIZE);

            // 4. 处理连接
            String name = extractName(uid);
            OnlineUser newUser = new OnlineUser(uid, name, session);

            long stamp = CONNECTION_LOCK.writeLock();
            try {
                // 移除旧连接
                OnlineUser oldUser = ONLINE_MAP.remove(uid);
                if (oldUser != null) {
                    safeRemoveConnection(uid);
                }

                // 添加新连接
                ONLINE_MAP.put(uid, newUser);
                ACTIVE_CONNECTIONS.incrementAndGet();
                TOTAL_CONNECTIONS.incrementAndGet();

                log.info("用户连接成功: {}, IP: {}, 活跃连接: {}",
                        name, clientIp, ACTIVE_CONNECTIONS.get());

            } finally {
                CONNECTION_LOCK.unlockWrite(stamp);
            }

            // 5. 启动心跳
            startHeartbeat(newUser);

            // 6. 发送初始消息
            sendAsync(session, buildMsg("selfInfo", null, uid, name));
            sendAsync(session, buildMsg("sys", null, null, "连接成功"));

            // 7. 异步广播通知
            getMessageExecutor().submit(() -> {
                smartBroadcast(buildMsg("sys", null, null, name + " 加入了群聊"));
                refreshUserList();
            });

            log.debug("连接处理完成，耗时: {}ms", System.currentTimeMillis() - startTime);

        } catch (Exception e) {
            log.error("处理连接异常 uid={}, IP={}: {}", uid, clientIp, e.getMessage());
            safeRemoveConnection(uid);
        }
    }

    @OnClose
    public void onClose(@PathParam("uid") String uid) {
        if (uid == null) {
            return;
        }

        log.info("连接关闭: {}", uid);
        safeRemoveConnection(uid);
    }

    @OnError
    public void onError(@PathParam("uid") String uid, Throwable error) {
        if (uid == null) {
            return;
        }

        log.error("WebSocket错误 uid={}: {}", uid, error.getMessage());
        safeRemoveConnection(uid);
    }

    @OnMessage
    public void onMessage(String message, @PathParam("uid") String uid) {
        if (uid == null || message == null || message.isEmpty()) {
            return;
        }

        getMessageExecutor().submit(() -> {
            try {
                // 更新活跃时间
                OnlineUser user = ONLINE_MAP.get(uid);
                if (user != null) {
                    user.updateActiveTime();
                }

                // 解析消息
                MsgVo vo = JSON.parseObject(message, MsgVo.class);
                if (vo == null || vo.getType() == null) {
                    return;
                }

                // 处理消息
                processMessage(vo, user);

            } catch (Exception e) {
                log.error("处理消息异常 uid={}: {}", uid, e.getMessage());
            }
        });
    }

    @OnMessage
    public void onPong(PongMessage pongMessage, @PathParam("uid") String uid) {
        OnlineUser user = ONLINE_MAP.get(uid);
        if (user != null) {
            user.updateActiveTime();
            log.trace("收到pong响应: {}", uid);
        }
    }

    // ===== 心跳管理 =====

    /**
     * 启动心跳检测
     */
    private void startHeartbeat(OnlineUser user) {
        if (user == null || user.getSession() == null) {
            return;
        }

        // 创建心跳任务
        ScheduledFuture<?> future = getHeartbeatScheduler().scheduleWithFixedDelay(() -> {
            String uid = user.getUid();

            try {
                // 检查连接状态
                if (!ONLINE_MAP.containsKey(uid)) {
                    log.debug("用户已不在线，停止心跳: {}", uid);
                    user.cancelPingFuture();
                    return;
                }

                // 检查session状态
                if (!user.isValid()) {
                    log.debug("session已关闭，停止心跳: {}", uid);
                    safeRemoveConnection(uid);
                    return;
                }

                // 检查无响应超时
                long inactiveTime = TimeUnit.MILLISECONDS.toSeconds(
                        System.currentTimeMillis() - user.getLastActiveTime());

                if (inactiveTime > HEARTBEAT_TIMEOUT_SECONDS) {
                    log.warn("心跳超时，移除用户: {}, 无响应时间: {}s", uid, inactiveTime);
                    safeRemoveConnection(uid);
                    return;
                }

                // 发送ping
                Session session = user.getSession();
                if (session != null && session.isOpen()) {
                    ping(session, uid);
                }

            } catch (Exception e) {
                log.error("心跳任务异常 uid={}: {}", uid, e.getMessage());
                safeRemoveConnection(uid);
            }
        }, Duration.ofSeconds(HEARTBEAT_INTERVAL_SECONDS));

        // 设置心跳任务
        user.setPingFuture(future);
    }

    private static void ping(Session session, String uid) {
        try {
            byte[] pingData = "PING".getBytes();
            session.getAsyncRemote().sendPing(ByteBuffer.wrap(pingData));
            log.trace("发送ping: {}", uid);
        } catch (Exception e) {
            log.debug("发送ping失败 uid={}: {}", uid, e.getMessage());
            safeRemoveConnection(uid);
        }
    }

    // ===== 消息处理 =====

    /**
     * 处理消息
     */
    private void processMessage(MsgVo vo, OnlineUser user) {
        if (user == null || vo == null) {
            return;
        }

        switch (vo.getType()) {
            case "group":
                smartBroadcast(buildMsg("group", user.getName(), null, vo.getContent()));
                break;

            case "private":
                if (vo.getTo() != null) {
                    String privateMsg = buildMsg("private", user.getName(), vo.getTo(), vo.getContent());
                    sendToUser(vo.getTo(), privateMsg);
                    Session session = user.getSession();
                    if (session != null && session.isOpen()) {
                        sendAsync(session, privateMsg);
                    }
                }
                break;

            case "createRoom":
                String roomId = createRoom(vo.getContent(), user.getUid());
                Session session = user.getSession();
                if (session != null && session.isOpen()) {
                    sendAsync(session, buildMsg("selfRoom", null, user.getUid(), roomId));
                }
                getMessageExecutor().submit(WebsocketServer::refreshUserList);
                break;

            case "joinRoom":
                joinRoom(vo, user);
                break;

            case "room":
                broadcastToRoom(vo.getTo(), buildMsg("room", user.getName(), vo.getTo(), vo.getContent()));
                break;

            case "ping":
                Session pingSession = user.getSession();
                if (pingSession != null && pingSession.isOpen()) {
                    sendAsync(pingSession, buildMsg("pong", null, user.getUid(), "pong"));
                }
                break;

            default:
                log.warn("未知消息类型: {}", vo.getType());
        }
    }

    private void joinRoom(MsgVo vo, OnlineUser user) {
        boolean joined = joinRoom(vo.getTo(), user.getUid());
        Session userSession = user.getSession();
        if (userSession != null && userSession.isOpen()) {
            sendAsync(userSession, buildMsg(
                    joined ? "sys" : "error",
                    null, user.getUid(),
                    joined ? "已加入房间" : "房间不存在"
            ));
        }
        if (joined) {
            getMessageExecutor().submit(WebsocketServer::refreshUserList);
        }
    }

    private static void sendToUser(String uid, String message) {
        OnlineUser targetUser = ONLINE_MAP.get(uid);
        if (targetUser != null) {
            Session session = targetUser.getSession();
            if (session != null && session.isOpen()) {
                sendAsync(session, message);
            }
        }
    }

    // ===== 房间管理 =====

    /**
     * 广播到房间
     */
    private void broadcastToRoom(String roomId, String message) {
        Room room = ROOM_MAP.get(roomId);
        if (room != null) {
            room.getMembers().forEach(uid -> sendToUser(uid, message));
        }
    }

    /**
     * 创建房间
     */
    private String createRoom(String roomName, String creatorUid) {
        String roomId = "room_" + System.currentTimeMillis() + "_" + creatorUid.hashCode();
        Room room = new Room(roomId, roomName, ConcurrentHashMap.newKeySet());
        room.getMembers().add(creatorUid);
        ROOM_MAP.put(roomId, room);
        log.info("创建房间: {}, 创建者: {}", roomName, creatorUid);
        return roomId;
    }

    /**
     * 加入房间
     */
    private boolean joinRoom(String roomId, String uid) {
        Room room = ROOM_MAP.get(roomId);
        if (room == null) {
            return false;
        }
        room.getMembers().add(uid);
        log.info("用户加入房间: {}, 房间: {}", uid, roomId);
        return true;
    }

    // ===== 辅助方法 =====

    private static String buildMsg(String type, String from, String to, String content) {
        try {
            MsgVo msgVo = new MsgVo(type, from, to,
                    content != null ? content.substring(0, Math.min(content.length(), 1000)) : "",
                    System.currentTimeMillis());
            return JSON.toJSONString(msgVo);
        } catch (Exception e) {
            log.error("构建消息失败: {}", e.getMessage());
            return "{\"type\":\"error\",\"content\":\"消息格式错误\"}";
        }
    }

    private String extractName(String uid) {
        int colonIndex = uid.indexOf(':');
        return colonIndex > 0 ? uid.substring(0, colonIndex) : "未知用户";
    }

    private String getClientIp(Session session) {
        try {
            Map<String, List<String>> headers = session.getRequestParameterMap();
            if (headers != null && headers.containsKey("X-Real-IP")) {
                return headers.get("X-Real-IP").get(0);
            }
        } catch (Exception e) {
            // 忽略
        }
        return "未知IP";
    }

    // ===== Spring生命周期管理 =====

    @Override
    public void destroy() throws Exception {
        gracefulShutdown();
    }

    /**
     * 优雅关闭
     */
    public static void gracefulShutdown() {
        if (!INITIALIZED.get()) {
            return;
        }

        log.info("开始优雅关闭WebSocket服务...");

        try {
            // 1. 停止接收新连接
            long stamp = CONNECTION_LOCK.writeLock();
            try {
                // 2. 关闭所有连接
                int count = ONLINE_MAP.size();
                List<OnlineUser> users = new ArrayList<>(ONLINE_MAP.values());

                users.forEach(user -> {
                    try {
                        // 清理用户数据（会取消心跳任务和关闭session）
                        user.cleanup();
                    } catch (Exception e) {
                        log.warn("关闭连接异常 uid={}: {}", user.getUid(), e.getMessage());
                    }
                });

                ONLINE_MAP.clear();
                ROOM_MAP.clear();
                CLEANING_MAP.clear();
                ACTIVE_CONNECTIONS.set(0);
                TOTAL_CONNECTIONS.set(0);

                log.info("已关闭 {} 个连接", count);

            } finally {
                CONNECTION_LOCK.unlockWrite(stamp);
            }

            log.info("WebSocket服务关闭完成");

        } catch (Exception e) {
            log.error("关闭WebSocket服务异常", e);
        }
    }

    // ===== 监控方法 =====

    /**
     * 获取WebSocket服务状态
     */
    public static Map<String, Object> getServiceStatus() {
        Map<String, Object> status = new LinkedHashMap<>();

        status.put("activeConnections", ACTIVE_CONNECTIONS.get());
        status.put("totalConnections", TOTAL_CONNECTIONS.get());
        status.put("roomCount", ROOM_MAP.size());
        status.put("cleaningMapSize", CLEANING_MAP.size());
        status.put("initialized", INITIALIZED.get());

        ThreadPoolTaskScheduler heartbeatScheduler = HEARTBEAT_SCHEDULER_REF.get();
        if (heartbeatScheduler != null) {
            status.put("heartbeatActiveThreads", heartbeatScheduler.getActiveCount());
        }

        ThreadPoolTaskExecutor messageExecutor = MESSAGE_EXECUTOR_REF.get();
        if (messageExecutor != null) {
            status.put("messageActiveThreads", messageExecutor.getActiveCount());
            status.put("messageQueueSize", messageExecutor.getThreadPoolExecutor().getQueue().size());
        }

        Runtime runtime = Runtime.getRuntime();
        long usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024;
        long maxMemory = runtime.maxMemory() / 1024 / 1024;
        status.put("memoryUsedMB", usedMemory);
        status.put("memoryMaxMB", maxMemory);

        return status;
    }

    // ===== DTO类 =====

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
            this.list = list;
            this.type = "userList";
        }
    }
}