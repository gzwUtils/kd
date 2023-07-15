package com.gzw.kd.mq.eventBus.listener;

import com.alibaba.fastjson.JSON;
import com.gzw.kd.common.MqConstant;
import com.gzw.kd.mq.eventBus.bo.KdApplicationEvent;
import com.gzw.kd.common.entity.TaskInfo;
import com.gzw.kd.common.entity.TemplateInfo;
import com.gzw.kd.mq.eventBus.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * @author gzw
 * @description：
 * @since：2023/5/24 14:02
 */

@SuppressWarnings("all")
@Component
public class SpringEventBusReceiverListener implements ApplicationListener<KdApplicationEvent> {

    @Autowired(required = false)
    private  CustomerService customerService;

    @Override
    public void onApplicationEvent(KdApplicationEvent event) {
        String topic = event.getSpringEventBusSource().getTopic();
        String jsonValue = event.getSpringEventBusSource().getJsonValue();
        if (topic.equals(MqConstant.Topic.KD_BUSINESS_TOPIC)) {
            customerService.customerMessage(JSON.parseArray(jsonValue, TaskInfo.class));
        } else if (topic.equals(MqConstant.Topic.KD_BUSINESS_RECALL_TOPIC)) {
            customerService.customerRecall(JSON.parseObject(jsonValue, TemplateInfo.class));
        }
    }
}
