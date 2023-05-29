package com.gzw.kd.common.annotation;

import com.gzw.kd.common.enums.RateLimitStrategy;
import java.lang.annotation.*;

/**
 * @author gzw
 * @description： 单机限流
 * @since：2023/5/25 10:36
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LocalRateLimit {

    /**
     *  默认根据请求数限流
     * @return 限流策略
     */

    RateLimitStrategy rateLimitStrategy() default RateLimitStrategy.REQUEST_RATE_LIMIT;
}
