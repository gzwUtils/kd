package com.gzw.kd.vo.output;


import lombok.Data;

@Data
public class MessageCountOutput {

    private Integer total;
    private Integer unread;
    // 系统通知
    private Integer system;

    // 告警
    private Integer security;

    // 公告
    private Integer update;
}
