package com.gzw.kd.mq.rocketmq;

import com.gzw.kd.common.MqConstant;
import com.gzw.kd.mq.rocketmq.service.RocketMqCustomerMsgService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.acl.common.AclClientRPCHook;
import org.apache.rocketmq.acl.common.SessionCredentials;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.rebalance.AllocateMessageQueueAveragely;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * 消费者配置
 * @author gaozhiwei
 */
@Slf4j
@SuppressWarnings("all")
@Configuration
@ConditionalOnProperty(prefix ="rocketmq.consumer",value = "isOnOff",havingValue ="on")
public class RocketmqConsumerConfig {


    @Value("${rocketmq.consumer.nameServAddr}")
    private String nameServAddr;
    @Value("${rocketmq.consumer.groupName}")
    private String groupName;
    @Value("${rocketmq.consumer.consumeThreadMin}")
    private int consumeThreadMin;
    @Value("${rocketmq.consumer.consumeThreadMax}")
    private int consumeThreadMax;
    @Value("${rocketmq.consumer.consumeMessageBatchMaxSize}")
    private int consumeMessageBatchMaxSize;
    @Value("${rocketmq.consumer.retryTimesWhenConsumerFailed}")
    private int retryTimesWhenConsumerFailed;

    @Value("${spring.profiles.active}")
    private String system;

    @Value("${rocketmq.acl.accessKey}")
    private String accessKey;
    @Value("${rocketmq.acl.secretKey}")
    private String secretKey;

    @Resource
    private RocketMqCustomerMsgService mqMsgListener;
    @Bean
    public DefaultMQPushConsumer defaultMQPushConsumer(){
        log.info("rocketmq consumer server 正在创建-------------------------------------");
        DefaultMQPushConsumer defaultMQPushConsumer=new DefaultMQPushConsumer(groupName, new AclClientRPCHook(new SessionCredentials(accessKey,secretKey)),
                //平均分配队列算法 hash
                new AllocateMessageQueueAveragely());
        defaultMQPushConsumer.setNamesrvAddr(nameServAddr);
        defaultMQPushConsumer.setConsumeThreadMin(consumeThreadMin);
        defaultMQPushConsumer.setConsumeThreadMax(consumeThreadMax);
        defaultMQPushConsumer.registerMessageListener(mqMsgListener);
        defaultMQPushConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        defaultMQPushConsumer.setMessageModel(MessageModel.BROADCASTING);
        defaultMQPushConsumer.setConsumeMessageBatchMaxSize(consumeMessageBatchMaxSize);
        defaultMQPushConsumer.setMaxReconsumeTimes(retryTimesWhenConsumerFailed);
        defaultMQPushConsumer.setVipChannelEnabled(false);
        defaultMQPushConsumer.setUnitName("yc_ai_cm");
        defaultMQPushConsumer.setInstanceName("ai_cm");
        try {
            String topic = MqConstant.Topic.KD_BUSINESS_TOPIC+system;
            defaultMQPushConsumer.subscribe(topic,"*");
            defaultMQPushConsumer.start();
            log.info("rocketmq consumer server 创建成功--------------------------------------------------");
        }catch (MQClientException e){
            log.error("rocketmq consumer server exception ",e);
            e.getErrorMessage();
        }

        return defaultMQPushConsumer;
    }
}
