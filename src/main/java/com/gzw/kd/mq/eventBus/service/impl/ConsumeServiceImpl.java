package com.gzw.kd.mq.eventBus.service.impl;

import com.gzw.kd.handler.HandlerHolder;
import com.gzw.kd.common.entity.TaskInfo;
import com.gzw.kd.common.entity.TemplateInfo;
import com.gzw.kd.mq.eventBus.service.CustomerService;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * @author gzw
 * @description：
 * @since：2023/5/24 14:34
 */
@ConditionalOnProperty(prefix = "kd.event.customer", value = "isOnOff", havingValue = "on")
@Service
public class ConsumeServiceImpl implements CustomerService {

    @Resource
    private HandlerHolder handlerHolder;


    @Override
    public void customerMessage(List<TaskInfo> list) {
        for (TaskInfo event:list) {
            handlerHolder.getHandler(event.getSendChannel()).doHandler(event);
        }
    }

    @Override
    public void customerRecall(TemplateInfo templateInfo) {
        handlerHolder.getHandler(templateInfo.getSendChannel()).recall(templateInfo);
    }
}
