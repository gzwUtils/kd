package com.gzw.kd.cache;

import com.gzw.kd.common.utils.ApplicationContextUtils;
import java.util.Map;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @author gzw
 * @description：
 * @since：2023/11/8 11:23
 */

@Component
@ConditionalOnProperty(prefix ="cache.init",value = "enable",havingValue ="on")
public class CachePreHandler implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        ApplicationContext context = ApplicationContextUtils.getApplicationContext();
        Map<String, AbstractCache> beansOfType = context.getBeansOfType(AbstractCache.class);
        for (Map.Entry<String, AbstractCache> cacheEntry : beansOfType.entrySet()) {
            AbstractCache cache = context.getBean(cacheEntry.getValue().getClass());
            cache.init();
        }
    }
}
