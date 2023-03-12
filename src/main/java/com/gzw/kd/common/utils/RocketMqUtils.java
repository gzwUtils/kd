package com.gzw.kd.common.utils;

import com.alibaba.fastjson.JSON;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author gaozhiwei
 */
@SuppressWarnings("all")
@Component
public class RocketMqUtils {

    /**
     * 日志
     */
    private static final Logger LOG = LoggerFactory.getLogger(RocketMqUtils.class);

    /**
     * rocketmq模板注入
     */
    @Autowired(required = false)
    private DefaultMQProducer defaultMQProducer;

    /**
     * 发送普通消息
     * @param topic
     * @param tags
     * @param body
     * @param <T>
     * @return
     */
    public <T> boolean sendMessage(String topic,String tags, T  body){
        try {
            LOG.info("rocketmq 开始发送消息========");
            Message message = new Message(topic,tags, JSON.toJSONBytes(body));
            SendResult sendResult = defaultMQProducer.send(message);
            if(sendResult!=null){
                LOG.info("rocketmq 消息发送成功========响应状态[{}]",sendResult.getSendStatus());
                LOG.info("rocketmq 消息发送响应消息[{}]",sendResult.toString());
                return true;
            }else {
                return false;
            }
        }catch (Exception e){
            LOG.error("rocketmq 发送消息异常",e);
            e.printStackTrace();
            return false;
        }

    }

    /**
     * 发送延迟消息
     * @param topic
     * @param tags
     * @param body
     * @param <T>
     * @return
     */
    public <T> boolean sendDelayMessage(String topic,String tags, T  body,int delay){
        try {
            LOG.info("rocketmq 开始发送消息========");
            Message message = new Message(topic,tags, JSON.toJSONBytes(body));
            //1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h
            message.setDelayTimeLevel(delay);
            int SEND_TIMEOUT = 20000;
            SendResult sendResult = defaultMQProducer.send(message, SEND_TIMEOUT);
            if(sendResult!=null){
                LOG.info("rocketmq 消息发送成功========响应状态[{}]",sendResult.getSendStatus());
                LOG.info("rocketmq 消息发送响应消息[{}]",sendResult.toString());
                return true;
            }else {
                return false;
            }
        }catch (Exception e){
            LOG.error("rocketmq 发送消息异常",e);
            e.printStackTrace();
            return false;
        }

    }
}
