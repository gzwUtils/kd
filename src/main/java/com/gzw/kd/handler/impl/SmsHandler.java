package com.gzw.kd.handler.impl;
import com.alibaba.fastjson2.JSON;
import com.google.common.util.concurrent.RateLimiter;
import com.gzw.kd.common.R;
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
        SmsContentModel model = JSON.parseObject(taskInfo.getContentModel(), SmsContentModel.class);
        String content = model.getContent();

        if (receivers == null || receivers.isEmpty()) {
            log.warn("没有接收者，跳过短信发送，业务ID: {}", taskInfo.getBusinessId());
            return true;  // 无接收者视为成功
        }

        boolean allSuccess = true;
        int successCount = 0;
        int failCount = 0;

        for (String receiver : receivers) {
            try {
                if (smsUtils == null) {
                    log.error("SMSUtils 未初始化");
                    return false;
                }

                // 发送短信
                R r = smsUtils.sendMessage(receiver, content);

                if (r.getSuccess()) {
                    successCount++;
                    log.debug("短信发送成功，接收人: {}", receiver);
                } else {
                    failCount++;
                    allSuccess = false;
                    log.error("短信发送失败 {}，接收人: {}",r.getMessage(), receiver);
                }
            } catch (Exception e) {
                failCount++;
                allSuccess = false;
                log.error("短信发送异常，接收人: {}, 异常: {}", receiver, e.getMessage(), e);
            }
        }

        // 统计日志
        log.info("短信发送完成统计 - 业务ID: {}, 成功: {} 条, 失败: {} 条, 总计: {} 条",
                taskInfo.getBusinessId(), successCount, failCount, receivers.size());

        return allSuccess;
    }

    @Override
    public void recall(TemplateInfo templateInfo) {
        log.warn("recall ................................");
    }
}
