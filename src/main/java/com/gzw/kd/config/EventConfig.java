package com.gzw.kd.config;

import com.gzw.kd.common.thread.ThreadPoolConfig;
import com.gzw.kd.learn.model.observe.KdAsyncEventBus;
import com.gzw.kd.learn.model.observe.KdEventBus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author gzw
 * @description：
 * @since：2023/6/17 00:07
 */

@Configuration
public class EventConfig {

    @Bean(name = "eventBus")
    public KdEventBus eventBus() {
        return new KdEventBus();
    }

    @Bean(name = "asyncEventBus")
    public KdAsyncEventBus asyncEventBus() {
        return new KdAsyncEventBus(ThreadPoolConfig.getJobExecutor());
    }
}
