package com.gzw.kd.mq.eventBus.bo;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author gzw
 * @description： event
 * @since：2023/5/24 11:24
 */
@Getter
public class KdApplicationEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;


    private final SpringEventBusSource springEventBusSource;

    public KdApplicationEvent(Object source, SpringEventBusSource springEventBusSource) {
        super(source);
        this.springEventBusSource = springEventBusSource;
    }
}
