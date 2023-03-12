package com.gzw.kd.common.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gzw.kd.common.entity.PrivacySerializer;
import com.gzw.kd.common.enums.PrivacyTypeEnum;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author gzw
 * @description： 脱敏注解
 * @since：2023/2/13 10:00
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@JsonSerialize(using = PrivacySerializer.class)
public @interface PrivacyEncrypt {

    /**
     * 脱敏数据类型
     */
    PrivacyTypeEnum type();

    /**
     * 前置不需要打码的长度
     */

    int prefixNoMaskLen() default  1;


    /**
     * 后置不需要打码的长度
     */

    int suffixNoMaskLen() default 1;

    /**
     * 用什么打码
     */

    String symbol() default "*";
}
