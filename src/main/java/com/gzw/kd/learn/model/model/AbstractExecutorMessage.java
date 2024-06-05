package com.gzw.kd.learn.model.model;

import com.gzw.kd.common.entity.User;
import java.util.concurrent.Callable;

/**
 * 项目名称：spring-demo
 * 类 名 称：ExecutorMessage
 * 类 描 述：抽象类定义的是一种从属关系
 * 创建时间：2022/3/10 7:54 下午
 *
 * @author gzw
 */
public abstract class AbstractExecutorMessage implements Callable<User> {

    /**
     * 唯一标识
     * @return id
     */
    public abstract Long uniqueId();


    /**
     * 真正执行的逻辑
     * @param id flag
     * @return 用户信息
     */

    public abstract User handler(Integer id);


    private Integer id;


    public AbstractExecutorMessage setUser(Integer id){
        this.id=id;
        return this;
    }


    @Override
    public User call() {
        return handler(id);
    }
}
