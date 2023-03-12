package com.gzw.kd.mq.rocketmq.bo;

import java.io.Serializable;

/**
 * @author gaozhiwei
 */

public class CustomerMsgRocketMqBo<T> implements Serializable {

    private String topic;

    private String tags;

    public CustomerMsgRocketMqBo(String topic, String tags, T body) {
        this.topic = topic;
        this.tags = tags;
        this.body = body;
    }

    private T body;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "CustomerMsgRocketMqBo{" +
                "topic='" + topic + '\'' +
                ", tags='" + tags + '\'' +
                ", body=" + body +
                '}';
    }
}
