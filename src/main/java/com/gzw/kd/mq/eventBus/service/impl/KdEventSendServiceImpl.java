package com.gzw.kd.mq.eventBus.service.impl;

import com.gzw.kd.common.utils.ApplicationContextUtils;
import com.gzw.kd.mq.eventBus.bo.KdApplicationEvent;
import com.gzw.kd.mq.eventBus.bo.SpringEventBusSource;
import com.gzw.kd.mq.eventBus.service.SendMqService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * @author gzw
 * @description： event
 * @since：2023/5/24 11:31
 */
@ConditionalOnProperty(prefix = "kd.event.product",value = "isOnOff",havingValue = "on")
@Service
@Slf4j
public class KdEventSendServiceImpl implements SendMqService {




    @Override
    public void send(String topic, String jsonValue, String tagId) {
        SpringEventBusSource source = SpringEventBusSource.builder().topic(topic).jsonValue(jsonValue).tagId(tagId).build();
        KdApplicationEvent kdApplicationEvent = new KdApplicationEvent(this, source);
        ApplicationContextUtils.getApplicationContext().publishEvent(kdApplicationEvent);
        log.info("kd event send success topic {} tag {}",topic,tagId);
    }

    @Override
    public void send(String topic, String jsonValue) {
        send(topic,jsonValue,null);
    }
}
