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
        //消息最大长度，默认1024 * 1024 * 4(默认4M)
        defaultMQProducer.setMaxMessageSize(maxMessageSize);
        //发送消息超时时间，默认3000
        defaultMQProducer.setSendMsgTimeout(sendMsgTimeout);
        //发送消息失败重试次数
        defaultMQProducer.setRetryTimesWhenSendFailed(retryTimesWhenSendFailed);
        //异步消息重试次数
        defaultMQProducer.setRetryTimesWhenSendAsyncFailed(retryTimesWhenSendFailed);
        //压缩消息阈值，默认4k(1024 * 4)
        defaultMQProducer.setCompressMsgBodyOverHowmuch(4096);
        // 是否在内部发送失败时重试另一个broker，默认false
        defaultMQProducer.setRetryAnotherBrokerWhenNotStoreOK(false);
        defaultMQProducer.setUnitName("kd_rocketmq");
        defaultMQProducer.setInstanceName("kd_rocketmq");
        defaultMQProducer.start();
        log.info("rocketMq  produce server 创建成功 -------------------------------------------------");
        return defaultMQProducer;
    }
}
