package com.gzw.kd.common.utils;

import lombok.Data;

/**
 * @author gzw
 * @description： 过滤 null
 * @since：2023/3/10 15:53
 */
@SuppressWarnings("all")
@Data
public class EsNullValueQueryWrapper<T> {

    /**
     * 原始参数
     */
    private final T originParameter;

    /**
     * 是否检索null值条目
     */
    private boolean queryNullValue;

    public EsNullValueQueryWrapper(T originParameter) {
        this(originParameter, true);
    }

    public EsNullValueQueryWrapper(T originParameter, boolean queryNullValue) {
        this.originParameter = originParameter;
        this.queryNullValue = queryNullValue;
    }
}
