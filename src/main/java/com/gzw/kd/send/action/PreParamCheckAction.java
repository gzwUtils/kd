package com.gzw.kd.send.action;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.gzw.kd.common.Constants;
import com.gzw.kd.common.R;
import com.gzw.kd.common.entity.MessageParam;
import com.gzw.kd.common.enums.ResultCodeEnum;
import com.gzw.kd.send.BusinessProcess;
import com.gzw.kd.send.ProcessContext;
import com.gzw.kd.send.SendTaskModel;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
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
            return;
        }

        // 2.过滤 receiver=null 的messageParam
        List<MessageParam> resultMessageParamList = messageParamList.stream()
                .filter(messageParam -> !StrUtil.isBlank(messageParam.getReceiver()))
                .collect(Collectors.toList());
        if (CollUtil.isEmpty(resultMessageParamList)) {
            context.setNeedBreak(true).setResponse(R.setResult(ResultCodeEnum.PARAM_ABSENT));
            return;
        }

        // 3.过滤receiver大于100的请求
        if (resultMessageParamList.stream().anyMatch(messageParam -> messageParam.getReceiver().split(StrUtil.COMMA).length > Constants.BATCH_RECEIVER_SIZE)) {
            context.setNeedBreak(true).setResponse(R.setResult(ResultCodeEnum.TOO_MANY_RECEIVER));
            return;
        }

        sendTaskModel.setMessageParamList(resultMessageParamList);
    }
}
