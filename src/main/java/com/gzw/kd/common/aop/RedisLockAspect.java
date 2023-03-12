package com.gzw.kd.common.aop;

import com.gzw.kd.common.annotation.RedisLockAnnotation;
import com.gzw.kd.common.entity.RedisLockDefinitionHolder;
import com.gzw.kd.common.enums.RedisLockTypeEnum;
import com.gzw.kd.common.enums.ResultCodeEnum;
import com.gzw.kd.common.exception.GlobalException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author gzw
 * @description： RedisLockAspect
 * @since：2023/2/15 18:39
 */
@SuppressWarnings("all")
@Slf4j
@Aspect
@Component
public class RedisLockAspect {

    @Resource
    private RedisTemplate<String,String>  redisTemplate;


    private static final ConcurrentLinkedQueue<RedisLockDefinitionHolder> holderList = new ConcurrentLinkedQueue();

    @Pointcut("@annotation(com.gzw.kd.common.annotation.RedisLockAnnotation)")
    public void redisLockPC(){

    }

    @Around(value = "redisLockPC()")
    public Object around(ProceedingJoinPoint point) throws Throwable{
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        RedisLockAnnotation annotation = method.getAnnotation(RedisLockAnnotation.class);
        RedisLockTypeEnum typeEnum = annotation.typeEnum();
        Object[] params = point.getArgs();
        String ukString = params[annotation.lockField()].toString();

        String businessKey = typeEnum.getUniqueKey(ukString);
        String uniqueValue = UUID.randomUUID().toString();
        Object result = null;

        try {
            boolean isSuccess = redisTemplate.opsForValue().setIfAbsent(businessKey, uniqueValue);
            if(!isSuccess){
                throw new GlobalException(ResultCodeEnum.REDIS_LOCK_EXIST);
            }
            redisTemplate.expire(businessKey,annotation.lockTime(), TimeUnit.SECONDS);
            Thread currentThread = Thread.currentThread();
            //将本次任务添加到延迟队列中
            holderList.add(new RedisLockDefinitionHolder(businessKey, annotation.lockTime(), System.currentTimeMillis(),currentThread,annotation.tryCount()));
            //执行业务操作
            result = point.proceed();
            //线程被中断 抛出异常 中断此次请求
            if(currentThread.isInterrupted()){
                throw new InterruptedException("you has been interrupted ===");
            }

        } catch (InterruptedException e) {
            log.error("interrupt exception rollback transaction",e);
            throw new GlobalException("interrupt exception please send request again",ResultCodeEnum.Failed.getCode());
        }catch (GlobalException e){
            log.error("error please check ",e);
            throw new GlobalException(e.getMessage(),ResultCodeEnum.UNKNOWN_ERROR.getCode());
        }finally {
            //删除 key 释放锁
            redisTemplate.delete(businessKey);
            log.info("release the lock , businessKey is {}",businessKey);
        }
        return result;
    }


    private static final ScheduledExecutorService SCHEDULER = new ScheduledThreadPoolExecutor(1,new BasicThreadFactory.Builder().namingPattern(" redis-schedule-pool ").daemon(true).build());

    {
        SCHEDULER.scheduleAtFixedRate(()->{
            Iterator<RedisLockDefinitionHolder> iterator = holderList.iterator();
            while (iterator.hasNext()){
                RedisLockDefinitionHolder holder = iterator.next();
                //判空
                if(holder == null ){
                    iterator.remove();
                    continue;
                }
                //判断 key 是否还有效 无效的话移除
                if(redisTemplate.opsForValue().get(holder.getBusinessKey()) == null){
                    iterator.remove();
                    continue;
                }
                //超时重试次数 超过时 线程中断
                if(holder.getCurrentCount() >= holder.getTryCount()){
                    holder.getCurrentThread().interrupt();
                    iterator.remove();
                    continue;
                }
                //判断是否进入最后三分之一时间
                long curTime = System.currentTimeMillis();
                boolean shouldExtend = (holder.getLastModifyTime()+holder.getModifyPeriod()) <= curTime;
                if(shouldExtend){
                    holder.setLastModifyTime(curTime);
                    redisTemplate.expire(holder.getBusinessKey(),holder.getLockTime(),TimeUnit.SECONDS);
                    log.info("businessKey : {}, try count : {}",holder.getBusinessKey(),holder.getCurrentCount()+1);
                    holder.setCurrentCount(holder.getCurrentCount()+ 1);
                }
            }
        },0,2,TimeUnit.SECONDS);
    }
}
