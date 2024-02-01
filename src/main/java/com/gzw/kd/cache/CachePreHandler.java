package com.gzw.kd.cache;

import com.gzw.kd.common.utils.ApplicationContextUtils;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @author gzw
 * @description： init
 * @since：2023/11/8 11:23
 */
@Slf4j
@Component
public class CachePreHandler implements SmartInitializingSingleton {

    @Override
    public void afterSingletonsInstantiated() {
        ApplicationContext context = ApplicationContextUtils.getApplicationContext();
        Map<String, AbstractCache> beansOfType = context.getBeansOfType(AbstractCache.class);
        for (Map.Entry<String, AbstractCache> cacheEntry : beansOfType.entrySet()) {
            AbstractCache cache = context.getBean(cacheEntry.getValue().getClass());
            log.info("cache {} init start",cache);
            cache.init();
        }
    }
}
