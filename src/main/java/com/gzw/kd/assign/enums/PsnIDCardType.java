package com.gzw.kd.assign.enums;

import lombok.Getter;

/**
 * @author 高志伟
 */
@SuppressWarnings("all")
@Getter
public enum PsnIDCardType {


    CH("CRED_PSN_CH_IDCARD","中国大陆居民身份证"),
    HONGKONG("CRED_PSN_CH_HONGKONG","香港来往大陆通行证"),
    MACAO("CRED_PSN_CH_MACAO","澳门来往大陆通行证"),
    TWCARD("CRED_PSN_CH_TWCARD","台湾来往大陆通行证"),
    PASSPORT("CRED_PSN_PASSPORT","护照");

    private final String message;

    private final String desc;


    PsnIDCardType(String message, String desc) {
        this.message = message;
        this.desc = desc;
    }
}
