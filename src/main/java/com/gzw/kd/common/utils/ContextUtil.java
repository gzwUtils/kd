package com.gzw.kd.common.utils;


import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 请求上下文工具类
 * @author ligq
 * @since 2020-05-21
 */
@SuppressWarnings("all")
public class ContextUtil {

    /**
     * 根据上下文信息获取HttpRequest
     * @return HttpServletRequest
     */
    public static HttpServletRequest getHttpRequest() {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        return request;
    }
}
