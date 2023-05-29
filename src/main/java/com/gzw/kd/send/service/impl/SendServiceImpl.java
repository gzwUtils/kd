package com.gzw.kd.send.service.impl;

import com.gzw.kd.common.R;
import com.gzw.kd.send.ProcessContext;
import com.gzw.kd.send.ProcessControl;
import com.gzw.kd.send.SendTaskModel;
import com.gzw.kd.send.service.SendService;
import com.gzw.kd.vo.input.BatchSendInput;
import com.gzw.kd.vo.input.SendInput;
import java.util.Collections;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * @author gzw
 * @description：
 * @since：2023/5/26 00:02
 */
@SuppressWarnings("all")
@Service
public class SendServiceImpl implements SendService {

    @Resource
    private ProcessControl processControl;

    @Override
    public R send(SendInput sendInput) {
        SendTaskModel sendTaskModel = SendTaskModel.builder()
                .messageTemplateId(sendInput.getMessageTemplateId())
                .messageParamList(Collections.singletonList(sendInput.getMessageParam()))
                .build();

        ProcessContext context = ProcessContext.builder()
                .code(sendInput.getCode())
                .processModel(sendTaskModel)
                .needBreak(false)
                .response(R.ok()).build();

        ProcessContext process = processControl.process(context);

        return process.getResponse();

    }

    @Override
    public R batchSend(BatchSendInput batchSendInput) {
        SendTaskModel sendTaskModel = SendTaskModel.builder()
                .messageTemplateId(batchSendInput.getMessageTemplateId())
                .messageParamList(batchSendInput.getMessageParamList())
                .build();

        ProcessContext context = ProcessContext.builder()
                .code(batchSendInput.getCode())
                .processModel(sendTaskModel)
                .needBreak(false)
                .response(R.ok()).build();

        ProcessContext process = processControl.process(context);
        return process.getResponse();
    }
}
