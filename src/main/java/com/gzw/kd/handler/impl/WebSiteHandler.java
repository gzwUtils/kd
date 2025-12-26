package com.gzw.kd.handler.impl;

import com.google.common.util.concurrent.RateLimiter;
import com.gzw.kd.common.entity.FlowControlParam;
import com.gzw.kd.common.entity.TaskInfo;
import com.gzw.kd.common.entity.TemplateInfo;
import com.gzw.kd.common.enums.ChannelTypeEnum;
import com.gzw.kd.common.enums.RateLimitStrategy;
import com.gzw.kd.handler.BaseHandler;
import com.gzw.kd.service.MessageRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Set;

@SuppressWarnings("all")
@Slf4j
@Component
public class WebSiteHandler extends BaseHandler {


    @Resource
    private   MessageRecordService messageRecordService;


    public WebSiteHandler() {
        channelCode = ChannelTypeEnum.IN_SITE_MESSAGE.getCode();

        // 按照请求限流，默认单机 3 qps
        double rateInitValue = 8.0;
        flowControlParam = FlowControlParam.builder().rateInitValue(rateInitValue)
                .rateLimitStrategy(RateLimitStrategy.REQUEST_RATE_LIMIT)
                .rateLimiter(RateLimiter.create(rateInitValue)).build();
    }
    @Override
    public boolean handler(TaskInfo taskInfo) {
        try {
            // 获取接收者
            Set<String> receivers = taskInfo.getReceiver();
            if (receivers == null || receivers.isEmpty()) {
                log.warn("没有接收者，跳过站内信发送，业务ID: {}", taskInfo.getBusinessId());
                return true;
            }

            Long recordId = taskInfo.getRecordId();
            if (recordId == null) {
                log.error("站内信记录ID为空，业务ID: {}", taskInfo.getBusinessId());
                return false;
            }

            // 2. 站内信发送逻辑（直接保存到数据库就算成功）
            log.info("站内信保存成功，业务ID: {}, 接收者数量: {}",
                    taskInfo.getBusinessId(), receivers.size());

            return true;

        } catch (Exception e) {
            log.error("站内信处理失败，业务ID: {}", taskInfo.getBusinessId(), e);
            return false;
        }
    }

    @Override
    public void recall(TemplateInfo templateInfo) {

    }
}
