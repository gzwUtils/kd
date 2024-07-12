package com.gzw.kd.common.thread;
import cn.hutool.core.thread.ExecutorBuilder;
import cn.hutool.core.thread.ThreadUtil;
import com.gzw.kd.common.ExecutorPoolConstant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author gzw
 * @description： 线程池配置
 * @since：2023/5/19 18:27
 */

@SuppressWarnings("all")
public class ThreadPoolConfig {


    /**
     * 配置：不丢弃消息，核心线程数不会随着keepAliveTime而减少(不会被回收)
     * 动态线程池且被Spring管理：true
     */
    public static ThreadPoolExecutor getJobExecutor() {

        ThreadPoolExecutor executor = new ThreadPoolExecutor(ExecutorPoolConstant.COMMON_CORE_POOL_SIZE
                , ExecutorPoolConstant.COMMON_MAX_POOL_SIZE, ExecutorPoolConstant.COMMON_KEEP_LIVE_TIME,
                TimeUnit.SECONDS, ExecutorPoolConstant.BIG_BLOCKING_QUEUE,
        ThreadUtil.newNamedThreadFactory(ExecutorPoolConstant.EXECUTOR_NAME_PREFIX, true),new ThreadPoolExecutor.CallerRunsPolicy());
        executor.allowCoreThreadTimeOut(false);
        return  executor;
    }

    /**
     * 配置：核心线程可以被回收，当线程池无被引用且无核心线程数，应当被回收
     * 动态线程池且被Spring管理：false
     *
     * @return
     */
    public static ExecutorService getConsumePendingThreadPool() {
        return ExecutorBuilder.create()
                .setCorePoolSize(ExecutorPoolConstant.COMMON_CORE_POOL_SIZE)
                .setMaxPoolSize(ExecutorPoolConstant.COMMON_MAX_POOL_SIZE)
                .setWorkQueue(ExecutorPoolConstant.BIG_BLOCKING_QUEUE)
                .setHandler(new ThreadPoolExecutor.CallerRunsPolicy())
                .setAllowCoreThreadTimeOut(true)
                .setKeepAliveTime(ExecutorPoolConstant.SMALL_KEEP_LIVE_TIME, TimeUnit.SECONDS)
                .build();
    }
}
