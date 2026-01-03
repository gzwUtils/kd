package com.gzw.kd.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author gzw
 * @description： 发送的消息类型
 * @since：2023/7/15 13:18
 */

@Getter
@ToString
@AllArgsConstructor
public enum MessageContentTypeEnum implements BaseEnum{

    /**
     * 通知类消息
     */
    NOTICE(10, "系统通知", "notice"),
    /**
     * 营销类消息
     */
    MARKETING(20, "营销", "marketing"),
    /**
     * 验证码消息
     */
    AUTH_CODE(30, "验证码", "auth_code"),


    /**
     * 安全告警类消息
     */
    ALERT_CODE(40, "告警", "alert_code"),


    /**
     * 更新公告
     */
     UPDATE_NOTICE(50, "公告", "update_notice"),
    ;

    /**
     * 编码值
     */
    private final Integer code;

    /**
     * 描述
     */
    private final String description;


    /**
     * 英文标识
     */
    private final String codeEn;
}
