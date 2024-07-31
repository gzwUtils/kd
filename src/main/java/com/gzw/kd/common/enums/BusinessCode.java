package com.gzw.kd.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author gzw
 * @description： 流程标识
 * @since：2023/5/25 22:24
 */

@Getter
@AllArgsConstructor
public enum BusinessCode implements BaseEnum {

    /**
     * 普通发送流程
     */
    COMMON_SEND("send", "普通发送"),

    /**
     * 撤回流程
     */
    RECALL("recall", "撤回消息");


    /**
     * code 关联着责任链的模板
     */
    private final String code;

    /**
     * 类型说明
     */
    private final String description;
}
