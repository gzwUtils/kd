package com.gzw.kd.vo.input;

import com.gzw.kd.common.entity.MessageParam;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author gzw
 * @description：
 * @since：2023/5/26 00:00
 */
@Accessors(chain = true)
@Data
public class BatchSendInput {

    /**
     * 执行业务类型
     * 必传,参考 BusinessCode枚举
     */
    private String code;


    /**
     * 消息模板Id
     * 必传
     */
    private Long messageTemplateId;


    /**
     * 消息相关的参数
     * 必传
     */
    private List<MessageParam> messageParamList;
}
