package com.gzw.kd.learn.model.model;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.CountDownLatch2;

/**
 * @author gzw
 * @description： GzwThread
 * @since：2023/3/23 16:43
 */
@SuppressWarnings("all")
@Slf4j
public abstract class GzwThread implements Runnable{

    private Thread thread;

    private static final long JOIN_TIME = 90000L;

    protected boolean isDaemon = false;

    protected final CountDownLatch2 waitPoint = new CountDownLatch2(1);
    protected volatile AtomicBoolean hasNotified = new AtomicBoolean(false);
    private final  AtomicBoolean started = new AtomicBoolean(false);

    protected volatile boolean stopped = false;


    public GzwThread() {
    }

    /**
     *  thread name
     * @return
     */
    public abstract String getServiceName();


    public void start(){
        if(!started.compareAndSet(false,true)){
            log.warn("try to stop service threadName {}",thread.getName());
            return;
        }
        String serviceName = getServiceName();
        log.info("try to start service threadName :{} started :{} lastThread :{}",serviceName,started.get(),thread);
        this.stopped = false;
        this.thread = new Thread(this,serviceName);
        this.thread.setDaemon(isDaemon);
        this.thread.start();
    }

    public void shutdown(boolean interrupt) {
        if (this.started.compareAndSet(true, false)) {
            this.stopped = true;
            log.info("shutdown thread " + this.thread.getName() + " interrupt " + interrupt);
            if (this.hasNotified.compareAndSet(false, true)) {
                this.waitPoint.countDown();
            }

            try {
                if (interrupt) {
                    this.thread.interrupt();
                }

                long beginTime = System.currentTimeMillis();
                if (!this.thread.isDaemon()) {
                    this.thread.join(this.getJoinTime());
                }

                long elapsedTime = System.currentTimeMillis() - beginTime;
                log.info("join thread " + this.thread.getName() + " elapsed time(ms) " + elapsedTime + " " + this.getJoinTime());
            } catch (InterruptedException var6) {
                log.error("Interrupted", var6);
            }

        }
    }


    public void shutdown() {
        this.shutdown(true);
    }

    public long getJoinTime() {
        return 90000L;
    }


    public void makeStop() {
        if (this.started.get()) {
            this.stopped = true;
            log.info("makestop thread " + thread.getName());
        }
    }

    public void wakeup() {
        if (this.hasNotified.compareAndSet(false, true)) {
            this.waitPoint.countDown();
        }

    }

    protected void waitForRunning(long interval) {
        if (this.hasNotified.compareAndSet(true, false)) {
            this.onWaitEnd();
        } else {
            this.waitPoint.reset();

            try {
                this.waitPoint.await(interval, TimeUnit.MILLISECONDS);
            } catch (InterruptedException var7) {
                log.error("Interrupted", var7);
            } finally {
                this.hasNotified.set(false);
                this.onWaitEnd();
            }

        }
    }

    protected void onWaitEnd() {
    }

    public boolean isStopped() {
        return this.stopped;
    }

    public boolean isDaemon() {
        return this.isDaemon;
    }

    public void setDaemon(boolean daemon) {
        this.isDaemon = daemon;
    }
}
