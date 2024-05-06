package com.gzw.kd.learn.model.model;
import com.gzw.kd.common.entity.User;
import org.springframework.stereotype.Component;
/**
 * 项目名称：spring-demo
 * 类 名 称：ExecutorHandler
 * 类 描 述：learn
 * 创建时间：2022/3/10 8:04 下午
 * @author gzw
 */
@Component
public class ExecutorHandler extends AbstractExecutorMessage {


    @Override
    public Long uniqueId() {
        return 0L;
    }

    @Override
    public User handler(Integer id) {
        User user = new User();
        user.setAccount("gzw").setId(id);
        return user;
    }
}
