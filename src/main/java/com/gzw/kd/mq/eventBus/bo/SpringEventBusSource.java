package com.gzw.kd.mq.eventBus.bo;

import lombok.Builder;
import lombok.Data;

/**
 * @author gzw
 * @description： 学习
 * @since：2023/5/24 11:22
 */
@Builder
@Data
public class SpringEventBusSource {

    public String topic;

    public String jsonValue;

    public String tagId;
}
