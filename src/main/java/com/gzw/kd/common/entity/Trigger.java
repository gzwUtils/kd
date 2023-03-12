package com.gzw.kd.common.entity;

import lombok.Data;

/**
 * @author 高志伟
 */
@Data
public class Trigger extends TriggerKey{

    private String jobName;

    private String jobGroup;

    private String description;

    private Long nextFireTime;

    private Long prevFireTime;

    private Integer priority;

    private String triggerState;

    private String triggerType;

    private Long startTime;

    private Long endTime;

    private String calendarName;

    private Short misfireInstr;

    private byte[] jobData;
}
