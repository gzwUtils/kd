package com.gzw.kd.common.entity;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.gzw.kd.common.annotation.PrivacyEncrypt;
import com.gzw.kd.common.enums.PrivacyTypeEnum;
import com.gzw.kd.common.utils.PrivacyUtil;
import java.io.IOException;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * @author gzw
 * @description：
 * @since：2023/2/13 10:03
 */
@NoArgsConstructor
@AllArgsConstructor
public class PrivacySerializer extends JsonSerializer<String> implements ContextualSerializer {

    /**
     * 脱敏类型
     */
    private PrivacyTypeEnum privacyTypeEnum;

    /**
     * 前几位不脱敏
     */

    private Integer prefixNoMaskLen;

    /**
     * 后几位不脱敏
     */

    private Integer suffixNoMaskLen;

    /**
     * 用什么打码
     */

    private String symbol;

    /**
     * 脱敏核心 根据type 设置序列化后的值
     * @param origin 原始值
     * @param jsonGenerator json
     * @param serializerProvider 序列化
     * @throws IOException IOException
     */

    @Override
    public void serialize(final String origin, final JsonGenerator jsonGenerator, final SerializerProvider serializerProvider) throws IOException {
        if(StringUtils.isNotBlank(origin) && privacyTypeEnum!=null){
            switch (privacyTypeEnum) {
                case CUSTOMER:
                    jsonGenerator.writeString(PrivacyUtil.desValue(origin,prefixNoMaskLen,suffixNoMaskLen,symbol));
                    break;
                case NAME:
                    jsonGenerator.writeString(PrivacyUtil.hideName(origin,symbol));
                    break;
                case PHONE:
                    jsonGenerator.writeString(PrivacyUtil.hidePhone(origin));
                    break;
                case EMAIL:
                    jsonGenerator.writeString(PrivacyUtil.hideEmail(origin));
                    break;
                default:
                    throw new IllegalArgumentException("unknown privacy type enum "+privacyTypeEnum);
            }
        }
    }

    /**
     * 读取自定义注解 打造上下文环境
     * @param serializerProvider serializerProvider
     * @param beanProperty beanProperty
     * @return jsonGenerator
     * @throws JsonMappingException JsonMappingException
     */
    @Override
    public JsonSerializer<?> createContextual(final SerializerProvider serializerProvider,final BeanProperty beanProperty) throws JsonMappingException {
        if(beanProperty != null){
            if(Objects.equals(beanProperty.getType().getRawClass(),String.class)){
                PrivacyEncrypt privacyEncrypt = beanProperty.getAnnotation(PrivacyEncrypt.class);
                if(privacyEncrypt == null){
                    privacyEncrypt = beanProperty.getContextAnnotation(PrivacyEncrypt.class);
                }
                if(privacyEncrypt!=null){
                    return new PrivacySerializer(privacyEncrypt.type(), privacyEncrypt.prefixNoMaskLen(), privacyEncrypt.suffixNoMaskLen(), privacyEncrypt.symbol());
                }
            }
            return serializerProvider.findValueSerializer(beanProperty.getType(),beanProperty);
        }
        return serializerProvider.findNullValueSerializer(null);
    }
}
