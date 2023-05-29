package com.gzw.kd.mq.eventBus.service;

import com.gzw.kd.common.entity.TaskInfo;
import com.gzw.kd.common.entity.TemplateInfo;
import java.util.List;

/**
 * @author gzw
 * @description： 消费
 * @since：2023/5/24 14:13
 */
public interface CustomerService {

    /**
     * 消费消息
     * @param list jsonList
     */
    void customerMessage(List<TaskInfo> list);

    /**
     * 消费消息 回退
     * @param templateInfo templateInfo
     */
    void customerRecall(TemplateInfo templateInfo);
}
