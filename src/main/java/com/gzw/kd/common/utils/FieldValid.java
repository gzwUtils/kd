package com.gzw.kd.common.utils;

import com.gzw.kd.common.annotation.FieldValidator;
import java.util.regex.Pattern;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * @author gzw
 * @description： 自定义校验器 实现了ConstraintValidator接口，对应传入的是注解和需要判断的类型。
 * @since：2023/4/20 20:55
 */


@Component
@Slf4j
public class FieldValid implements ConstraintValidator<FieldValidator,String> {


    /**
     *  是否强制校验
     */
    private boolean required;

    /**
     * 正则
     */
    private  Pattern pattern;

    @Override
    public void initialize(FieldValidator constraintAnnotation) {

        this.required = constraintAnnotation.required();

        this.pattern = Pattern.compile(constraintAnnotation.regex());
        //获取注解上的值 可以做一些判断
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        //禁用默认消息
        //context.disableDefaultConstraintViolation();

        if(required){
            log.info(".....................valid param ...............................");
            return !StringUtils.isBlank(value) && pattern.matcher(value).matches();
        }
        return false;
    }
}
