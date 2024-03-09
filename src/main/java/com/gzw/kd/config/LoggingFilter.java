package com.gzw.kd.config;
import cn.hutool.core.util.RandomUtil;
import static com.gzw.kd.common.Constants.*;
import com.gzw.kd.common.utils.SnowFlakeIdUtils;
import java.io.IOException;
import javax.servlet.*;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

/**
 * @author 高志伟
 */
@Slf4j
@SuppressWarnings("all")
public class LoggingFilter implements Filter {

    private static final long serialVersionUID = 1l;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        try {
            HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
            HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
            MDC.put(TRACE_ID,TRACE_ID_FLAG+Thread.currentThread().getId() + SnowFlakeIdUtils.generatorId() + RandomUtil.randomInt(99999));
            // 把请求id写到响应头里面
            httpResponse.setHeader(REQUEST_ID_HEADER, MDC.get(TRACE_ID));

            filterChain.doFilter(servletRequest,servletResponse);
            // URI
            String requestURI = httpServletRequest.getRequestURL().toString();
            // 请求方法
            String method = httpServletRequest.getMethod();

            log.info("request : {} {}",requestURI,method);

        }finally {

            MDC.remove(TRACE_ID);
        }

    }

    @Override
    public void destroy() {
    }
}
