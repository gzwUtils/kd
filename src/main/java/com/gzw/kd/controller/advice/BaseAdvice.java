package com.gzw.kd.controller.advice;

import com.gzw.kd.controller.BaseController;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.util.HashMap;
import java.util.Map;

import static com.gzw.kd.common.Constants.TOKEN_PUSH_BASE_CLUE;

/**
 * @author 高志伟
 */

@ControllerAdvice(assignableTypes = {BaseController.class})
@SuppressWarnings("all")
public class BaseAdvice extends AbstractApiAdvice{

    /**
     * 过期时间定为10min
     */
    private static final long MAX_EXPIRED_DURATION_MILLIS = 10 * 60 * 1000L;

    private static final Map<Class<?>, String> INNER_API_TOKEN = new HashMap<Class<?>, String>(3) {{
        put(BaseController.class, TOKEN_PUSH_BASE_CLUE);
    }};

    @Override
    protected String getToken(HttpHeaders headers, MethodParameter parameter) {
        return INNER_API_TOKEN.get(parameter.getContainingClass());
    }


    @Override
    protected long getExpire() {
        return MAX_EXPIRED_DURATION_MILLIS;
    }
}
