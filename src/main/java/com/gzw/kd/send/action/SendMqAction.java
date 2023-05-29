package com.gzw.kd.send.action;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.base.Throwables;
import com.gzw.kd.common.MqConstant;
import com.gzw.kd.common.R;
import com.gzw.kd.common.enums.BusinessCode;
import com.gzw.kd.mq.eventBus.service.SendMqService;
import com.gzw.kd.send.BusinessProcess;
import com.gzw.kd.send.ProcessContext;
import com.gzw.kd.send.SendTaskModel;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author gzw
 * @description： 发送消息到mq
 * @since：2023/5/25 22:14
 */


@Slf4j
@Component
public class SendMqAction implements BusinessProcess<SendTaskModel> {

    @Resource
    private SendMqService sendMqService;

    @Value("${kd.mq.pipeline}")
    private String mqPipeline;


    @Override
    public void process(ProcessContext<SendTaskModel> context) {
        SendTaskModel sendTaskModel = context.getProcessModel();
        try {
            if (BusinessCode.COMMON_SEND.getCode().equals(context.getCode())) {
                String message = JSON.toJSONString(sendTaskModel.getTaskInfo(), SerializerFeature.WriteClassName);
                sendMqService.send(MqConstant.Topic.KD_BUSINESS_TOPIC, message, MqConstant.TagId.KD_BUSINESS_TAG_ID);
            } else if (BusinessCode.RECALL.getCode().equals(context.getCode())) {
                String message = JSON.toJSONString(sendTaskModel.getTemplateInfo(), SerializerFeature.WriteClassName);
                sendMqService.send(MqConstant.Topic.KD_BUSINESS_RECALL_TOPIC, message, MqConstant.TagId.KD_BUSINESS_TAG_ID);
            }
        } catch (Exception e) {
            context.setNeedBreak(true).setResponse(R.error());
            log.error("send {} fail! e:{},params:{}", mqPipeline, Throwables.getStackTraceAsString(e)
                    , JSON.toJSONString(CollUtil.getFirst(sendTaskModel.getTaskInfo().listIterator())));
        }
    }
}
