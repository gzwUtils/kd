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
public enum EventTypeEnum {

    /**
     * 事件类型
     */

    SUB("subscribe","订阅"),
    UN_SUB("unsubscribe","取消订阅"),
    HETONG("hetong","合同"),
    TEMPLATE("TEMPLATESENDJOBFINISH","模板返回结果"),
    LOCATION("LOCATION","上报地理位置");

    private final String  code;

    private final String desc;

    EventTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }


    public static String getDesc(String code) {
        EventTypeEnum[] values = EventTypeEnum.values();
        for (EventTypeEnum v : values) {
            if (v.getCode().equals(code)) {
                return v.getDesc();
            }
        }
        return null;
    }
}
