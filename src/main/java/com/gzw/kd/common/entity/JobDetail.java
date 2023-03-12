package com.gzw.kd.common.entity;

import lombok.Data;

/**
 * @author 高志伟
 */
@Data
public class JobDetail extends JobDetailKey{

    private String description;

    private String jobClassName;

    private String isDurable;

    private String isNonConcurrent;

    private String isUpdateData;

    private String requestsRecovery;

    private byte[] jobData;
}
