package com.gzw.kd.handler;

import com.gzw.kd.common.entity.SendResult;
import com.gzw.kd.common.entity.TaskInfo;
import com.gzw.kd.common.entity.TemplateInfo;

/**
 * @author gzw
 * @description：
 * @since：2023/5/24 14:46
 */
public interface Handler {

    /**
     * 处理消息
     * @param taskInfo 任务信息
     * @return 发送结果
     */
    SendResult doHandler(TaskInfo taskInfo);

    /**
     * 撤回消息
     * @param templateInfo 模板信息
     */
    void recall(TemplateInfo templateInfo);

    /**
     * 处理器是否可用
     * @return 是否可用
     */
    default boolean isAvailable() {
        return true;
    }
}
