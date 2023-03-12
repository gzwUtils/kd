package com.gzw.kd.common.enums;

import lombok.Getter;

/**
 * @author gzw
 * @version 1.0
 * @Date 2022/10/8
 * @dec
 */

@Getter
public enum UserAdminEnum {

    /**
     * 默认登录 普通用户
     */
    MANAGER(1, "管理员"),
    VIP(0, "普通用户");

    private final Integer status;

    private final String desc;

    UserAdminEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }


    public static String getDesc(Integer status) {
        UserAdminEnum[] values = UserAdminEnum.values();
        for (UserAdminEnum v : values) {
            if (v.getStatus().equals(status)) {
                return v.getDesc();
            }
        }
        return null;
    }
}
