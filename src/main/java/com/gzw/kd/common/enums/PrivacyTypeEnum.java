package com.gzw.kd.common.enums;

import lombok.Getter;

/**
 * @author gzw
 * @description： 隐私数据类型枚举
 * @since：2023/2/13 09:56
 */

@Getter
public enum PrivacyTypeEnum {

    /** 自定义 该项需设置脱敏的范围*/
    CUSTOMER,
    /** 姓名*/
    NAME,
    /** 身份证*/
    ID_CARD,
    /** 手机号*/
    PHONE,
    /** 邮箱*/
    EMAIL
}
