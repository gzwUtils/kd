package com.gzw.kd.config;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import com.gzw.kd.common.enums.ResultCodeEnum;
import com.gzw.kd.common.exception.GlobalException;
import com.gzw.kd.common.annotation.Resubmit;
import com.gzw.kd.common.utils.AddressUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.gzw.kd.common.Constants.*;

/**
 * @author gzw
 * @version 1.0
 * @Date 2022/10/12
 * @dec
 */

@Slf4j
@Component
public class ResubmitInterceptor implements HandlerInterceptor {

    @Value("${visit-limit.enable:false}")
    private boolean defaultEnabled;



    @Resource
    private RedisTemplate<String,String> redisTemplate;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(!defaultEnabled){
            return true;
        }
        if(handler instanceof HandlerMethod){
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Resubmit resubmit = handlerMethod.getMethod().getAnnotation(Resubmit.class);
            if(ObjectUtil.isNotEmpty(resubmit) && resubmit.enable()){
                String ipAddress = AddressUtil.getIpAddress(request);
                String uri = request.getRequestURI();
                checkVisitNum(resubmit,ipAddress,uri);

            }
        }
        return true;
    }


    private void checkVisitNum(Resubmit resubmit, String ip, String uri) throws GlobalException {
        if (StringUtils.isBlank(ip) || StringUtils.isBlank(uri)) {
            return;
        }
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        String blackListStr = redisTemplate.opsForValue().get(VISIT_LIMIT_BLACKLIST_REDIS_KEY);
        if (StringUtils.isNotBlank(blackListStr)) {
            List<String> blackList = Arrays.asList(StringUtils.split(blackListStr, STRING_COMMA));
            if (blackList.contains(ip)) {
                log.info("===checkVisitNum is in blackList! ip:{} url:{}", ip, uri);
                throw new GlobalException(ResultCodeEnum.BLACK_LIST);
            }
        }
            String redisKey = VISIT_LIMIT_REDIS_KEY + ip + "." + uri;
            log.info("===checkVisitNum key:{} url:{}", redisKey, uri);
            int maxLimit = resubmit.limit();
            long seconds = resubmit.seconds();
            long blackListLimit = resubmit.blackListLimit();

            String visitNumStr = redisTemplate.opsForValue().get(redisKey);
            int visitNum;
            if (StringUtils.isBlank(visitNumStr)) {
                //第一次请求
                visitNum = 1;
                redisTemplate.opsForValue().set(redisKey, Convert.toStr(visitNum), seconds, TimeUnit.SECONDS);
            } else {
                visitNum = Integer.parseInt(visitNumStr) + 1;
                //只修改值，但不修改原过期时间
                redisTemplate.opsForValue().increment(redisKey);
            }
            //时间段内 请求达到黑名单次数后，将此ip放入黑名单
            if (visitNum >= blackListLimit) {
                blackListStr = redisTemplate.opsForValue().get(VISIT_LIMIT_BLACKLIST_REDIS_KEY);
                if (StringUtils.isBlank(blackListStr)) {
                    blackListStr = ip;
                } else {
                    blackListStr += STRING_COMMA + ip;
                }
                redisTemplate.opsForValue().set(VISIT_LIMIT_BLACKLIST_REDIS_KEY, blackListStr);
                log.info("===checkVisitNum to blacklist! ip:{} uri:{} blackListStr:{}", ip, uri, blackListStr);
                throw new GlobalException(ResultCodeEnum.BLACK_LIST);
            }

            if (visitNum >= maxLimit) {
                throw new GlobalException(ResultCodeEnum.OPERATOR_FREQUENTLY);
            }

            //兜底key过期前刚好被set的场景
            if (Objects.equals(redisTemplate.getExpire(redisKey), -1L)) {
                redisTemplate.expire(redisKey, seconds, TimeUnit.SECONDS);
            }
        }
    }
