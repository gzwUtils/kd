package com.gzw.kd.config;

import cn.hutool.core.util.CharsetUtil;
import com.alibaba.fastjson2.JSONObject;
import com.gzw.kd.common.Constants;
import static com.gzw.kd.common.ErrorCode.ERR_NO_ACCESS_PRIVILEGE;
import com.gzw.kd.common.R;
import com.gzw.kd.common.utils.MessageUtils;
import com.gzw.kd.common.xss.XssFilter;
import java.io.IOException;
import java.util.Objects;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.util.StreamUtils;

/**
 * Filter配置
 *
 * @author gaozhiwei
 */
@SuppressWarnings("all")
@ConditionalOnProperty(prefix = "app.auth",value = "enable",havingValue ="true" )
@Configuration
public class FilterConfig {
    @Bean
    public FilterRegistrationBean<XssFilter> xssFilterRegistration() {
        FilterRegistrationBean<XssFilter> registration = new FilterRegistrationBean<>();
        registration.setDispatcherTypes(DispatcherType.REQUEST);
        registration.setFilter(new XssFilter());
        registration.addUrlPatterns("/*");
        registration.setName("xssFilter");
        registration.setOrder(Integer.MAX_VALUE);
        return registration;
    }


    @Bean
    public FilterRegistrationBean<LoggingFilter> loggingFilterRegistration() {
        FilterRegistrationBean<LoggingFilter> registration = new FilterRegistrationBean<>();
        registration.setDispatcherTypes(DispatcherType.REQUEST);
        registration.setFilter(new LoggingFilter());
        registration.addUrlPatterns("/*");
        registration.setName("logFilter");
        registration.setOrder(Constants.INT_ONE);
        return registration;
    }

    @Bean
    public FilterRegistrationBean<UnifiedTokenFilter> tokenFilterNewRegister() {
        FilterRegistrationBean<UnifiedTokenFilter> registration = new FilterRegistrationBean<>();
        registration.setDispatcherTypes(DispatcherType.REQUEST);
        registration.setFilter(new UnifiedTokenFilter());
        registration.addUrlPatterns(Constants.URL_PATTERN_ALL);
        registration.setName(Constants.KEY_TOKEN_FILTER);
        registration.setOrder(Constants.INT_TWO);
        return registration;
    }

    @Slf4j
    public static class UnifiedTokenFilter implements Filter {

        @Override
        public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
            HttpServletRequest req = (HttpServletRequest) servletRequest;
            HttpServletResponse resp = (HttpServletResponse) servletResponse;

            final String token = req.getHeader(Constants.HTTP_USER_TOKEN);

            try {
                Objects.requireNonNull(token);
                //校验token
                filterChain.doFilter(req, resp);
            } catch (Exception ex) {
                log.warn("kd tokenFiler validate token: [{}] failed ", token);
                this.writeFailureRestResponse(resp);
            }
        }

        private void writeFailureRestResponse(HttpServletResponse response) throws IOException {
            R res = R.error().code(ERR_NO_ACCESS_PRIVILEGE).message(MessageUtils.getMessage(ERR_NO_ACCESS_PRIVILEGE));
            response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
            StreamUtils.copy(JSONObject.toJSONString(res), CharsetUtil.CHARSET_UTF_8, response.getOutputStream());
        }
    }
}
