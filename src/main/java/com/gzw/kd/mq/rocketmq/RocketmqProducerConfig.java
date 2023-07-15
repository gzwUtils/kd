package com.gzw.kd.mq.rocketmq;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.acl.common.AclClientRPCHook;
import org.apache.rocketmq.acl.common.SessionCredentials;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 生产者配置
 * @author 高志伟
 */
@Slf4j
@SuppressWarnings("all")
@Configuration
@ConditionalOnProperty(prefix ="rocketmq.producer",value = "isOnOff",havingValue ="on")
public class RocketmqProducerConfig {

    @Value("${rocketmq.producer.groupName}")
    private String groupName;
    @Value("${rocketmq.producer.nameServAddr}")
    private String nameServAddr;
    @Value("${rocketmq.producer.maxMessageSize}")
    private Integer maxMessageSize;
    @Value("${rocketmq.producer.sendMsgTimeout}")
    private Integer sendMsgTimeout;
    @Value("${rocketmq.producer.retryTimesWhenSendFailed}")
    private Integer retryTimesWhenSendFailed;
    @Value("${rocketmq.acl.accessKey}")
    private String accessKey;
    @Value("${rocketmq.acl.secretKey}")
    private String secretKey;

    @Bean
    public DefaultMQProducer defaultMQProducer() throws MQClientException {
        log.info("rocketMq  produce server   正在创建 --------------------------------------------");
        DefaultMQProducer defaultMQProducer=new DefaultMQProducer(groupName,new AclClientRPCHook(new SessionCredentials(accessKey,secretKey)));
        defaultMQProducer.setNamesrvAddr(nameServAddr);
        defaultMQProducer.setVipChannelEnabled(false);
        defaultMQProducer.setMaxMessageSize(maxMessageSize);
        defaultMQProducer.setSendMsgTimeout(sendMsgTimeout);
        defaultMQProducer.setRetryTimesWhenSendFailed(retryTimesWhenSendFailed);
        defaultMQProducer.setUnitName("yc_ai_cm");
        defaultMQProducer.setInstanceName("ai_cm");
        defaultMQProducer.start();
        log.info("rocketMq  produce server 创建成功 -------------------------------------------------");
        return defaultMQProducer;
    }
}
