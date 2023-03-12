package com.gzw.kd.common.enums;

import lombok.Getter;

/**
 * @author gzw
 * @version 1.0
 * @Date 2022/10/8
 * @dec
 */

@Getter
public enum UserStatusEnum {

    /**
     * 用户状态
     */

    START(0,"启用"),
    STOP(1,"停用");

    private final int  status;

    private final String desc;

    UserStatusEnum(int status, String desc) {
        this.status = status;
        this.desc = desc;
    }


    public static String getDesc(int status) {
        UserStatusEnum[] values = UserStatusEnum.values();
        for (UserStatusEnum v : values) {
            if (v.getStatus() == status) {
                return v.getDesc();
            }
        }
        return null;
    }
}
