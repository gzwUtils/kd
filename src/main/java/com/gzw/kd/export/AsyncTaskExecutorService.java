package com.gzw.kd.export;

import cn.hutool.core.thread.ExecutorBuilder;
import cn.hutool.core.thread.ThreadUtil;
import com.gzw.kd.common.ExecutorPoolConstant;
import com.gzw.kd.vo.output.AsyncTaskOutput;
import java.util.concurrent.TimeUnit;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

/**
 *  业务  核心线程可以被回收  不被spring管理
 * @author 高志伟
 */
@Component
@Slf4j
@SuppressWarnings("all")
public class AsyncTaskExecutorService {

    private static final String THREAD_PREFIX = "async-task-executor-thread-";
    private static final ThreadPoolExecutor EXECUTOR;

    static {
        EXECUTOR = ExecutorBuilder.create().setMaxPoolSize(ExecutorPoolConstant.COMMON_MAX_POOL_SIZE).
                setCorePoolSize(ExecutorPoolConstant.COMMON_CORE_POOL_SIZE).setWorkQueue(ExecutorPoolConstant.SYNCHRONOUS_QUEUE)
                .setAllowCoreThreadTimeOut(true).setKeepAliveTime(ExecutorPoolConstant.SMALL_KEEP_LIVE_TIME, TimeUnit.SECONDS).
                setHandler(new ThreadPoolExecutor.CallerRunsPolicy())
                .setThreadFactory(ThreadUtil.newNamedThreadFactory(THREAD_PREFIX, true)).build();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (!EXECUTOR.isTerminated()) {
                log.info("Export executor closing...");
                EXECUTOR.shutdown();
                log.info("Export executor closed");
            }
        }, "Export-executor-shutdown-hook-thread"));
    }

    public void submitTask(Callable<AsyncTaskOutput> callable, Consumer<AsyncRunnableResult> postHandler) {
        EXECUTOR.submit(wrapperTryCatch(callable, postHandler));
    }

    private Runnable wrapperTryCatch(Callable<AsyncTaskOutput> callable
            , Consumer<AsyncRunnableResult> postHandler) {
        return () -> {
            AsyncTaskOutput output = null;
            Exception ex = null;
            try {
                output = callable.call();
            } catch (Exception e) {
                log.warn("Occurs error when executing async task", e);
                ex = e;
            }
            postHandler.accept(new AsyncRunnableResult(output, ex));
        };
    }

    @Getter
    @AllArgsConstructor
    public static class AsyncRunnableResult {
        private final AsyncTaskOutput asyncTaskOutput;
        private final Exception ex;

        public boolean isOccursException() {
            return null != ex;
        }
    }
}
