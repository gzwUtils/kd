package com.gzw.kd.common.entity;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.websocket.Session;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Data
@Slf4j
public class OnlineUser {

    private final String uid;
    private final String name;

    // 使用 AtomicReference 包装 session，支持原子操作
    private final AtomicReference<Session> sessionRef;
    private final AtomicLong lastActiveTime;

    // 心跳任务引用，使用AtomicReference确保线程安全
    private final AtomicReference<ScheduledFuture<?>> pingFutureRef = new AtomicReference<>();

    // 添加锁用于保护关键操作
    private final Lock cleanupLock = new ReentrantLock();

    public OnlineUser(String uid, String name, Session session) {
        this.uid = uid;
        this.name = name;
        this.sessionRef = new AtomicReference<>(session);
        this.lastActiveTime = new AtomicLong(System.currentTimeMillis());
    }

    public Session getSession() {
        return sessionRef.get();
    }

    public void setSession(Session session) {
        sessionRef.set(session);
    }

    public void updateActiveTime() {
        lastActiveTime.set(System.currentTimeMillis());
    }

    public long getLastActiveTime() {
        return lastActiveTime.get();
    }

    public void setPingFuture(ScheduledFuture<?> newFuture) {
        // CAS操作，确保线程安全
        while (true) {
            ScheduledFuture<?> current = pingFutureRef.get();
            if (pingFutureRef.compareAndSet(current, newFuture)) {
                // 取消旧的任务（如果有）
                if (current != null && !current.isDone()) {
                    current.cancel(false);
                    log.debug("替换旧的心跳任务: {}", uid);
                }
                return;
            }
        }
    }

    public void cancelPingFuture() {
        ScheduledFuture<?> future = pingFutureRef.getAndSet(null);
        if (future != null && !future.isDone()) {
            boolean cancelled = future.cancel(false);
            log.debug("取消心跳任务 {}: {}", uid, cancelled ? "成功" : "失败");
        }
    }

    /**
     * 安全清理资源（线程安全）
     */
    public void cleanup() {
        // 使用锁确保cleanup操作的原子性
        cleanupLock.lock();
        try {
            // 1. 取消心跳任务
            cancelPingFuture();

            // 2. 安全关闭session
            Session session = sessionRef.getAndSet(null);
            if (session != null && session.isOpen()) {
                try {
                    session.close();
                    log.debug("关闭session: {}", uid);
                } catch (Exception e) {
                    log.warn("关闭session异常 uid={}: {}", uid, e.getMessage());
                }
            }

            log.debug("用户资源已清理: {}", uid);

        } finally {
            cleanupLock.unlock();
        }
    }

    /**
     * 检查用户是否仍然有效
     */
    public boolean isValid() {
        Session session = sessionRef.get();
        return session != null && session.isOpen();
    }

}