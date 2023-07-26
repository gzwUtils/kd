package com.gzw.kd.common.annotation;

import java.lang.annotation.*;

/**
 * @author 高志伟
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.TYPE})
public @interface OperatorLog {

    /**
     * 日志类型
     * @return type
     */
    String value() default "";

    /**
     * 描述信息，非必填
     */
    String description() default "";
}
