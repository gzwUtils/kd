package com.gzw.kd.common.enums;

/**
 * @author gzw
 * @description： 调度过期策略
 * @since：2023/6/5 15:13
 */
public enum MisfireStrategyEnum {


    /**
     * do nothing
     */
    DO_NOTHING,

    /**
     * fire once now
     */
    FIRE_ONCE_NOW;

    MisfireStrategyEnum() {
    }
}
