package com.gzw.kd.learn.dubbo.config;

import org.springframework.beans.factory.FactoryBean;

/**
 * @author gzw
 * @description：
 * @since：2023/5/11 11:06
 */
@SuppressWarnings("all")
public class ReferenceBean<T> extends ReferenceConfig<T> implements FactoryBean {
    @Override
    public Object getObject() throws Exception {
        return getRef();
    }

    @Override
    public Class<?> getObjectType() {
        return getInterfaceClass();
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
