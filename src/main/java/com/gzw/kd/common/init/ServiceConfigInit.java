package com.gzw.kd.common.init;

import cn.hutool.core.util.ObjectUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.gzw.kd.cache.AbstractCache;
import static com.gzw.kd.common.Constants.SERVICE_COFIG;
import com.gzw.kd.common.entity.Configs;
import com.gzw.kd.common.exception.Asserts;
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
        } else {
            Asserts.fail("初始化服务配置失败........................................");
        }
    }

    @Override
    public <T> T get() {
        log.warn(this.getClass()+"get config cache.........");
        if (Boolean.FALSE.equals(redisTemplate.hasKey(SERVICE_COFIG))) {
            log.warn(this.getClass()+"config cache reload.........");
            reload();
        }
        return (T) redisTemplate.opsForValue().get(SERVICE_COFIG);
    }


    @Override
    public void clear() {
        log.warn(this.getClass()+"clear config cache.........");
        redisTemplate.delete(SERVICE_COFIG);
        caffeineCache.cleanUp();
    }
}
