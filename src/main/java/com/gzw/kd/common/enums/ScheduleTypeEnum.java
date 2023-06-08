package com.gzw.kd.common.enums;

/**
 * @author gzw
 * @description： xxl 调度类型
 * @since：2023/6/5 15:00
 */
public enum ScheduleTypeEnum {

    /**NONE*/

    NONE,
    /** schedule by cron*/
    CRON,

    /**schedule by fixed rate (in seconds)*/
    FIX_RATE;

    ScheduleTypeEnum() {
    }
}
