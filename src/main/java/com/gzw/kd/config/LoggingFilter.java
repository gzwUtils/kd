package com.gzw.kd.config;
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

    private static final String LOG_TEMPLATE = "request : {} {}";


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {


        HttpServletRequest  req  = (HttpServletRequest)  servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;

        String uri = req.getRequestURI();
        /* 提前放行静态资源 */
        int dot = uri.lastIndexOf('.');
        if (dot != -1 && STATIC_EXT.contains(uri.substring(dot + 1))) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        String traceId = String.valueOf(SnowFlakeIdUtils.generatorId());

        try (MDC.MDCCloseable ignored = MDC.putCloseable(TRACE_ID, traceId)) {
            resp.setHeader(REQUEST_ID_HEADER, traceId);
            filterChain.doFilter(servletRequest, servletResponse);
            log.info(LOG_TEMPLATE, req.getMethod(), req.getRequestURI());
        }

    }

    @Override
    public void destroy() {
    }
}
