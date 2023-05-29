package com.gzw.kd.flowControl.service.impl;

import com.google.common.util.concurrent.RateLimiter;
import com.gzw.kd.common.annotation.LocalRateLimit;
import com.gzw.kd.common.entity.FlowControlParam;
import com.gzw.kd.common.enums.RateLimitStrategy;
import com.gzw.kd.flowControl.service.FlowControlService;
import com.gzw.kd.common.entity.TaskInfo;
import org.springframework.stereotype.Service;


/**
 * @author gzw
 * @description：
 * @since：2023/5/25 10:41
 */
@Service
@SuppressWarnings("all")
@LocalRateLimit(rateLimitStrategy = RateLimitStrategy.REQUEST_RATE_LIMIT)
public class RequestRateLimitServiceImpl implements FlowControlService {

    /**
     * 根据渠道进行流量控制
     *
     * @param taskInfo
     * @param flowControlParam
     */
    @Override
    public Double flowControl(TaskInfo taskInfo, FlowControlParam flowControlParam) {
        RateLimiter rateLimiter = flowControlParam.getRateLimiter();
        return rateLimiter.acquire(1);
    }
}
