package com.gzw.kd.assign.enums;

import lombok.Getter;

/**
 * @author 高志伟
 */
@Getter
public enum PsnAuthModel {

    /**
     * 认证方式
     */

    FACE("PSN_FACE","人脸识别认证"),
    MOBILE("PSN_MOBILE3","手机运营商三要素认证"),
    BANKCARD("PSN_BANKCARD4","银行卡四要素认证");

    private final String message;

    private final String desc;

    PsnAuthModel(String message, String desc) {
        this.message = message;
        this.desc = desc;
    }
}
