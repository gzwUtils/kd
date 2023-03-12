package com.gzw.kd.common.annotation;

import java.lang.annotation.*;

/**
 * @author gzw
 * @version 1.0
 * @Date 2022/10/12
 * @dec
 */
@Inherited
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Resubmit {

    /**是否开启限制*/
    boolean enable() default true;

    /**频繁 请求限制次数*/
    int limit() default  20;

    /**入黑名单的限制次数*/
    int blackListLimit() default 100;

    /**时间段(s) （尽量>5s 用于redis操作）*/
    long seconds() default 60;

}
