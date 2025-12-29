package com.gzw.kd.vo.output;

import lombok.Data;
import java.util.Date;

@Data
public class MessageOutput {

    private Long id;

    private String title;

    private String content;

    //根据 msgType 转换: system, security, update 等
    private String type;

    private Date time;

    // true-未读 false-已读
    private Boolean unread;
    private String tag; // 根据 msgType 显示标签
    // 接收者ID
    private Long userId;

    // 消息来源 // 渠道类型
    private String channelType;
    // 原始消息类型
    private Integer msgType;
}
