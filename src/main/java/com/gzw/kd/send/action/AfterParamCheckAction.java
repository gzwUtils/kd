package com.gzw.kd.send.action;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReUtil;
import com.alibaba.fastjson.JSON;
import com.gzw.kd.common.Constants;
import com.gzw.kd.common.R;
import com.gzw.kd.common.entity.TaskInfo;
import com.gzw.kd.common.enums.IdType;
import com.gzw.kd.common.enums.ResultCodeEnum;
import com.gzw.kd.send.BusinessProcess;
import com.gzw.kd.send.ProcessContext;
import com.gzw.kd.send.SendTaskModel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author gzw
 * @description： 后置参数检查
 * @since：2023/7/15 10:25
 */

@Slf4j
@Component
public class AfterParamCheckAction implements BusinessProcess<SendTaskModel> {

    public static final HashMap<Integer, String> CHANNEL_REGEX_EXP = new HashMap<>();

    static {
        CHANNEL_REGEX_EXP.put(IdType.PHONE.getCode(), Constants.PHONE_REGEX_EXP);
        CHANNEL_REGEX_EXP.put(IdType.EMAIL.getCode(), Constants.EMAIL_REGEX_EXP);
    }
    @Override
    public void process(ProcessContext<SendTaskModel> context) {
        SendTaskModel sendTaskModel = context.getProcessModel();
        List<TaskInfo> taskInfo = sendTaskModel.getTaskInfo();

        // 1. 过滤掉不合法的手机号、邮件
        filterIllegalReceiver(taskInfo);

        if (CollUtil.isEmpty(taskInfo)) {
            context.setNeedBreak(true).setResponse(R.setResult(ResultCodeEnum.PARAM_ERROR));
        }
    }


    /**
     * 如果指定类型是手机号，检测输入手机号是否合法
     * 如果指定类型是邮件，检测输入邮件是否合法
     *
     * @param taskInfo info
     */
    private void filterIllegalReceiver(List<TaskInfo> taskInfo) {
        Integer idType = CollUtil.getFirst(taskInfo.iterator()).getIdType();
        filter(taskInfo, CHANNEL_REGEX_EXP.get(idType));
    }

    /**
     * 利用正则过滤掉不合法的接收者
     *
     * @param taskInfo info
     * @param regexExp rex
     */
    private void filter(List<TaskInfo> taskInfo, String regexExp) {
        Iterator<TaskInfo> iterator = taskInfo.iterator();
        while (iterator.hasNext()) {
            TaskInfo task = iterator.next();
            Set<String> illegalPhone = task.getReceiver().stream()
                    .filter(phone -> !ReUtil.isMatch(regexExp, phone))
                    .collect(Collectors.toSet());

            if (CollUtil.isNotEmpty(illegalPhone)) {
                task.getReceiver().removeAll(illegalPhone);
                log.error("messageTemplateId:{} find illegal receiver!{}", task.getMessageTemplateId(), JSON.toJSONString(illegalPhone));
            }
            if (CollUtil.isEmpty(task.getReceiver())) {
                iterator.remove();
            }
        }
    }

}
