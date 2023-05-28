package com.gzw.kd.mq.eventBus.service;

/**
 * @author gzw
 * @description： 生产
 * @since：2023/5/24 11:28
 */
public interface SendMqService {

    /**
     * 发送消息
     *
     * @param topic topic
     * @param jsonValue json
     * @param tagId tag
     */
    void send(String topic, String jsonValue, String tagId);


    /**
     * 发送消息
     *
     * @param topic topic
     * @param jsonValue json
     */
    void send(String topic, String jsonValue);
}
