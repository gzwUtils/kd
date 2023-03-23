package com.gzw.kd.learn.model.model;

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
    @Override
    public String getServiceName() {
        return "gzw-thread-one";
    }

    @Override
    public void run() {
      log.info("--------------------------------GzwThreadDemo-----------------------------------------------");
    }
}
