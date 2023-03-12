package com.gzw.kd.mq.rocketmq.service;

import com.alibaba.fastjson.JSON;

import com.gzw.kd.mq.rocketmq.bo.CustomerMsgRocketMqBo;
import com.gzw.kd.mq.rocketmq.listener.ICustomerRocketMqMessageListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 消费者消息监听类
 * @author gaozhiwei
 */
@Slf4j
@SuppressWarnings("all")
@Component
public class RocketMqCustomerMsgService implements MessageListenerConcurrently {


    private ICustomerRocketMqMessageListener listener;

    @Value("${rocketmq.consumer.retryTimesWhenConsumerFailed}")
    private int retryTimesWhenConsumerFailed;

    public void addMessageListener(ICustomerRocketMqMessageListener listener) {
        this.listener = listener;
    }


    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        if(CollectionUtils.isEmpty(list)){
            log.warn("rocketmq customer 收到的消息为空");
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
        log.info("rocketmq customer 收到的消息为[{}]", JSON.toJSONString(list));
        MessageExt messageExt = list.get(0);
        try {
            String topic = messageExt.getTopic();
            String tags = messageExt.getTags();
            String body = new String(messageExt.getBody(), StandardCharsets.UTF_8);
            CustomerMsgRocketMqBo rocketMqBo = new CustomerMsgRocketMqBo(topic, tags, body);
            if (this.listener != null) {
                listener.onMessage(rocketMqBo);
            }
            log.info("rocketmq customer success topic[{}],tags[{}],msgId[{}],body[{}]", topic, tags, messageExt.getMsgId(), body);
        } catch (Exception e) {
            log.error("customer 获取rocketmq 消息内容异常", e);
            if (messageExt.getReconsumeTimes() == retryTimesWhenConsumerFailed) {
                log.debug("消费次数[{}],消息[{}]", retryTimesWhenConsumerFailed, JSON.toJSONString(messageExt));
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            } else {
                log.debug("消费失败,请重试!!!");
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }
}

