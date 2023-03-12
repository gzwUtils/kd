package com.gzw.kd.learn.model.model;

import java.util.concurrent.Callable;

/**
 * 项目名称：spring-demo
 * 类 名 称：Learn
 * 类 描 述：设计模式-模版模式
 * 创建时间：2021/10/18 7:03 下午
 *
 * @author gzw
 * 抽象类中不一定都是抽象方法
 */
public abstract class AbstractLearn implements Callable<Object> {



    /**
     *唯一ID
     * @return
     */

    public abstract Long flagId();

    /**
     * 处理逻辑
     * @param lx
     * @return
     * @throws Exception
     */
    public abstract Object handler(Object lx) throws Exception;


    private Object lx;


    private AbstractLearn set(Object lx){
        this.lx=lx;
        return this;
    }


    @Override
    public Object call() throws Exception {
        return handler(lx);
    }
}
