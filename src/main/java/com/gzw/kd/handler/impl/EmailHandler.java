package com.gzw.kd.handler.impl;
import com.alibaba.fastjson2.JSON;
import com.google.common.util.concurrent.RateLimiter;
import com.gzw.kd.common.entity.*;
import com.gzw.kd.common.enums.ChannelTypeEnum;
import com.gzw.kd.common.enums.RateLimitStrategy;
import com.gzw.kd.handler.BaseHandler;
import com.gzw.kd.mail.MailUtil;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author gzw
 * @description： 邮件推送
 * @since：2023/5/25 11:19
 */
@SuppressWarnings("all")
@Slf4j
@Component
public class EmailHandler extends BaseHandler {

    public EmailHandler() {
        channelCode = ChannelTypeEnum.EMAIL.getCode();

        // 按照请求限流，默认单机 3 qps （具体数值配置在apollo动态调整)
        double rateInitValue = 3.0;
        flowControlParam = FlowControlParam.builder().rateInitValue(rateInitValue)
                .rateLimitStrategy(RateLimitStrategy.REQUEST_RATE_LIMIT)
                .rateLimiter(RateLimiter.create(rateInitValue)).build();

    }
    @Override
    public boolean handler(TaskInfo taskInfo) {
        try {
            EmailContentModel emailContentModel = JSON.parseObject(taskInfo.getContentModel(), EmailContentModel.class);
            Set<String> receiver = taskInfo.getReceiver();
            String[] array = receiver.toArray(new String[0]);
            MailUtil.getMailSend().sendEmail(emailContentModel.getTitle(), emailContentModel.getContent(), array, true);

        } catch (Exception e) {
            log.error("EmailHandler error {}", e.getMessage(), e);
            return false;
        }
        return true;
    }

    @Override
    public void recall(TemplateInfo templateInfo) {

    }
}
