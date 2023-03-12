package com.gzw.kd.scheduletask;

import cn.hutool.core.thread.NamedThreadFactory;
import lombok.extern.slf4j.Slf4j;
import java.util.Date;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.*;

/**
 * @author 高志伟
 */
@SuppressWarnings("all")
@Slf4j
public class TaskEngine {

    private static TaskEngine instance = new TaskEngine();

    private Timer timer;
    private ExecutorService executor;
    private Map<TimerTask, TimerTaskWrapper> wrappedTasks = new ConcurrentHashMap<>();

    public static TaskEngine getInstance(){
        return instance;
    }


    private TaskEngine (){
        timer = new Timer("TaskEngine-timer", true);
        final ThreadFactory threadFactory = new NamedThreadFactory( "TaskEngine-pool-",   Thread.currentThread().getThreadGroup(), true);
        executor = Executors.newCachedThreadPool( threadFactory );
    }

    public Future<?> submit(Runnable task) {
        try {
            return executor.submit(task);
        } catch (Throwable t) {
            log.warn("Failed to schedule task; will retry using caller's thread: {}", t.getMessage());
            FutureTask<?> result = new FutureTask<>(task, null);
            result.run();
            return result;
        }
    }


    public void schedule(TimerTask task, long delay) {
        timer.schedule(new TimerTaskWrapper(task), delay);
    }

    public void schedule(TimerTask task, Date time) {
        timer.schedule(new TimerTaskWrapper(task), time);
    }

    public void schedule(TimerTask task, long delay, long period) {
        TimerTaskWrapper taskWrapper = new TimerTaskWrapper(task);
        wrappedTasks.put(task, taskWrapper);
        timer.schedule(taskWrapper, delay, period);
    }

    private class TimerTaskWrapper extends TimerTask {

        private TimerTask task;

        public TimerTaskWrapper(TimerTask task) {
            this.task = task;
        }

        @Override
        public void run() {
            try {
                submit(task);
            } catch (Throwable t) {
                log.error("Failed to execute TimerTask", t);
            }
        }
    }
}
