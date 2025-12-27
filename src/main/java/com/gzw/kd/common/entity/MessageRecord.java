package com.gzw.kd.common.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.*;

@Data
@Accessors(chain = true)
public class MessageRecord {

    private Long id;                    // 主键ID
    private String bizId;              // 业务ID
    private Long templateId;           // 模板ID
    private String channelType;        // 渠道类型
    private Integer messageType;       // 消息类型：1-广播 2-单条 3-批量
    private String receiver;           // 主要接收者
    private String receiverCount;     // 接收者总数
    private String title;              // 消息标题
    private String content;            // 消息内容
    private Integer delayMinutes;      // 延迟分钟数
    private Date scheduleTime;         // 计划发送时间
    private Integer retryCount;        // 重试次数
    private Integer status;            // 状态：MessageStatusEnum
    private Integer successCount;      // 成功数量
    private Integer failCount;         // 失败数量
    private Date sendTime;             // 发送时间
    private Date createTime;           // 创建时间
}