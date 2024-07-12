package com.gzw.kd.handler.impl;
import com.alibaba.fastjson2.JSON;
import com.google.common.util.concurrent.RateLimiter;
import com.gzw.kd.common.entity.FlowControlParam;
import com.gzw.kd.common.entity.SmsContentModel;
import com.gzw.kd.common.enums.ChannelTypeEnum;
import com.gzw.kd.common.enums.RateLimitStrategy;
import com.gzw.kd.common.utils.SMSUtils;
import com.gzw.kd.handler.BaseHandler;
import com.gzw.kd.common.entity.TaskInfo;
import com.gzw.kd.common.entity.TemplateInfo;
import java.util.Set;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author gzw
 * @description： 短信推送
 * @since：2023/5/24 15:33
 */
@SuppressWarnings("all")
@Slf4j
@Component
public class SmsHandler extends BaseHandler {


    @Resource
    SMSUtils smsUtils;


    public SmsHandler() {

        channelCode = ChannelTypeEnum.SMS.getCode();

        // 按照请求限流，默认单机 3 qps
        double rateInitValue = 3.0;
        flowControlParam = FlowControlParam.builder().rateInitValue(rateInitValue)
                .rateLimitStrategy(RateLimitStrategy.REQUEST_RATE_LIMIT)
                .rateLimiter(RateLimiter.create(rateInitValue)).build();
    }

    @Override
    public boolean handler(TaskInfo taskInfo) {
        Set<String> receivers = taskInfo.getReceiver();
        for (String receiver : receivers) {
            SmsContentModel model = JSON.parseObject(taskInfo.getContentModel(), SmsContentModel.class);
            try {
                smsUtils.sendMessage(receiver, model.getContent());
            } catch (Exception e) {
                log.error("event send error ..........", e);
            }
            log.info("testHandler  sendChannel {} businessId {},context {},receiver {}", taskInfo.getSendChannel(), taskInfo.getBusinessId(), model.getContent(), taskInfo.getReceiver());
        }
        return true;
    }

    @Override
    public void recall(TemplateInfo templateInfo) {
        log.warn("recall ................................");
    }
}
