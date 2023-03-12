package com.gzw.kd.config;
import cn.hutool.core.util.ObjectUtil;
import com.gzw.kd.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author gaozhiwei
 * @描述 线程池配置
 */
@SuppressWarnings("all")
@Slf4j
@Configuration
public class ExecutorConfiguration {


    /**
     * 线程池总数
     */
    @Value("${async.executor.thread.core_pool_size:10}")
    private int corePoolSize;
    /**
     * 最大线程池数
     */
    @Value("${async.executor.thread.max_pool_size:10}")
    private int maxPoolSize;
    /**
     * 队列大小
     */
    @Value("${async.executor.thread.queue_capacity:200}")
    private int queueCapacity;
    /**
     * 线程名前缀
     */
    @Value("${async.executor.thread.name.prefix:gzw-test}")
    private String namePrefix;

    @Bean(name = "asyncTaskExecutor")
    public Executor asyncServiceExecutor() {
        log.info("start asyncTaskExecutor");
        ThreadPoolTaskExecutor executor = new CustomThreadPollTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(corePoolSize);
        //配置最大线程数
        executor.setMaxPoolSize(maxPoolSize);
        //配置队列大小
        executor.setQueueCapacity(queueCapacity);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix(namePrefix);

        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        // rejection-policy：当pool已经达到max size的时候，如何处理新任务
        // CALLER_RUNS：不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        //执行初始化
        executor.initialize();
        return executor;
    }


    public static final class CustomThreadPollTaskExecutor extends ThreadPoolTaskExecutor {

        private void showThreadPoolInfo(String prefix) {
            ThreadPoolExecutor threadPoolExecutor = getThreadPoolExecutor();

            if (ObjectUtil.isEmpty(threadPoolExecutor)) {
                return;
            }

            log.info("{}, {},taskCount [{}], completedTaskCount [{}], activeCount [{}], queueSize [{}]",
                    this.getThreadNamePrefix(),
                    prefix,
                    threadPoolExecutor.getTaskCount(),
                    threadPoolExecutor.getCompletedTaskCount(),
                    threadPoolExecutor.getActiveCount(),
                    threadPoolExecutor.getQueue().size());
        }

        @Override
        public void execute(Runnable task) {
            showThreadPoolInfo("1. do execute");
            super.execute(wrap(task,MDC.getCopyOfContextMap()));
        }

        @Override
        public void execute(Runnable task, long startTimeout) {
            showThreadPoolInfo("2. do execute");
            super.execute(wrap(task,MDC.getCopyOfContextMap()), startTimeout);
        }

        @Override
        public Future<?> submit(Runnable task) {
            showThreadPoolInfo("1. do submit");
            return super.submit(wrap(task,MDC.getCopyOfContextMap()));
        }

        @Override
        public <T> Future<T> submit(Callable<T> task) {
            showThreadPoolInfo("2. do submit");
            return super.submit(wrap(task,MDC.getCopyOfContextMap()));
        }
    }


    public static void setTraceIdIfAbsent() {
        if (MDC.get(Constants.TRACE_ID) == null) {
            MDC.put(Constants.TRACE_ID, Constants.TRACE_ID_EXECUTOR_FLAG+UUID.randomUUID().toString().replace("-",""));
        }
    }

    public static <T> Callable<T> wrap(final Callable<T> callable, final Map<String, String> context) {
        return () -> {
            if (context == null) {
                MDC.clear();
            } else {
                MDC.setContextMap(context);
            }
            setTraceIdIfAbsent();
            try {
                return callable.call();
            } finally {
                MDC.clear();
            }
        };
    }

    public static Runnable wrap(final Runnable runnable, final Map<String, String> context) {
        return () -> {
            if (context == null) {
                MDC.clear();
            } else {
                MDC.setContextMap(context);
            }
            setTraceIdIfAbsent();
            try {
                runnable.run();
            } finally {
                MDC.clear();
            }
        };
    }

}




