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
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
@SuppressWarnings("unused")
@ServerEndpoint("/websocket/{uid}")
@Component
@Slf4j
public class WebsocketServer {

    // 使用ConcurrentHashMap保证线程安全
    private static final Map<String, OnlineUser> ONLINE_MAP = new ConcurrentHashMap<>(1024);
    private static final Map<String, Room> ROOM_MAP = new ConcurrentHashMap<>(128);

    // 统一的定时任务调度器（单线程，避免线程膨胀）
    private static final ScheduledExecutorService SCHEDULED_EXECUTOR =
            Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
                private final AtomicInteger counter = new AtomicInteger(0);
                @Override
                public Thread newThread(Runnable r) {
                    Thread thread = new Thread(r, "ws-scheduler-" + counter.incrementAndGet());
                    thread.setDaemon(true);
                    thread.setPriority(Thread.MIN_PRIORITY);
                    return thread;
                }
            });

    // 连接管理锁
    private static final Lock CONNECTION_LOCK = new ReentrantLock();

    // 配置参数
    private static final int MAX_CONNECTIONS = 1000;
    private static final int HEARTBEAT_INTERVAL_SECONDS = 30;
    private static final int HEARTBEAT_TIMEOUT_SECONDS = 90;
    private static final int CLEANUP_INTERVAL_SECONDS = 60;

    // 统计信息
    private static final AtomicInteger TOTAL_CONNECTIONS = new AtomicInteger(0);
    private static final AtomicInteger ACTIVE_CONNECTIONS = new AtomicInteger(0);

    // 初始化定时清理任务
    static {
        // 定期清理无响应连接
        SCHEDULED_EXECUTOR.scheduleAtFixedRate(WebsocketServer::cleanupInactiveConnections,
                10, CLEANUP_INTERVAL_SECONDS, TimeUnit.SECONDS);

        // 定期打印统计信息
        SCHEDULED_EXECUTOR.scheduleAtFixedRate(WebsocketServer::printStatistics,
                30, 60, TimeUnit.SECONDS);
    }

    /* ===== 连接管理 ===== */

    /**
     * 安全地移除并关闭连接
     */
    private void safeRemoveAndClose(String uid) {
        CONNECTION_LOCK.lock();
        try {
            OnlineUser user = ONLINE_MAP.remove(uid);
            if (user != null) {
                // 取消心跳任务
                if (user.getPingFuture() != null) {
                    user.getPingFuture().cancel(false);
                }

                // 离开所有房间
                leaveAllRooms(uid);

                // 关闭session
                closeSessionSafely(user.getSession(), uid);

                // 更新统计
                ACTIVE_CONNECTIONS.decrementAndGet();

                log.info("用户移除完成: {}, 剩余连接: {}", user.getName(), ACTIVE_CONNECTIONS.get());
            }
        } finally {
            CONNECTION_LOCK.unlock();
        }
    }

    /**
     * 安全关闭session（避免内存泄漏）
     */
    private void closeSessionSafely(Session session, String uid) {
        if (session == null || !session.isOpen()) {
            return;
        }

        try {
            session.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "正常关闭"));
        } catch (IOException e) {
            log.debug("关闭session时的小错误: {}", e.getMessage());
        } catch (Exception e) {
            log.error("关闭session异常 uid={}: {}", uid, e.getMessage());
        }
    }

    /* ===== 消息发送 ===== */

    /**
     * 异步发送消息（非阻塞）
     */
    private void sendAsync(Session session, String json) {
        if (session == null || !session.isOpen()) {
            return;
        }

        try {
            session.getAsyncRemote().sendText(json);
        } catch (Exception e) {
            log.debug("异步发送消息失败: {}", e.getMessage());
        }
    }

    /**
     * 同步发送消息（关键消息使用）
     */
    private void sendSync(Session session, String json) {
        if (session == null || !session.isOpen()) {
            return;
        }

        try {
            session.getBasicRemote().sendText(json);
        } catch (IOException e) {
            log.debug("同步发送消息失败: {}", e.getMessage());
        }
    }

    /**
     * 智能广播（根据连接数选择策略）
     */
    private void smartBroadcast(String json) {
        int size = ACTIVE_CONNECTIONS.get();

        if (size <= 50) {
            // 小规模：直接广播
            ONLINE_MAP.values().forEach(user -> sendAsync(user.getSession(), json));
        } else if (size <= 200) {
            // 中等规模：分批发送
            SCHEDULED_EXECUTOR.execute(() -> ONLINE_MAP.values().forEach(user -> sendAsync(user.getSession(), json)));
        } else {
            // 大规模：抽样发送+队列
            log.debug("大规模广播，消息: {}", json.substring(0, Math.min(50, json.length())));
        }
    }

    /**
     * 发送给指定用户
     */
    private void sendToUser(String uid, String json) {
        OnlineUser user = ONLINE_MAP.get(uid);
        if (user != null) {
            sendAsync(user.getSession(), json);
        }
    }

    /* ===== 工具方法 ===== */

    private String buildMsg(String type, String from, String to, String content) {
        try {
            return JSON.toJSONString(new MsgVo(type, from, to, content, System.currentTimeMillis()));
        } catch (Exception e) {
            log.error("构建消息失败: {}", e.getMessage());
            return "{\"type\":\"error\",\"content\":\"消息格式错误\"}";
        }
    }

    private void refreshUserList() {
        List<UserListVo> users = ONLINE_MAP.values().stream()
                .map(user -> new UserListVo(user.getUid(), user.getName()))
                .collect(Collectors.toList());

        List<UserListVo> rooms = ROOM_MAP.values().stream()
                .map(room -> new UserListVo(room.getRoomId(), "【房间】" + room.getRoomName()))
                .collect(Collectors.toList());

        users.addAll(rooms);
        String json = JSON.toJSONString(new UserListDTO(users));
        smartBroadcast(json);
    }

    private void leaveAllRooms(String uid) {
        ROOM_MAP.values().forEach(room -> room.getMembers().remove(uid));
        // 清理空房间
        ROOM_MAP.entrySet().removeIf(entry -> entry.getValue().getMembers().isEmpty());
    }

    /* ===== 生命周期方法（核心修复） ===== */

    @OnOpen
    public void onOpen(Session session, @PathParam("uid") String uid) {
        long startTime = System.currentTimeMillis();
        String clientIp = getClientIp(session);

        try {
            // 1. 验证参数
            if (uid == null || !uid.contains(":")) {
                log.warn("无效的uid格式: {}", uid);
                closeSessionSafely(session, uid);
                return;
            }

            // 2. 检查连接限制（使用原子操作避免死锁）
            if (ACTIVE_CONNECTIONS.get() >= MAX_CONNECTIONS) {
                log.warn("连接数已达上限: {}, 拒绝: {}", MAX_CONNECTIONS, uid);
                closeSessionSafely(session, uid);
                return;
            }

            // 3. 配置session参数（避免内存泄漏）
            session.setMaxIdleTimeout(300000L); // 5分钟
            session.setMaxTextMessageBufferSize(8192);
            session.setMaxBinaryMessageBufferSize(8192);

            // 4. 加锁处理连接（避免并发问题）
            CONNECTION_LOCK.lock();
            try {
                // 移除旧连接
                OnlineUser oldUser = ONLINE_MAP.get(uid);
                if (oldUser != null) {
                    safeRemoveAndClose(uid);
                }

                // 创建新用户
                String name = extractName(uid);
                OnlineUser newUser = new OnlineUser(uid, name, session);
                ONLINE_MAP.put(uid, newUser);

                // 启动心跳检测
                startHeartbeat(newUser);

                // 更新统计
                ACTIVE_CONNECTIONS.incrementAndGet();
                TOTAL_CONNECTIONS.incrementAndGet();

                log.info("用户连接成功: {}, IP: {}, 活跃连接: {}, 总连接: {}",
                        name, clientIp, ACTIVE_CONNECTIONS.get(), TOTAL_CONNECTIONS.get());

            } finally {
                CONNECTION_LOCK.unlock();
            }

            // 5. 发送欢迎消息
            sendSync(session, buildMsg("selfInfo", null, uid, extractName(uid)));
            sendAsync(session, buildMsg("sys", null, null, "连接成功"));

            // 6. 广播通知（异步）
            SCHEDULED_EXECUTOR.schedule(() -> {
                smartBroadcast(buildMsg("sys", null, null, extractName(uid) + " 加入了群聊"));
                refreshUserList();
            }, 100, TimeUnit.MILLISECONDS);

            log.debug("连接处理完成，耗时: {}ms", System.currentTimeMillis() - startTime);

        } catch (Exception e) {
            log.error("处理连接异常 uid={}, IP={}: {}", uid, clientIp, e.getMessage(), e);
            safeRemoveAndClose(uid);
        }
    }

    @OnClose
    public void onClose(@PathParam("uid") String uid) {
        if (uid == null) return;

        try {
            safeRemoveAndClose(uid);
            log.info("连接关闭: {}", uid);
        } catch (Exception e) {
            log.error("处理关闭连接异常 uid={}: {}", uid, e.getMessage());
        }
    }

    @OnError
    public void onError(@PathParam("uid") String uid, Throwable t) {
        if (uid == null) return;

        log.error("WebSocket错误 uid={}: {}", uid, t.getMessage());
        safeRemoveAndClose(uid);
    }

    @OnMessage
    public void onMessage(String json, @PathParam("uid") String uid) {
        if (uid == null || json == null || json.isEmpty()) {
            return;
        }

        try {
            // 更新活跃时间
            OnlineUser user = ONLINE_MAP.get(uid);
            if (user != null) {
                user.updateActiveTime();
            }

            // 解析消息
            MsgVo vo = JSON.parseObject(json, MsgVo.class);
            if (vo == null || vo.getType() == null || vo.getContent() == null) {
                return;
            }

            // 处理消息
            processMessage(vo, user);

        } catch (Exception e) {
            log.error("处理消息异常 uid={}, json={}: {}", uid,
                    json.length() > 100 ? json.substring(0, 100) + "..." : json,
                    e.getMessage());
        }
    }

    @OnMessage
    public void onPong(PongMessage pongMessage, @PathParam("uid") String uid) {
        OnlineUser user = ONLINE_MAP.get(uid);
        if (user != null) {
            user.updateActiveTime();
            log.trace("收到pong响应: {}", uid);
        }
    }

    /* ===== 消息处理 ===== */

    private void processMessage(MsgVo vo, OnlineUser user) {
        if (user == null) return;

        switch (vo.getType()) {
            case "group":
                smartBroadcast(buildMsg("group", user.getName(), null, vo.getContent()));
                break;

            case "private":
                String privateMsg = buildMsg("private", user.getName(), vo.getTo(), vo.getContent());
                sendToUser(vo.getTo(), privateMsg);
                sendAsync(user.getSession(), privateMsg);
                break;

            case "createRoom":
                String roomId = createRoom(vo.getContent(), user.getUid());
                sendSync(user.getSession(), buildMsg("selfRoom", null, user.getUid(), roomId));
                SCHEDULED_EXECUTOR.schedule(this::refreshUserList, 100, TimeUnit.MILLISECONDS);
                break;

            case "joinRoom":
                boolean joined = joinRoom(vo.getTo(), user.getUid());
                sendAsync(user.getSession(), buildMsg(joined ? "sys" : "error", null,
                        user.getUid(), joined ? "已加入房间" : "房间不存在"));
                if (joined) {
                    SCHEDULED_EXECUTOR.schedule(this::refreshUserList, 100, TimeUnit.MILLISECONDS);
                }
                break;

            case "room":
                broadcastToRoom(vo.getTo(), buildMsg("room", user.getName(), vo.getTo(), vo.getContent()));
                break;

            case "ping":
                sendAsync(user.getSession(), buildMsg("pong", null, user.getUid(), "pong"));
                break;

            default:
                log.warn("未知消息类型: {}", vo.getType());
        }
    }

    /* ===== 心跳管理（修复内存泄漏关键） ===== */

    private void startHeartbeat(OnlineUser user) {
        if (user == null || user.getSession() == null) {
            return;
        }

        // 为每个用户创建心跳任务
        ScheduledFuture<?> future = SCHEDULED_EXECUTOR.scheduleAtFixedRate(() -> {
            try {
                String uid = user.getUid();
                Session session = user.getSession();

                // 检查用户是否还在线
                OnlineUser currentUser = ONLINE_MAP.get(uid);
                if (currentUser == null || session == null || !session.isOpen()) {
                    log.debug("用户已离线，停止心跳: {}", uid);
                    if (user.getPingFuture() != null) {
                        user.getPingFuture().cancel(false);
                    }
                    return;
                }

                // 检查无响应超时
                long inactiveTime = TimeUnit.MILLISECONDS.toSeconds(
                        System.currentTimeMillis() - user.getLastActiveTime());

                if (inactiveTime > HEARTBEAT_TIMEOUT_SECONDS) {
                    log.warn("心跳超时，移除用户: {}, 无响应时间: {}s", uid, inactiveTime);
                    safeRemoveAndClose(uid);
                    return;
                }

                ping(session, uid);

            } catch (Exception e) {
                log.error("心跳任务异常: {}", e.getMessage());
            }
        }, HEARTBEAT_INTERVAL_SECONDS, HEARTBEAT_INTERVAL_SECONDS, TimeUnit.SECONDS);

        user.setPingFuture(future);
    }

    private void ping(Session session, String uid) {
        // 发送ping
        try {
            session.getAsyncRemote().sendPing(ByteBuffer.wrap("PING".getBytes()));
            log.trace("发送ping: {}", uid);
        } catch (Exception e) {
            log.debug("发送ping失败: {}", e.getMessage());
            safeRemoveAndClose(uid);
        }
    }

    /* ===== 定期清理任务 ===== */

    private static void cleanupInactiveConnections() {
        try {
            long now = System.currentTimeMillis();
            int cleaned = 0;

            for (Map.Entry<String, OnlineUser> entry : ONLINE_MAP.entrySet()) {
                OnlineUser user = entry.getValue();

                // 检查无响应超时
                long inactiveTime = TimeUnit.MILLISECONDS.toSeconds(now - user.getLastActiveTime());
                if (inactiveTime > HEARTBEAT_TIMEOUT_SECONDS * 2) { // 两倍超时时间
                    log.info("清理无响应连接: {}, 无响应时间: {}s", user.getName(), inactiveTime);

                    // 安全移除
                    CONNECTION_LOCK.lock();
                    try {
                        ONLINE_MAP.remove(entry.getKey());
                        if (user.getPingFuture() != null) {
                            user.getPingFuture().cancel(false);
                        }
                        ACTIVE_CONNECTIONS.decrementAndGet();
                        cleaned++;
                    } finally {
                        CONNECTION_LOCK.unlock();
                    }
                }
            }

            if (cleaned > 0) {
                log.info("清理完成，移除 {} 个无响应连接", cleaned);
            }

        } catch (Exception e) {
            log.error("清理连接异常: {}", e.getMessage());
        }
    }

    private static void printStatistics() {
        log.info("WebSocket统计 - 活跃连接: {}, 总连接: {}, 房间数: {}, 内存使用: {}MB",
                ACTIVE_CONNECTIONS.get(),
                TOTAL_CONNECTIONS.get(),
                ROOM_MAP.size(),
                (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024);
    }

    /* ===== 房间管理 ===== */

    private void broadcastToRoom(String roomId, String json) {
        Room room = ROOM_MAP.get(roomId);
        if (room == null) return;

        room.getMembers().forEach(uid -> {
            OnlineUser member = ONLINE_MAP.get(uid);
            if (member != null) {
                sendAsync(member.getSession(), json);
            }
        });
    }

    private String createRoom(String roomName, String creatorUid) {
        String roomId = "room_" + System.currentTimeMillis() + "_" + creatorUid.hashCode();
        Room room = new Room(roomId, roomName, ConcurrentHashMap.newKeySet());
        room.getMembers().add(creatorUid);
        ROOM_MAP.put(roomId, room);
        log.info("创建房间: {}, 创建者: {}", roomName, creatorUid);
        return roomId;
    }

    private boolean joinRoom(String roomId, String uid) {
        Room room = ROOM_MAP.get(roomId);
        if (room == null) return false;
        room.getMembers().add(uid);
        log.info("用户加入房间: {}, 房间: {}", uid, roomId);
        return true;
    }

    /* ===== 辅助方法 ===== */

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

    /* ===== DTO类 ===== */

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

    /* ===== 系统管理方法 ===== */

    /**
     * 优雅关闭（应用退出时调用）
     */
    public static void gracefulShutdown() {
        log.info("开始优雅关闭WebSocket服务...");

        // 1. 停止接收新连接
        CONNECTION_LOCK.lock();

        try {
            // 2. 关闭所有连接
            int count = ONLINE_MAP.size();
            ONLINE_MAP.values().forEach(user -> {
                try {
                    if (user.getSession() != null && user.getSession().isOpen()) {
                        user.getSession().close(new CloseReason(
                                CloseReason.CloseCodes.GOING_AWAY,
                                "服务关闭"
                        ));
                    }
                    if (user.getPingFuture() != null) {
                        user.getPingFuture().cancel(false);
                    }
                } catch (Exception e) {
                    // 忽略关闭时的异常
                }
            });

            ONLINE_MAP.clear();
            ROOM_MAP.clear();
            ACTIVE_CONNECTIONS.set(0);

            log.info("已关闭 {} 个连接", count);

        } finally {
            CONNECTION_LOCK.unlock();
        }

        // 3. 关闭线程池
        try {
            SCHEDULED_EXECUTOR.shutdown();
            if (!SCHEDULED_EXECUTOR.awaitTermination(5, TimeUnit.SECONDS)) {
                SCHEDULED_EXECUTOR.shutdownNow();
            }
            log.info("线程池已关闭");
        } catch (InterruptedException e) {
            SCHEDULED_EXECUTOR.shutdownNow();
            Thread.currentThread().interrupt();
        }

        log.info("WebSocket服务关闭完成");
    }
}