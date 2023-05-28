package com.gzw.kd.send.action;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.base.Throwables;
import static com.gzw.kd.common.Constants.LINK_NAME;
import com.gzw.kd.common.R;
import com.gzw.kd.common.entity.ContentModel;
import com.gzw.kd.common.entity.MessageParam;
import com.gzw.kd.common.enums.BusinessCode;
import com.gzw.kd.common.enums.ChannelTypeEnum;
import com.gzw.kd.common.enums.TemplateStatusEnum;
import com.gzw.kd.common.utils.ContentHolderUtil;
import com.gzw.kd.common.entity.TaskInfo;
import com.gzw.kd.common.entity.TemplateInfo;
import com.gzw.kd.common.utils.SnowFlakeIdUtils;
import com.gzw.kd.send.BusinessProcess;
import com.gzw.kd.send.ProcessContext;
import com.gzw.kd.send.SendTaskModel;
import com.gzw.kd.service.MessageTemplateService;
import java.lang.reflect.Field;
import java.util.*;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author gzw
 * @description： 拼接参数
 * @since：2023/5/25 22:29
 */
@SuppressWarnings("all")
@Slf4j
@Component
public class AssembleAction implements BusinessProcess<SendTaskModel> {

    @Resource
    private MessageTemplateService messageTemplateService;


    @Override
    public void process(ProcessContext<SendTaskModel> context) {

        SendTaskModel sendTaskModel = context.getProcessModel();
        Long messageTemplateId = sendTaskModel.getMessageTemplateId();

        try {
            TemplateInfo messageTemplate = messageTemplateService.selectById(messageTemplateId.intValue());
            if (ObjectUtil.isEmpty(messageTemplate) || messageTemplate.getIsDeleted().equals(TemplateStatusEnum.STOP.getStatus())) {
                context.setNeedBreak(true).setResponse(R.error());
                return;
            }
            if (BusinessCode.COMMON_SEND.getCode().equals(context.getCode())) {
                List<TaskInfo> taskInfos = assembleTaskInfo(sendTaskModel, messageTemplate);
                sendTaskModel.setTaskInfo(taskInfos);
            } else if (BusinessCode.RECALL.getCode().equals(context.getCode())) {
                sendTaskModel.setTemplateInfo(messageTemplate);
            }
        } catch (Exception e) {
            context.setNeedBreak(true).setResponse(R.error());
            log.error("assemble task fail! templateId:{}, e:{}", messageTemplateId, Throwables.getStackTraceAsString(e));
        }

    }


    /**
     * 组装 TaskInfo 任务消息
     *
     * @param sendTaskModel 组装模型
     * @param templateInfo 模版消息
     */
    private List<TaskInfo> assembleTaskInfo(SendTaskModel sendTaskModel, TemplateInfo templateInfo) {
        List<MessageParam> messageParamList = sendTaskModel.getMessageParamList();
        List<TaskInfo> taskInfoList = new ArrayList<>();

        for (MessageParam messageParam : messageParamList) {

            TaskInfo taskInfo = TaskInfo.builder()
                    .messageTemplateId(templateInfo.getId())
                    .businessId(SnowFlakeIdUtils.generatorId())
                    .receiver(new HashSet<>(Arrays.asList(messageParam.getReceiver().split(String.valueOf(StrUtil.C_COMMA)))))
                    .idType(templateInfo.getIdType())
                    .sendChannel(templateInfo.getSendChannel())
                    .templateType(templateInfo.getTemplateType())
                    .msgType(templateInfo.getMsgType())
                    .shieldType(templateInfo.getShieldType())
                    .sendAccount(templateInfo.getSendAccount())
                    .contentModel(getContentModelValue(templateInfo, messageParam)).build();

            taskInfoList.add(taskInfo);
        }

        return taskInfoList;

    }


    /**
     * 获取 contentModel，替换模板msgContent中占位符信息
     */
    private static String getContentModelValue(TemplateInfo messageTemplate, MessageParam messageParam) {

        // 得到真正的ContentModel 类型
        Integer sendChannel = messageTemplate.getSendChannel();
        Class<? extends ContentModel> contentModelClass = ChannelTypeEnum.getContextModel(sendChannel);

        // 得到模板的 msgContent 和 入参
        Map<String, String> variables = messageParam.getVariables();
        JSONObject jsonObject = JSON.parseObject(messageTemplate.getMsgContent());


        // 通过反射 组装出 contentModel
        Field[] fields = ReflectUtil.getFields(contentModelClass);
        ContentModel contentModel = ReflectUtil.newInstance(contentModelClass);
        for (Field field : fields) {
            String originValue = jsonObject.getString(field.getName());

            if (StrUtil.isNotBlank(originValue)) {
                String resultValue = ContentHolderUtil.replacePlaceHolder(originValue, variables);
                Object resultObj = JSONUtil.isJsonObj(resultValue) ? JSONUtil.toBean(resultValue, field.getType()) : resultValue;
                ReflectUtil.setFieldValue(contentModel, field, resultObj);
            }
        }

        // 如果 url 字段存在，则在url拼接对应的埋点参数
        String url = (String) ReflectUtil.getFieldValue(contentModel, LINK_NAME);
        if (StrUtil.isNotBlank(url)) {
            ReflectUtil.setFieldValue(contentModel, LINK_NAME, url);
        }
        return JSON.toJSONString(contentModel, SerializerFeature.WriteClassName);
    }
}
