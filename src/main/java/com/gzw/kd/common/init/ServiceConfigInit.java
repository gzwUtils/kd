package com.gzw.kd.common.init;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONObject;
import com.github.benmanes.caffeine.cache.Cache;
import static com.gzw.kd.common.Constants.SERVICE_COFIG;
import com.gzw.kd.common.entity.Configs;
import com.gzw.kd.common.exception.Asserts;
import com.gzw.kd.service.ConfigService;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author gzw
 * @description： 服务配置初始化
 * @since：2023/5/15 16:29
 */
@Slf4j
@Component
public class ServiceConfigInit implements CommandLineRunner {

    @Resource
    private ConfigService configService;

    @Resource
    private Cache<String, Object> caffeineCache;

    @Resource
    private RedisTemplate<String,String> redisTemplate;


    @Override
    public void run(String... args) throws Exception {
        Configs configs = configService.getConfigs();
        if(ObjectUtil.isNotEmpty(configs)){
            redisTemplate.opsForValue().set(SERVICE_COFIG, JSONObject.toJSONString(configs));
            caffeineCache.put(SERVICE_COFIG, configs);
            log.info("初始化服务配置加载成功.............................................");
        } else {
            Asserts.fail("初始化服务配置失败........................................");
        }
    }
}
