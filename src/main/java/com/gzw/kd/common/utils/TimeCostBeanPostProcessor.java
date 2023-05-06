package com.gzw.kd.common.utils;

import com.alibaba.google.common.collect.Maps;
import static com.gzw.kd.common.Constants.INT_100;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * @author gzw
 * @description： bean初始化耗时
 * @since：2023/5/5 18:17
 */
@Slf4j
@Component
public class TimeCostBeanPostProcessor implements BeanPostProcessor {


    private final  Map<String, Long> costMap = Maps.newConcurrentMap();


    @Override
    public Object postProcessBeforeInitialization(@NotNull Object bean, @NotNull String beanName) throws BeansException {
        costMap.put(beanName,System.currentTimeMillis());
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(@NotNull Object bean, @NotNull String beanName) throws BeansException {
        if (costMap.containsKey(beanName)) {
            Long start = costMap.get(beanName);
            long cost  = System.currentTimeMillis() - start;
            if (cost > INT_100) {
                costMap.put(beanName, cost);
                log.warn("bean....:{} time...{} :",beanName,cost);
            }
        }
        return bean;
    }
}
