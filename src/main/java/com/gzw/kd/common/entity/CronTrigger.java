package com.gzw.kd.common.entity;

import lombok.Data;

/**
 * @author 高志伟
 */

@Data
public class CronTrigger extends CronTriggerKey{

    private String cronExpression;

    private String timeZoneId;
}
