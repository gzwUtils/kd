package com.gzw.kd.mq.rocketmq.listener;

import com.gzw.kd.mq.rocketmq.bo.CustomerMsgRocketMqBo;

/**
 * @author gaozhiwei
 */
public interface ICustomerRocketMqMessageListener {

    /**
     * 消费消息
     * @param mqBo
     * @return
     */
    boolean onMessage(CustomerMsgRocketMqBo mqBo);
}
