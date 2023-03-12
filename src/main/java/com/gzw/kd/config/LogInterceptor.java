package com.gzw.kd.config;
import com.gzw.kd.common.generators.LogTraceIdGenerator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static com.gzw.kd.common.Constants.TRACE_ID;

/**
 * @author 高志伟
 */
@SuppressWarnings("all")
@Component
public class LogInterceptor implements HandlerInterceptor {


    @Resource
    private LogTraceIdGenerator  logTraceIdGenerator;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String tid = logTraceIdGenerator.generate(16);
        if(StringUtils.isNotBlank(request.getHeader(TRACE_ID))){
            tid = request.getHeader(TRACE_ID);
        }
        MDC.put(TRACE_ID,tid);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        MDC.remove(TRACE_ID);
    }
}
