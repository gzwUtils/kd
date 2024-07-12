package com.gzw.kd.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author gzw
 * @description： 模板枚举信息
 * @since：2023/6/8  14:26
 */
@Getter
@AllArgsConstructor
public enum TemplateType implements BaseEnum{

    /**
     * 定时类的模板(后台定时调用)
     */
    CLOCKING(10, "定时类的模板(后台定时调用)"),
    /**
     * 实时类的模板(接口实时调用)
     */
    REALTIME(20, "实时类的模板(接口实时调用)"),
    ;

    private final Integer code;
    private final String description;
}
