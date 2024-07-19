package com.gzw.kd.common.init;

import com.googlecode.aviator.AviatorEvaluator;
import com.gzw.kd.cache.AbstractCache;
import com.gzw.kd.common.utils.AviatorUtils;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author gzw
 * @description：
 * @since：2023/11/11 22:57
 */
@Order(1)
@Slf4j
@Component
public class AviatorFunctionInit extends AbstractCache {

    @Resource
    private AviatorUtils aviatorUtils;


    @Override
    protected void init() {
        log.info("aviatorUtils init success.......");
        AviatorEvaluator.addFunction(aviatorUtils);
    }

    @Override
    public <T> T get() {
        return null;
    }

    @Override
    public void clear() {
        AviatorEvaluator.removeFunction(aviatorUtils);
    }
}
