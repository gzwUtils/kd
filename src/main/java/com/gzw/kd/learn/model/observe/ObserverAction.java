package com.gzw.kd.learn.model.observe;

import java.lang.reflect.Method;
import lombok.extern.slf4j.Slf4j;

/**
 * @author gzw
 * @description： 观察者事件
 * @since：2023/6/16 14:51
 */
@Slf4j
public class ObserverAction {
    /** 观察者类*/
    private final Object target;
    /** 观察者方法*/
    private final Method method;

    public ObserverAction(Object target, Method method){
        this.target = target ;
        this.method = method;
        this.method.setAccessible(true);
    }

    public void execute(Object event) {
        try {
            method.invoke(target, event);
        } catch (Exception e) {
            log.error("观察者事件异常 观察者:{} 异常{}",target,e.getMessage(),e);
        }
    }


}
