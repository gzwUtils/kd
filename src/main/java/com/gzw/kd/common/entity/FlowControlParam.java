package com.gzw.kd.common.entity;

import com.google.common.util.concurrent.RateLimiter;
import com.gzw.kd.common.enums.RateLimitStrategy;
import lombok.Builder;
import lombok.Data;

/**
 * @author gzw
 * @description：
 * @since：2023/5/25 10:45
 */
@Builder
@Data
@SuppressWarnings("all")
public class FlowControlParam {

    /**
     * 限流器
     * 子类初始化的时候指定
     */
    protected RateLimiter rateLimiter;

    /**
     * 限流器初始限流大小
     * 子类初始化的时候指定
     */
    protected Double rateInitValue;

    /**
     * 限流的策略
     * 子类初始化的时候指定
     */
    protected RateLimitStrategy rateLimitStrategy;
}
