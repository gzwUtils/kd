package com.gzw.kd.common.thread;
import cn.hutool.core.thread.ThreadUtil;
import com.gzw.kd.common.ExecutorPoolConstant;
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
}
