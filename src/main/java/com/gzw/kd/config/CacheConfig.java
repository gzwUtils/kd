package com.gzw.kd.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.TimeUnit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author gzw
 * @description： caffeine
 * @since：2023/8/5 17:56
 */

@Configuration
public class CacheConfig {

    @Bean
    public Cache<String,Object> caffeineCache(){
        return Caffeine.newBuilder()
                // 设置最后一次写入或访问后经过固定时间过期
                .expireAfterWrite(60 * 20, TimeUnit.SECONDS)
                // 初始的缓存空间大小
                .initialCapacity(100)
                // 缓存的最大条数
                .maximumSize(1000)
                .build();
    }
}
