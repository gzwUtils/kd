package com.gzw.kd.mq.eventBus.service.impl;

import com.gzw.kd.common.entity.SendResult;
import com.gzw.kd.handler.Handler;
import com.gzw.kd.handler.HandlerHolder;
import com.gzw.kd.common.entity.TaskInfo;
import com.gzw.kd.common.entity.TemplateInfo;
import com.gzw.kd.mq.eventBus.service.CustomerService;
import java.util.List;
import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * @author gzw
 * @description：
 * @since：2023/5/24 14:34
 */
@Slf4j
@ConditionalOnProperty(prefix = "kd.event.customer", value = "isOnOff", havingValue = "on")
@Service
public class ConsumeServiceImpl implements CustomerService {

    @Resource
    private HandlerHolder handlerHolder;


    @Override
    public void customerMessage(List<TaskInfo> list) {
        for (TaskInfo event:list) {
            // 获取对应的处理器
            Handler handler = handlerHolder.getHandler(event.getSendChannel());

            if (handler != null) {
                // 执行处理并获取结果
                SendResult result = handler.doHandler(event);

                // 根据结果进行后续处理
                if (result.isSuccess()) {
                    // 成功处理
                    log.info("处理成功，耗时: {} ms" , result.getExecuteTime());
                } else {
                    // 处理失败
                    log.error("处理失败， {} " , result.getErrorMsg());

                    // 可以记录失败日志、重试等
                    retryOrNotify(result);
                }
            }
        }
    }

    @Override
    public void customerRecall(TemplateInfo templateInfo) {
        handlerHolder.getHandler(templateInfo.getSendChannel()).recall(templateInfo);
    }

    private void retryOrNotify(SendResult result) {
        // 失败重试或通知逻辑
        log.error("消息处理失败，准备重试 - 业务ID: {}, 处理器: {}, 错误: {}",
                result.getTaskInfo().getBusinessId(),
                result.getHandlerName(),
                result.getErrorMsg());
    }
}
