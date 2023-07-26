package com.gzw.kd.mq.rocketmq.service;
import com.gzw.kd.common.utils.RocketMqUtils;
import com.gzw.kd.mq.eventBus.service.SendMqService;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * @author gzw
 * @description： rocketmq
 * @since：2023/7/15 16:34
 */
@Slf4j
@SuppressWarnings("all")
@Component
@ConditionalOnProperty(prefix ="rocketmq.producer",value = "isOnOff",havingValue ="on")
public class RocketMqSendService implements SendMqService {

    @Resource
    private RocketMqUtils rocketMqUtils;


    @Override
    public void send(String topic, String jsonValue, String tagId) {
        rocketMqUtils.sendMessage(topic,tagId,jsonValue);
    }

    @Override
    public void send(String topic, String jsonValue) {
        send(topic,null,jsonValue);
    }
}
