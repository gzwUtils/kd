package com.gzw.kd.common.annotation;

import static com.gzw.kd.common.Constants.STRING_EMPTY;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author gzw
 * @description： es搜索注解, 用于拼接query参数
 * @since：2023/3/10 15:24
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EsSearchCondition {

    /**
     * @return es中字段名称
     */
    String fieldName();

    /**
     * @return 是否为range查询, 默认为false
     */
    boolean isRangeQuery() default false;

    /**
     * @return 多字段配置
     */
    String fieldsConfig() default STRING_EMPTY;

    /**
     * @return 检索时，值是否需要转为小写
     */
    boolean toLowerCase() default false;
}
