package com.gzw.kd.common.entity;

import lombok.Data;

/**
 * @author 高志伟
 */

@Data
public class CronTriggerKey {

    private String schedulerName;

    private String triggerName;

    private String triggerGroup;
}
