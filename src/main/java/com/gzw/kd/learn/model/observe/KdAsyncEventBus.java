package com.gzw.kd.learn.model.observe;

import java.util.concurrent.Executor;

/**
 * @author gzw
 * @description：
 * @since：2023/6/16 16:49
 */
public class KdAsyncEventBus extends KdEventBus {

    public KdAsyncEventBus(Executor executor) {
        super(executor);
    }
}
