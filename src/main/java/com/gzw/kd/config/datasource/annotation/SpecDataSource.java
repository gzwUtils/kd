package com.gzw.kd.config.datasource.annotation;

import com.gzw.kd.common.enums.DataSourceEnum;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 项目名称：spring-demo
 * 类 名 称：SpecDataSource
 * 类 描 述：多数据源指定某一数据源的注解
 * 创建时间：2022/5/23 6:48 下午
 *
 * @author gzw
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.TYPE})
public @interface SpecDataSource {

    DataSourceEnum value() default DataSourceEnum.MASTER;
}
