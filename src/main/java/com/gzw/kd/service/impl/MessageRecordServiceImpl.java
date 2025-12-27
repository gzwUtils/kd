package com.gzw.kd.service.impl;

import com.alibaba.fastjson.JSON;
import com.gzw.kd.common.Constants;
import com.gzw.kd.common.entity.*;
import com.gzw.kd.common.enums.*;
import com.gzw.kd.common.utils.ContextUtil;
import com.gzw.kd.mapper.MessageRecordMapper;
import com.gzw.kd.mapper.UserMapper;
import com.gzw.kd.service.MessageRecordService;
import com.gzw.kd.service.MessageTemplateService;
import com.gzw.kd.vo.output.SendRecordVo;
import com.gzw.kd.vo.output.TemplateVo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import static com.gzw.kd.common.enums.MessageSendTypeEnum.BROADCAST;

@Slf4j
@AllArgsConstructor
@Service
public class MessageRecordServiceImpl implements MessageRecordService {


    private final MessageRecordMapper messageRecordMapper;


    private final MessageTemplateService messageTemplateService;


    private final UserMapper userMapper;


    @Override
    public Long createRecord(TaskInfo taskInfo) {
        try {
            MessageRecord messageRecord = new MessageRecord();
            messageRecord.setBizId(String.valueOf(taskInfo.getBusinessId()));
            messageRecord.setTemplateId(taskInfo.getMessageTemplateId());
            messageRecord.setChannelType(ChannelTypeEnum.getDescription(taskInfo.getSendChannel()));

            // 过滤掉空值
            Set<String> collected = taskInfo.getReceiver().stream()
                    .filter(r -> r != null && !r.trim().isEmpty())
                    .collect(Collectors.toSet());
            // 消息类型
            if (collected.isEmpty()) {
                messageRecord.setMessageType(BROADCAST.getCode());
            } else if (collected.size() == 1) {
                messageRecord.setMessageType(MessageSendTypeEnum.SINGLE.getCode());
                messageRecord.setReceiver(collected.iterator().next());
            } else {
                messageRecord.setMessageType(MessageSendTypeEnum.BATCH.getCode());
                messageRecord.setReceiver(String.join(",", collected));
            }

            messageRecord.setReceiverCount(collected.isEmpty() ? "all" : String.valueOf(collected.size()));
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
        Operator operator = (Operator) ContextUtil.getHttpRequest().getSession()
                .getAttribute(Constants.LOGIN_USER_SESSION_KEY);

        // 1. 查询用户模板
        List<TemplateVo> templates = messageTemplateService.findAllByAccount(operator.getAccount());
        if (templates.isEmpty()) {
            return new ArrayList<>();
        }

        // 2. 查询所有消息记录
        List<Long> templateIds = templates.stream()
                .map(TemplateVo::getId)
                .collect(Collectors.toList());

        List<MessageRecord> records = new ArrayList<>();
        templateIds.forEach(t -> {
            List<MessageRecord> messageRecords = messageRecordMapper.selectByTemplateId(t);
            records.addAll(messageRecords);
        });
        Map<Long, TemplateVo> templateMap = templates.stream()
                .collect(Collectors.toMap(TemplateVo::getId, Function.identity()));

        // 3. 转换结果

        return records.stream()
                .map(messageRecord -> convertToVo(messageRecord, templateMap.get(messageRecord.getTemplateId())))
                .sorted((a, b) -> b.getTime().compareTo(a.getTime()))
                .collect(Collectors.toList());
    }


    /**
     * 转换单个记录
     */
    private SendRecordVo convertToVo(MessageRecord messageRecord, TemplateVo template) {
        SendRecordVo vo = new SendRecordVo();
        vo.setTemplateId(messageRecord.getTemplateId());
        vo.setTime(messageRecord.getSendTime());
        vo.setStatus(EnumUtils.getDescriptionByCode(messageRecord.getStatus(), MessageStatusEnum.class));
        vo.setId(messageRecord.getId());
        // 设置接收者显示
        if (messageRecord.getReceiver() == null || messageRecord.getReceiver().isEmpty()) {
            vo.setReceiver(BROADCAST.getDescription());
        } else if (IdType.USER_ID.getCode().equals(Integer.valueOf(template.getIdType()))) {
            // 用户类型，查询用户信息
            String receiverDisplay = formatUserReceiver(messageRecord.getReceiver());
            vo.setReceiver(receiverDisplay);
        } else {
            // 其他类型，直接显示
            vo.setReceiver(messageRecord.getReceiver());
        }

        return vo;
    }


    /**
     * 格式化用户接收者
     */
    private String formatUserReceiver(String receiver) {
        if (receiver == null || receiver.isEmpty()) {
            return "无接收者";
        }

        String[] ids = receiver.split(",");
        List<String> displayNames = new ArrayList<>();

        for (String id : ids) {
            String trimmedId = id.trim();
            if (!trimmedId.isEmpty()) {
                try {
                    int userId = Integer.parseInt(trimmedId);
                    User user = userMapper.getUserById(userId);
                    if (user != null) {
                        displayNames.add(user.getAccount());
                    } else {
                        displayNames.add("未知用户" + userId);
                    }
                } catch (Exception e) {
                    displayNames.add(trimmedId);
                }
            }
        }
        return String.join(", ", displayNames);
    }
}
