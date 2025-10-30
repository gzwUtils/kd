package com.gzw.kd.cache;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 对外暴露的刷新缓存接口
 */
public interface CacheRefreshApi {

    /**
     * 刷新单个缓存
     *
     * @param beanName Spring 容器中 AbstractCache 的 bean 名称
     * @return 执行结果
     */
    RefreshResult refreshOne(String beanName);

    /**
     * 刷新全部缓存
     *
     * @return 每个缓存的刷新结果
     */
    List<RefreshResult> refreshAll();

    @Data
    @Builder
    class RefreshResult {
        private String beanName;
        private boolean success;
        private String msg;
        private long cost;          // ms
    }
}