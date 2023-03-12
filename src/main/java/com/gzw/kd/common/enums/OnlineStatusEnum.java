package com.gzw.kd.common.enums;

import lombok.Getter;

/**
 * @author gzw
 * @version 1.0
 * @Date 2022/10/8
 * @dec
 */

@Getter
public enum OnlineStatusEnum {

    /**
     * 用户在线 离线
     */

    ON_LINE(1000,"上线"),
    OFF_LINE(1001,"离线");

    private final int  status;

    private final String desc;

    OnlineStatusEnum(int status, String desc) {
        this.status = status;
        this.desc = desc;
    }


    public static String getDesc(int status) {
        OnlineStatusEnum[] values = OnlineStatusEnum.values();
        for (OnlineStatusEnum v : values) {
            if (v.getStatus() == status) {
                return v.getDesc();
            }
        }
        return null;
    }
}
