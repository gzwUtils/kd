package com.gzw.kd.common.init;

import cn.hutool.core.util.ObjectUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.gzw.kd.cache.AbstractCache;
import static com.gzw.kd.common.Constants.SERVICE_COFIG;
import com.gzw.kd.common.entity.Configs;
import com.gzw.kd.common.utils.ToolUtil;
import com.gzw.kd.service.ConfigService;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author gzw
 * @description： 服务配置初始化
 * @since：2023/5/15 16:29
 */
@Order(2)
@SuppressWarnings("unchecked")
@Slf4j
@Component
public class ServiceConfigInit extends AbstractCache {

    @Resource
    private ConfigService configService;

    @Resource
    private Cache<String, Object> caffeineCache;

    @Resource
    private RedisTemplate<String,String> redisTemplate;

    @Override
    protected void init() {
        Configs configs = configService.getConfigs();
        if(ObjectUtil.isNotEmpty(configs)){
            redisTemplate.opsForValue().set(SERVICE_COFIG, ToolUtil.objectToJson(configs));
            caffeineCache.put(SERVICE_COFIG, configs);
            log.info("初始化服务配置加载成功.............................................");
        }
    }

    @Override
    public <T> T get() {
        log.warn("{} get config cache.........", this.getClass());
        if (Boolean.FALSE.equals(redisTemplate.hasKey(SERVICE_COFIG))) {
            log.warn("{} config cache reload.........", this.getClass());
            reload();
        }
        return (T) redisTemplate.opsForValue().get(SERVICE_COFIG);
    }


    @Override
    public void clear() {
        log.warn("{} clear config cache.........", this.getClass());
        redisTemplate.delete(SERVICE_COFIG);
        caffeineCache.cleanUp();
    }
}
