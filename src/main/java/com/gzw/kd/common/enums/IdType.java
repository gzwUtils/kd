package com.gzw.kd.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author gzw
 * @description： 发送ID类型枚举
 * @since：2023/7/15 10:32
 */
@ToString
@AllArgsConstructor
@Getter
public enum IdType implements BaseEnum{
    /**
     * userId
     */
    USER_ID(10, "userId"),
    /**
     * 手机设备号
     */
    DID(20, "did"),
    /**
     * 手机号
     */
    PHONE(30, "phone"),
    /**
     * 微信体系的openId
     */
    OPEN_ID(40, "openId"),
    /**
     * 邮件
     */
    EMAIL(50, "email");


    private final Integer code;
    private final String description;
}
