package com.gzw.kd.common.enums;

/**
 * @author gzw
 * @description：
 * @since：2023/6/5 17:32
 */
public interface BaseEnum {

    /**
     * 获取枚举的code
     *
     * @return code
     */
    Integer getCode();

    /**
     * 获取枚举的描述
     *
     * @return desc
     */
    String getDescription();
}
