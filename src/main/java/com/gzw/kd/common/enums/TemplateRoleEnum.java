package com.gzw.kd.common.enums;
import lombok.Getter;

/**
 * @author gzw
 * @version 1.0
 * @Date 2022/10/8
 * @dec
 */
@SuppressWarnings("all")
@Getter
public enum TemplateRoleEnum {

    /**
     * 模板用途
     */

    GUAN_ZHU(0,"关注"),
    NOTICE(1,"通知"),
    REMIND(2,"提醒"),
    OTHER(3,"其他");

    private final int  status;

    private final String desc;

    TemplateRoleEnum(int status, String desc) {
        this.status = status;
        this.desc = desc;
    }


    public static String getDesc(int status) {
        TemplateRoleEnum[] values = TemplateRoleEnum.values();
        for (TemplateRoleEnum v : values) {
            if (v.getStatus() == status) {
                return v.getDesc();
            }
        }
        return null;
    }


    public static Integer getStatus(String desc) {
        TemplateRoleEnum[] values = TemplateRoleEnum.values();
        for (TemplateRoleEnum v : values) {
            if (desc.contains(v.getDesc())) {
                return v.getStatus();
            }
        }
        return null;
    }
}
