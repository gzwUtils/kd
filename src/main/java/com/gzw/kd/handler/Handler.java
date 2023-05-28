package com.gzw.kd.handler;

import com.gzw.kd.common.entity.TaskInfo;
import com.gzw.kd.common.entity.TemplateInfo;

/**
 * @author gzw
 * @description：
 * @since：2023/5/24 14:46
 */
public interface Handler {

    /**
     * 处理器
     *
     * @param taskInfo task
     */
    void doHandler(TaskInfo taskInfo);

    /**
     * 撤回消息
     *
     * @param templateInfo info
     */
    void recall(TemplateInfo templateInfo);
}
