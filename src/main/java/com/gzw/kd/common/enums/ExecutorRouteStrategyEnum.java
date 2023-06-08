package com.gzw.kd.common.enums;

/**
 * @author gzw
 * @description：  路由策略
 * @since：2023/6/5 15:14
 */
public enum ExecutorRouteStrategyEnum {

    /**
     * FIRST
     */
    FIRST,
    /**
     * LAST
     */
    LAST,
    /**
     * ROUND
     */
    ROUND,
    /**
     * RANDOM
     */
    RANDOM,
    /**
     * CONSISTENT_HASH
     */
    CONSISTENT_HASH,
    /**
     * LEAST_FREQUENTLY_USED
     */
    LEAST_FREQUENTLY_USED,
    /**
     * LEAST_RECENTLY_USED
     */
    LEAST_RECENTLY_USED,
    /**
     * FAILOVER
     */
    FAILOVER,
    /**
     * BUSYOVER
     */
    BUSYOVER,
    /**
     * SHARDING_BROADCAST
     */
    SHARDING_BROADCAST;

    ExecutorRouteStrategyEnum() {
    }
}
