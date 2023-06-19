package com.gzw.kd.common.utils;
import com.gzw.kd.learn.model.observe.KdAsyncEventBus;
import com.gzw.kd.learn.model.observe.KdEventBus;
import com.gzw.kd.service.ObserverListener;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author gzw
 * @description：
 * @since：2023/6/16 23:43
 */
@SuppressWarnings("unused")
@Slf4j
@Component
public class EventBusUtils {

    @Resource
    private ObserverListener observerListener;
    @Resource
    private KdEventBus eventBus;
    @Resource
    private KdAsyncEventBus asyncEventBus;


    /**
     * 注册总线
     */
    @PostConstruct
    public void init() {
        eventBus.register(observerListener);
        asyncEventBus.register(observerListener);
    }

    /**
     * 发布活动 同步阻塞
     * @param obj 消息
     */
    public void eventPost(Object obj){
        eventBus.post(obj);
        log.info("同步阻塞....................分发消息完成");
    }


    /**
     *  异步 非阻塞
     * @param obj 消息
     */
    public void asyncEventPost(Object obj){
        asyncEventBus.post(obj);
        log.info("异步非阻塞....................分发消息完成");
    }
}
