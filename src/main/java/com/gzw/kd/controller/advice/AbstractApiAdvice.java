package com.gzw.kd.controller.advice;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.text.AntPathMatcher;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.gzw.kd.common.Constants;
import com.gzw.kd.common.exception.GlobalException;
import com.gzw.kd.common.utils.ContextUtil;
import com.gzw.kd.common.utils.TimeUtils;
import com.gzw.kd.config.AuthConfig;
import com.gzw.kd.vo.input.InnerApiInput;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;
import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

import static com.gzw.kd.common.enums.ResultCodeEnum.*;

/**
 * @author gzw
 * @version 1.0
 * @Date 2022/10/14
 * @dec
 */
@SuppressWarnings("all")
@Slf4j
public abstract class AbstractApiAdvice extends RequestBodyAdviceAdapter {

    @Resource
    private AuthConfig authConfig;

    protected static final String HEAD_SIGN_PARAMETER = "sign";

    protected static final String HEAD_TIMESTAMP_PARAMETER = "timestamp";
    protected static final String HEAD_APP_KEY_PARAMETER = "appKey";

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        //鉴权开关判断
        if (!authConfig.isEnabled()) {
            return false;
        }
        if (ArrayUtil.isNotEmpty(authConfig.getExcludeUrls())) {
            String accessUrl = ContextUtil.getHttpRequest().getRequestURI();
            for (String excludeUrl : authConfig.getExcludeUrls()) {
                if (pathMatcher.match(excludeUrl.trim(), accessUrl)) {
                    return false;
                }
            }
        }
        return true;
    }

    @SneakyThrows
    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {


        HttpHeaders headers = inputMessage.getHeaders();
        String sign = headers.getFirst(HEAD_SIGN_PARAMETER);
        final String timestamp = headers.getFirst(HEAD_TIMESTAMP_PARAMETER);
        if(StringUtils.isBlank(sign)){
            throw new GlobalException(HTTP_SIGN_NULL);
        }
        if(StringUtils.isBlank(timestamp)){
            throw new GlobalException(HTTP_TIMESTAMP_NULL);
        }
        validTime(timestamp);

        final InnerApiInput innerApiInput= new InnerApiInput().setBody(IoUtil.read(inputMessage.getBody(), CharsetUtil.UTF_8)).setSign(sign)
                .setToken(getToken(headers,parameter)).setTimestamp(timestamp);

        signCheckout(innerApiInput);

        return new HttpInputMessage() {
            @Override
            public InputStream getBody() throws IOException {
                return new ByteArrayInputStream(innerApiInput.getBody().getBytes(StandardCharsets.UTF_8));
            }

            @Override
            public HttpHeaders getHeaders() {
                return preHandleHttpHeader(headers);
            }
        };

    }


    protected abstract String getToken(HttpHeaders headers,MethodParameter parameter);

    protected abstract long getExpire();

    protected HttpHeaders preHandleHttpHeader(HttpHeaders originHttpHeaders) {
        return originHttpHeaders;
    }


    private void validTime(String timestamp) throws GlobalException{
       final Long timestampMaillis = Convert.toLong(timestamp);
       final long currentTimeMillis = TimeUtils.currentTimeMillis();
       if(Math.abs(currentTimeMillis-timestampMaillis) > getExpire()){
           throw new GlobalException(HTTP_TIMESTAMP_EXPIRE);
       }
    }

    /**
     * 参数的签名校验
     * @param innerApiInput
     */

    private void signCheckout(InnerApiInput innerApiInput) throws GlobalException{

        //签名校验
        String formatStr = String.format(Constants.INNER_MD5_ENC_STR, innerApiInput.getTimestamp(),
                innerApiInput.getToken(), innerApiInput.getBody());
        String md5 = DigestUtil.md5Hex(formatStr);
        if (!StringUtils.equals(innerApiInput.getSign(), md5)) {
            log.info("Api sign error!! input:{} server:{}", innerApiInput.getSign(), md5);
            throw new GlobalException(HTTP_SIGN_ERROR);
        }
    }

}
