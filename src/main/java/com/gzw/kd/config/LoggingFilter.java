package com.gzw.kd.config;
import static com.gzw.kd.common.Constants.*;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import javax.servlet.*;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.MDC;

/**
 * @author 高志伟
 */
@SuppressWarnings("all")
public class LoggingFilter implements Filter {

    private AtomicInteger count = new AtomicInteger(0);
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        try {
            count.incrementAndGet();
            MDC.put(TRACE_ID,TRACE_ID_FLAG+Thread.currentThread().getId()+STRING_UNDERLINE+count.get());
            HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
            httpResponse.setHeader(REQUEST_ID_HEADER, MDC.get(TRACE_ID));  // 把请求id写到响应头里面
            filterChain.doFilter(servletRequest,servletResponse);
        }finally {
            MDC.remove(TRACE_ID);
        }

    }

    @Override
    public void destroy() {
    }
}
