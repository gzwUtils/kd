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
 */
@SuppressWarnings("all")
@Service
@LocalRateLimit(rateLimitStrategy = RateLimitStrategy.SEND_USER_NUM_RATE_LIMIT)
public class SendUserNumRateLimitServiceImpl implements FlowControlService {

    /**
     * 根据渠道进行流量控制
     *
     * @param taskInfo info
     * @param flowControlParam flow
     */
    @Override
    public Double flowControl(TaskInfo taskInfo, FlowControlParam flowControlParam) {
        RateLimiter rateLimiter = flowControlParam.getRateLimiter();
        return rateLimiter.acquire(taskInfo.getReceiver().size());
    }
}
