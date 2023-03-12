package com.gzw.kd.common.enums;

import lombok.Getter;

/**
 * @author gzw
 * @version 1.0
 * @Date 2022/10/8
 * @dec  文件管理状态枚举
 */
@SuppressWarnings("all")
@Getter
public enum StatusEnum {

    DRAFT(1,"开始"),
    APPROVE(2,"待审批"),
    REVIEW_DRIFT(3,"待确认"),
    END(4,"结束");

    private final int  status;

    private final String desc;

    StatusEnum(int status, String desc) {
        this.status = status;
        this.desc = desc;
    }


    public static String getDesc(int status) {
        StatusEnum[] values = StatusEnum.values();
        for (StatusEnum v : values) {
            if (v.getStatus() == status) {
                return v.getDesc();
            }
        }
        return null;
    }
}
