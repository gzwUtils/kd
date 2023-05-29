package com.gzw.kd.common.enums;

import lombok.Getter;

/**
 * @author gzw
 * @description：
 * @since：2023/5/25 10:37
 */
@Getter
public enum RateLimitStrategy {


    /**
     * 根据真实请求数限流 (实际意义上的QPS）
     */
    REQUEST_RATE_LIMIT(10, "根据真实请求数限流"),
    /**
     * 根据发送用户数限流（人数限流）
     */
    SEND_USER_NUM_RATE_LIMIT(20, "根据发送用户数限流"),
    ;

    private final Integer code;
    private final String description;


    RateLimitStrategy(Integer code, String description) {
        this.code = code;
        this.description = description;
    }
}
