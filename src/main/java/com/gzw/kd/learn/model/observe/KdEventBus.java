package com.gzw.kd.learn.model.observe;
import com.alibaba.google.common.util.concurrent.MoreExecutors;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * @author gzw
 * @description： 同步阻塞
 * @since：2023/6/16 16:30
 */
public class KdEventBus {

    private final ObserverRegistry observerRegistry = new ObserverRegistry();

    private final Executor executor;


    public KdEventBus(){
        //单线程
        this(MoreExecutors.directExecutor());
    }

    protected KdEventBus(Executor executor){
        this.executor = executor ;
    }

    public void register(Object observer) {
        observerRegistry.register(observer);
    }

    public void post(Object event) {
        List<ObserverAction> observerActions = observerRegistry.getMatchedObserverActions(event);
        for (ObserverAction observerAction : observerActions) {
            executor.execute(()->observerAction.execute(event));
        }
    }
}
