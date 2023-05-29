package com.gzw.kd.send;

import com.gzw.kd.common.entity.MessageParam;
import com.gzw.kd.common.entity.TaskInfo;
import com.gzw.kd.common.entity.TemplateInfo;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author gzw
 * @description： 发送消息任务类型
 * @since：2023/5/25 22:05
 */
@Builder
@Accessors(chain = true)
@Data
public class SendTaskModel extends ProcessModel{

    /**
     * 消息模板Id
     */
    private Long messageTemplateId;

    /**
     * 请求参数
     */
    private List<MessageParam> messageParamList;

    /**
     * 发送任务的信息
     */
    private List<TaskInfo> taskInfo;

    /**
     * 撤回任务的信息
     */
    private TemplateInfo templateInfo;
}
