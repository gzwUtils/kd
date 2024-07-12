package com.gzw.kd.controller.advice;

import static com.gzw.kd.common.Constants.TOKEN_PUSH_KD;
import com.gzw.kd.controller.BaseController;
import com.gzw.kd.controller.ChatGptController;
import com.gzw.kd.controller.EsController;
import com.gzw.kd.controller.SendController;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.ControllerAdvice;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 高志伟
 */

@ControllerAdvice(assignableTypes = {BaseController.class, EsController.class, ChatGptController.class, SendController.class})
@SuppressWarnings("all")
public class BaseAdvice extends AbstractApiAdvice{

    /**
     * 过期时间定为10min
     */
    private static final long MAX_EXPIRED_DURATION_MILLIS = 10 * 60 * 1000L;

    private static final Map<Class<?>, String> INNER_API_TOKEN = new HashMap<Class<?>, String>(3) {{
        put(BaseController.class, TOKEN_PUSH_KD);
        put(EsController.class, TOKEN_PUSH_KD);
        put(ChatGptController.class, TOKEN_PUSH_KD);
        put(SendController.class, TOKEN_PUSH_KD);
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
