package com.gzw.kd.common.entity;

import lombok.Data;

import java.math.BigInteger;

/**
 * @author 高志伟
 */
@SuppressWarnings("all")
@Data
public class JobAndTrigger {


    private String JOB_NAME;

    private String JOB_GROUP;

    private String JOB_CLASS_NAME;

    private String TRIGGER_NAME;

    private String TRIGGER_GROUP;

    private String TRIGGER_STATE;

    private BigInteger REPEAT_INTERVAL;

    private BigInteger TIMES_TRIGGERED;

    private String CRON_EXPRESSION;

    private String TIME_ZONE_ID;

}
