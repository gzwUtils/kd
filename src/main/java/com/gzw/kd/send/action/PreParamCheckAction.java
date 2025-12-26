package com.gzw.kd.send.action;

import cn.hutool.core.collection.CollUtil;
import com.gzw.kd.common.R;
import com.gzw.kd.common.entity.MessageParam;
import com.gzw.kd.common.enums.ResultCodeEnum;
import com.gzw.kd.send.BusinessProcess;
import com.gzw.kd.send.ProcessContext;
import com.gzw.kd.send.SendTaskModel;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author gzw
 * @description： 前置参数校验
 * @since：2023/7/15 10:15
 */
@Slf4j
@Component
public class PreParamCheckAction implements BusinessProcess<SendTaskModel> {


    @Override
    public void process(ProcessContext<SendTaskModel> context) {
        SendTaskModel sendTaskModel = context.getProcessModel();
        Long messageTemplateId = sendTaskModel.getMessageTemplateId();
        List<MessageParam> messageParamList = sendTaskModel.getMessageParamList();

        // 1.没有传入 消息模板Id 或者 messageParam
        if (Objects.isNull(messageTemplateId) || CollUtil.isEmpty(messageParamList)) {
            context.setNeedBreak(true).setResponse(R.setResult(ResultCodeEnum.EMPTY_RECEIVER));
        }
    }
}
