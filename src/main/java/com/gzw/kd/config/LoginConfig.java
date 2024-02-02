package com.gzw.kd.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import javax.annotation.Resource;


/**
 * @author 高志伟
 */
@SuppressWarnings("all")
@Configuration
public class LoginConfig implements WebMvcConfigurer {

    @Resource
    private ResubmitInterceptor resubmitInterceptor;
    @Resource
    private LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {


        registry.addInterceptor(loginInterceptor).addPathPatterns("/**").excludePathPatterns(
                "/pc/register",
                "/pc/registerAction",
                "/pc/login",
                "/pc/phoneCheck",
                "/pc/phoneLogin",
                "/pc/phoneCheckLogin",
                "/pc/getCode",
                "/pc/dc",
                "/base/**",
                "/wx/**",
                "/pc/unknown",
                "/pc/error",
                "/pc/loginAction",
                "/ali/**",
                "/assets/**");
        registry.addInterceptor(resubmitInterceptor).addPathPatterns("/**");


    }
}
