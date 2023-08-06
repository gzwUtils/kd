package com.gzw.kd.common.utils;

import com.gzw.kd.common.R;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author gzw
 * @description： redis limit flow
 * @since：2023/3/16 15:26
 */
@Component
@SuppressWarnings("all")
@Slf4j
public class RedisLimitFlow {


    private static RedisTemplate<String,String> redisTemplate;

    @Autowired
    public void  setRedisTemplate(RedisTemplate<String,String> redisTemplate){
        this.redisTemplate = redisTemplate;
    }



    public static R limitFlow(String key,long intervalTime,int  limitCount){
        long time = System.currentTimeMillis();
        Boolean limit = redisTemplate.hasKey(key);
        if(Boolean.TRUE.equals(limit)){
            int count = redisTemplate.opsForZSet().rangeByScore(key, time - intervalTime, time).size();
            log.info("limit count {}",count);
            if(count >= limitCount){
                int minute = (int) (intervalTime/1000/60);
                return R.error().message("请求太频繁 请稍后重试..........");
            }
        }
        redisTemplate.opsForZSet().add(key, UUID.randomUUID().toString(),time);
        return R.ok();
    }
}
