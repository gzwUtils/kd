package com.gzw.kd.learn.model.model;

import com.gzw.kd.common.Constants;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author gzw
 * @description： demo
 * @since：2023/3/23 17:17
 */
@Component
@Slf4j
public class GzwThreadDemo extends GzwThread {

    private static final String THREAD_NAME = "gzw-thread-one";

    private static final AtomicInteger ATOMIC_INTEGER = new AtomicInteger();
    @Override
    public String getServiceName() {
        ATOMIC_INTEGER.incrementAndGet();
        return THREAD_NAME + Constants.STRING_UNDERLINE + ATOMIC_INTEGER.get();
    }

    @Override
    public void run() {
      log.info("-----------------------get thread ---------GzwThreadDemo-----------------------------------------------");
    }
}
