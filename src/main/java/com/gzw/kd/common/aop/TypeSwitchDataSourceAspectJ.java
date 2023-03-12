package com.gzw.kd.common.aop;

import com.gzw.kd.config.datasource.DynamicDataSource;
import com.gzw.kd.config.datasource.annotation.SpecDataSource;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

/**
 * 项目名称：spring-demo
 * 类 名 称：MethodSwitchDataSourceAspectJ
 * 类 描 述：多数据源切换的AOP 作用在类上
 * 创建时间：2022/5/23 6:52 下午
 *
 * @author gzw
 */
@Aspect
@Component
public class TypeSwitchDataSourceAspectJ implements Ordered {

    @Pointcut("@within(com.gzw.kd.config.datasource.annotation.SpecDataSource)")
    public void pointcut() {

    }

    @Before(value = "pointcut() && @within(specDataSource)",argNames = "specDataSource")
    public void before(SpecDataSource specDataSource){
        DynamicDataSource.setDataSourceKey(specDataSource.value());
    }
    @After(value = "pointcut()")
    public void after(){
        DynamicDataSource.cleanDataSourceKey();
    }

    @Override
    public int getOrder() {
        return -2147483646;
    }
}
