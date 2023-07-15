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
public enum MessageType implements BaseEnum{

    /**
     * 通知类消息
     */
    NOTICE(10, "通知类消息", "notice"),
    /**
     * 营销类消息
     */
    MARKETING(20, "营销类消息", "marketing"),
    /**
     * 验证码消息
     */
    AUTH_CODE(30, "验证码消息", "auth_code");

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
