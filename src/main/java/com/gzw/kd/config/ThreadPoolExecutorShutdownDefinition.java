package com.gzw.kd.config;

import static com.gzw.kd.common.ExecutorPoolConstant.AWAIT_TERMINATION;
import static com.gzw.kd.common.ExecutorPoolConstant.TIME_UNIT;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * @author gzw 学习自3y
 * @description： 优雅关闭线程池
 * @since：2023/5/19 13:56
 */
@SuppressWarnings("all")
@Component
@Slf4j
public class ThreadPoolExecutorShutdownDefinition implements ApplicationListener<ContextClosedEvent> {

    /**
     * 线程池集合  当前要处理的线程池数量
     */
    private final List<ExecutorService>  pools=Collections.synchronizedList(new ArrayList<>(12));




    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        log.warn("容器关闭前线程池优雅关闭开始 当前要处理的线程数 {}............",pools.size());
        if(CollectionUtils.isEmpty(pools)){
            return;
        }
        for (ExecutorService pool : pools) {
            pool.shutdown();
            try {
                if (!pool.awaitTermination(AWAIT_TERMINATION, TIME_UNIT)) {
                    if (log.isWarnEnabled()) {
                        log.warn("Timed out while waiting for executor [{}] to terminate", pool);
                    }
                }
            } catch (InterruptedException ex) {
                if (log.isWarnEnabled()) {
                    log.warn("Timed out while waiting for executor [{}] to terminate", pool);
                }
                Thread.currentThread().interrupt();
            }
        }
    }




    public void registryExecutor(ExecutorService executor) {
        pools.add(executor);
    }
}
