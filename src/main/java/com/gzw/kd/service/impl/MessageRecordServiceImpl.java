package com.gzw.kd.service.impl;

import com.alibaba.fastjson.JSON;
import com.gzw.kd.common.Constants;
import com.gzw.kd.common.entity.*;
import com.gzw.kd.common.enums.ChannelTypeEnum;
import com.gzw.kd.common.enums.EnumUtils;
import com.gzw.kd.common.enums.MessageSendTypeEnum;
import com.gzw.kd.common.enums.MessageStatusEnum;
import com.gzw.kd.common.utils.ContextUtil;
import com.gzw.kd.mapper.MessageRecordMapper;
import com.gzw.kd.service.MessageRecordService;
import com.gzw.kd.service.MessageTemplateService;
import com.gzw.kd.vo.output.SendRecordVo;
import com.gzw.kd.vo.output.TemplateVo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class MessageRecordServiceImpl implements MessageRecordService {


    private final MessageRecordMapper messageRecordMapper;


    private final MessageTemplateService messageTemplateService;


    @Override
    public Long createRecord(TaskInfo taskInfo) {
        try {
            MessageRecord messageRecord = new MessageRecord();
            messageRecord.setBizId(String.valueOf(taskInfo.getBusinessId()));
            messageRecord.setTemplateId(taskInfo.getMessageTemplateId());
            messageRecord.setChannelType(ChannelTypeEnum.getDescription(taskInfo.getSendChannel()));

            // 消息类型
            if (taskInfo.getReceiver() == null || taskInfo.getReceiver().isEmpty() || taskInfo.getReceiver().toString().isEmpty()) {
                messageRecord.setMessageType(MessageSendTypeEnum.BROADCAST.getCode());
            } else if (taskInfo.getReceiver().size() == 1) {
                messageRecord.setMessageType(MessageSendTypeEnum.SINGLE.getCode());
                messageRecord.setReceiver(taskInfo.getReceiver().iterator().next());
            } else {
                messageRecord.setMessageType(MessageSendTypeEnum.BATCH.getCode());
                messageRecord.setReceiver(taskInfo.getReceiver().iterator().next());
            }

            messageRecord.setReceiverCount(taskInfo.getReceiver() != null ? taskInfo.getReceiver().size() : 0);
            ContentModel contentModel = JSON.parseObject(taskInfo.getContentModel(), ContentModel.class);
            messageRecord.setContent(contentModel.getContent());

            // 延迟信息
            messageRecord.setDelayMinutes(taskInfo.getDelayMinutes().intValue());
            if (taskInfo.getDelayMinutes() > 0) {
                messageRecord.setScheduleTime(new Date(System.currentTimeMillis() +
                        taskInfo.getDelayMinutes() * 60 * 1000));
            }

            messageRecord.setStatus(MessageStatusEnum.SENDING.getCode()); // 发送中
            messageRecord.setCreateTime(new Date());

            int result = messageRecordMapper.insertRecord(messageRecord);
            if (result > 0) {
                log.info("消息记录创建成功，ID: {}, 业务ID: {}", messageRecord.getId(), messageRecord.getBizId());
                return messageRecord.getId();
            }
            return 0L;
        } catch (Exception e) {
            log.error("创建消息记录失败，业务ID: {}", taskInfo.getBusinessId(), e);
            return 0L;
        }
    }

    @Override
    public boolean updateStatus(Long recordId, boolean success, int successCount, int failCount) {
        try {
            MessageRecord messageRecord = messageRecordMapper.selectById(recordId);
            if (messageRecord == null) {
                log.error("消息记录不存在，ID: {}", recordId);
                return false;
            }

            messageRecord.setStatus(success ? MessageStatusEnum.SEND_SUCCESS.getCode() : MessageStatusEnum.SEND_FAIL.getCode());
            messageRecord.setSuccessCount(successCount);
            messageRecord.setFailCount(failCount);
            messageRecord.setSendTime(new Date());

            int result = messageRecordMapper.updateStatistics(recordId, successCount, failCount);
            messageRecordMapper.updateStatus(recordId, messageRecord.getStatus(), messageRecord.getSendTime());

            return result > 0;
        } catch (Exception e) {
            log.error("更新发送结果失败，记录ID: {}", recordId, e);
            return false;
        }
    }

    @Override
    public List<SendRecordVo> selectByAccount() {
        List<SendRecordVo> result = new ArrayList<>();
        Operator operator = (Operator) ContextUtil.getHttpRequest().getSession().getAttribute(Constants.LOGIN_USER_SESSION_KEY);
        List<TemplateVo> allByAccount = messageTemplateService.findAllByAccount(operator.getAccount());
        allByAccount.forEach(info -> {
            List<MessageRecord> messageRecords = messageRecordMapper.selectByTemplateId(info.getId());
            messageRecords.forEach(record -> {
                SendRecordVo vo = new SendRecordVo();
                vo.setTemplateId(info.getId());
                vo.setTime(record.getSendTime());
                vo.setStatus(EnumUtils.getDescriptionByCode(record.getStatus(), MessageStatusEnum.class));
                vo.setReceiver(record.getReceiver());
                result.add(vo);
            });
        });

        return result;
    }


}
