package com.gzw.kd.common.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.websocket.Session;
import java.util.concurrent.ScheduledFuture;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class OnlineUser {


    private String uid;
    private String name;
    private Session session;
    private ScheduledFuture<?> pingFuture;  // 心跳任务引用
    /**
     * -- GETTER --
     *  获取最后活跃时间
     */
    private volatile long lastActiveTime;   // 最后活跃时间（volatile保证可见性）

    public OnlineUser(String uid, String name, Session session) {
        this.uid = uid;
        this.name = name;
        this.session = session;
        this.lastActiveTime = System.currentTimeMillis();
    }

    /**
     * 更新活跃时间（线程安全）
     */
    public void updateActiveTime() {
        this.lastActiveTime = System.currentTimeMillis();
    }

    /**
     * 取消心跳任务
     */
    public void cancelPing() {
        if (pingFuture != null && !pingFuture.isCancelled()) {
            pingFuture.cancel(false);  // 不中断正在执行的任务
        }
    }

}
