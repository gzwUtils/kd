package com.gzw.kd.mq.rocketmq.listener;

import com.gzw.kd.mq.rocketmq.bo.CustomerMsgRocketMqBo;

/**
 * @author gaozhiwei
 */
@SuppressWarnings("unused")
public interface ICustomerRocketMqMessageListener {

    /**
     * 消费消息
     * @param mqBo mq
     * @return flag
     */
    boolean onMessage(CustomerMsgRocketMqBo mqBo);
}
