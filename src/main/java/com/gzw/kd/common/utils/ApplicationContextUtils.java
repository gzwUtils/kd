package com.gzw.kd.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author 高志伟
 */
@Slf4j
@SuppressWarnings("all")
@Component
public class ApplicationContextUtils  implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public  void setApplicationContext(ApplicationContext context) throws BeansException {
            this.applicationContext = context;
    }

    public static ApplicationContext getApplicationContext() {
        assertContextInjected();
        return applicationContext;
    }



    public static <T> T getBean(Class<T> requiredType){
        assertContextInjected();
        return getApplicationContext().getBean(requiredType);
    }

    public static <T> T getBean(String name){
        assertContextInjected();
        return (T) getApplicationContext().getBean(name);
    }

    public static void clearHolder() {
        if (log.isDebugEnabled()){
            log.debug("清除SpringContextHolder中的ApplicationContext:" + applicationContext);
        }
        applicationContext = null;
    }

    private static void assertContextInjected() {
        Validate.validState(applicationContext != null, "applicaitonContext属性未注入, 请检查");
    }
}
