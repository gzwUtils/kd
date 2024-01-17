package com.gzw.kd.learn.spi.impl;

import com.gzw.kd.learn.spi.StandardRemoteServiceInterface;
import lombok.extern.slf4j.Slf4j;

/**
 * @author gzw
 * @description：
 * @since：2024/1/18 00:12
 */
@Slf4j
public class TestImpl implements StandardRemoteServiceInterface {
    @Override
    public void exec() {
        log.info("{} start load ",this.getClass().getCanonicalName());
    }
}
