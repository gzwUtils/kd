package com.gzw.kd.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public enum MessageSendTypeEnum implements BaseEnum {

    /**
     * 广播 - 发送给所有用户（系统公告）
     */
    BROADCAST(1, "广播"),

    /**
     * 单条 - 发送给单个指定用户
     */
    SINGLE(2, "单条"),

    /**
     * 批量 - 发送给特定用户群体
     */
    BATCH(3, "批量"),

    /**
     * 分组 - 按用户分组/标签发送
     */
    GROUP(4, "分组"),

    /**
     * 条件触发 - 满足特定条件时自动发送
     */
    TRIGGER(5, "条件触发"),

    /**
     * 定时 - 在指定时间发送
     */
    SCHEDULED(6, "定时");

    private final Integer code;
    private final String description;

}
