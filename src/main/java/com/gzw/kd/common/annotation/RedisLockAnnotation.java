package com.gzw.kd.common.annotation;

import com.gzw.kd.common.enums.RedisLockTypeEnum;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author gzw
 * @description： redis lock annotation
 * @since：2023/2/15 18:33
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE,ElementType.METHOD})
public @interface RedisLockAnnotation {

    /**
     * 特定参数识别 默认取第0个下标
     */

    int lockField() default  0;

    /**
     * 超时重试次数
     */

    int tryCount() default  3;

    /**
     * 自定义加锁类型
     */

    RedisLockTypeEnum typeEnum();

    /**
     * 锁定时间 秒 s
     */

    long lockTime() default 10;
}
