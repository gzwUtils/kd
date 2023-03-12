package com.gzw.kd.common.enums;

import lombok.Getter;

/**
 * @author gzw
 * @version 1.0
 * @Date 2022/10/8
 * @dec
 */

@Getter
public enum MenuTypeEnum {

    /**
     * 菜单类型
     */

    CLICK("click","推送消息类型为 event 的结构"),
    VIEW("view","跳转 URL");

    private final String  name;

    private final String desc;

    MenuTypeEnum(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }


    public static String getDesc(String name) {
        MenuTypeEnum[] values = MenuTypeEnum.values();
        for (MenuTypeEnum v : values) {
            if (v.getName().equals(name)) {
                return v.getDesc();
            }
        }
        return null;
    }
}
