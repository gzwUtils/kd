package com.gzw.kd.common.entity;

import lombok.Data;
import java.util.Date;

@Data
public class MessageReceiverDetail {

    private Long id;                // 主键ID
    private Long recordId;          // 记录ID
    private String receiver;        // 接收者
    private Integer readStatus;     // 阅读状态：0-未读 1-已读
    private Date readTime;          // 阅读时间
    private Date createTime;        // 创建时间
}