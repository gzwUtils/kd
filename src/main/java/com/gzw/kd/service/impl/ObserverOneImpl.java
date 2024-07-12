package com.gzw.kd.service.impl;

import com.gzw.kd.common.annotation.Subscribe;
import com.gzw.kd.service.ObserverListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author gzw
 * @description： 观察者 1
 * @since：2023/6/16 18:08
 */
@Service
@Slf4j
public class ObserverOneImpl implements ObserverListener {


    @Override
    @Subscribe
    public void push(String message) {
        log.info("接受到消息...............................{}",message);
    }
}
