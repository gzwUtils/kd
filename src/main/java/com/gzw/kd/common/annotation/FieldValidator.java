package com.gzw.kd.common.annotation;

import com.gzw.kd.common.utils.FieldValid;
import java.lang.annotation.*;
import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * @author gzw
 * @description： 实体类字段校验
 * @since：2023/4/20 20:51
 */
@Documented
@Constraint(validatedBy = FieldValid.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD,ElementType.PARAMETER})
public @interface FieldValidator {

    /**
     * 是否强制校验
     * @return 是否强制校验的boolean值
     */

    boolean required() default true;


    /**
     * 校验不通过时的报错信息
     * @return 校验不通过时的报错信息
     */


    String message() default "内容输入有误";

    /**
     * 将validator进行分类，不同的类group中会执行不同的validator操作
     * @return validator的分类类型
     */

    Class<?> [] groups() default {};

    /**
     * 主要是针对bean，很少使用
     * @return 负载
     */

    Class<? extends Payload> [] payload() default {};


    /**
     *  正则表达式验证
     */

    String regex() default "";
}
