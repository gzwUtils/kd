package com.gzw.kd.common.utils;

import cn.hutool.core.util.ObjectUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.util.UUID;

/**
 * @author gzw
 * @version 1.0
 * @Date 2022/8/17
 * @dec
 */
@Data
@SuppressWarnings("all")
@Component
@Slf4j
public class RedisLock {

    @Resource
    private RedisTemplate<String,String> redisTemplate;

    private DefaultRedisScript<Long>  lockScript;

    private DefaultRedisScript<Object> unlockScript;

    private static final String REDIS_LOCK_PREFIX= "redis_lock_";

    private static final String  REDIS_LOCK_EXPIRE = "20" ;

    public RedisLock(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        // 加载加锁的脚本
        lockScript = new DefaultRedisScript<>();
        this.lockScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("lock.lua")));
        this.lockScript.setResultType(Long.class);
        // 加载释放锁的脚本
        unlockScript = new DefaultRedisScript<>();
        this.unlockScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("unlock.lua")));
    }


    /**
     * 加锁
     * @param key
     * @param expire
     * @return
     */
    public Boolean lock(String key, String expire) {
        String lock = REDIS_LOCK_PREFIX + key;
        String expires = StringUtils.isBlank(expire) ? REDIS_LOCK_EXPIRE : expire;
        return redisTemplate.execute((RedisCallback<Boolean>) connection -> {
            long expireAt = System.currentTimeMillis() + Long.valueOf(expires) + 1;
            Boolean acquire = connection.setNX(lock.getBytes(), String.valueOf(expireAt).getBytes());
            if (!acquire) {
                byte[] value = connection.get(lock.getBytes());

                if (ObjectUtil.isNotEmpty(value)) {

                    long expireTime = Long.parseLong(new String(value));
                    if (expireTime < System.currentTimeMillis()) {
                        byte[] oldValue = connection.getSet(lock.getBytes(), String.valueOf(System.currentTimeMillis() + Long.valueOf(expires) + 1).getBytes());
                        return Long.parseLong(new String(oldValue)) < System.currentTimeMillis();
                    }
                }

            }
            return acquire;
        });
    }

    /**
     * 获取锁
     * @param redisKey
     * @param timeoutSeconds
     * @return
     */
    public Boolean tryLock(String redisKey,long timeoutSeconds){
        log.info("RedisLock tryLOck input {} {}",redisKey,timeoutSeconds);
        String lock = REDIS_LOCK_PREFIX + redisKey;
        long timeout = timeoutSeconds * 1000;
        Boolean result = false ;
        try {
          result = (Boolean)  redisTemplate.execute((RedisCallback<Object>) connection ->{
              // 进入方法时间；用于计时器
              long start = System.currentTimeMillis();
              // 当前元素：当前元素值的第1段为信息量(即毫秒数)；第2段为识别码(即UUID)
              String signal = start + "_" + UUID.randomUUID().toString();
              // 加入队列，并返回队列长度
              long len = connection.lPush(lock.getBytes(), signal.getBytes());
              connection.expire(lock.getBytes(), timeoutSeconds);
              // 队列中只有自己，得到锁
              if (len == 1) {
                  log.info("RedisLock tryLock success {} {} {} {}", lock, timeoutSeconds, signal, "lb:len");
                  return true;
              }
              // 队首
              String first;
              do {
                  // 如果超时，获取锁失败
                  // 此处保证，一定能跳出while
                  if (System.currentTimeMillis() - start > timeout) {
                      log.info("RedisLock tryLock fail {} {} {} {}", lock, timeoutSeconds, signal, "lb:expire");
                      return false;
                  }
                  // 队首
                  first = new String(defaultEmpty(connection.lIndex(lock.getBytes(), 0)));
                  log.info("RedisLock tryLock loop {} {} {} waiting {}", lock, timeoutSeconds, signal, first);
                  try {
                      // 休眠80毫秒
                      Thread.sleep(80);
                  } catch (InterruptedException e) {
                      // 处理休眠失败的异常
                      log.error("RedisLock tryLock error {} {} {}", lock, timeoutSeconds, signal, e);
                      // 停止当前线程，返回false
                      Thread.currentThread().interrupt();
                  }
              } while (!(first.equals(signal))); // 如果队首不是自己，说明排队还没到自己；继续循环
              log.info("RedisLock tryLock success {} {} {} {}", lock, timeoutSeconds, signal, "lb:end");
              return  true;
            });
        }catch (Exception e){
            log.error("RedisLock tryLock error {} {}", lock, timeoutSeconds, e);
        }
        return result;
    }

    /**
     * 字节数组空值处理
     *
     * @param bytes 字节数组
     * @return 如果字节数组为空，则将字符串"0"转为数组返回
     */
    private byte[] defaultEmpty(byte[] bytes) {
        // 如果数组为空指针或元素数为0
        return bytes == null || bytes.length == 0
                // 字符串"0"转为数组返回
                ? "0".getBytes()
                // 否则返回原数组
                : bytes;
    }


    /**
     * 释放锁
     *
     * @param key            锁信息
     * @param timeoutSeconds 超时时间,单位 秒
     * @return 释放锁是否成功
     */
    public boolean releaseLock(String key, long timeoutSeconds) {
        log.info("RedisLock releaseLock input {} {}", key, timeoutSeconds);
        long timeout = timeoutSeconds * 1000;
        String lock = REDIS_LOCK_PREFIX + key;
        Boolean result = false;
        try {
            result = (Boolean) redisTemplate.execute((RedisCallback<Object>) connection -> {
                // 进入方法时间；用于计时器
                long start = System.currentTimeMillis();
                String first;
                long firstMs;
                do {
                    // 清理队首
                    connection.rPop(lock.getBytes());
                    first = new String(defaultEmpty(connection.lIndex(lock.getBytes(), 0)));
                    firstMs = Long.parseLong(first.split("_")[0]);
                } while (firstMs > 0 && start - firstMs > timeout);
                log.info("RedisLock releaseLock success {} {}", lock, timeoutSeconds);
                return true;
            });

        } catch (Exception e) {
            log.error("RedisLock releaseLock error{} {}", key, timeoutSeconds, e);
        }

        return result;
    }


    public void deleteKey(String key) {
        String lock = REDIS_LOCK_PREFIX + key;
        redisTemplate.delete(lock);
        log.info("redis lock key {} delete success",key);
    }
}
