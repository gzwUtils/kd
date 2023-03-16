package com.gzw.kd.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
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

    @Resource
    private LogInterceptor logInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(logInterceptor).addPathPatterns("/**");

        registry.addInterceptor(loginInterceptor).addPathPatterns("/**").excludePathPatterns(
                "/pc/register",
                "/pc/registerAction",
                "/pc/login",
                "/pc/phoneCheck",
                "/pc/phoneLogin",
                "/pc/phoneCheckLogin",
                "/pc/getCode",
                "/wx/**",
                "/es/**",
                "/pc/unknown",
                "/pc/error",
                "/pc/loginAction",
                "/quartz/**",
                "/zfb/**",
                "/base/**",
                "/qrt/dyQrCodeWriter",
                "/swagger-resources/**",
                "/webjars/**",
                "/v2/**",
                "/swagger-ui.html/**",
                "/assets/**");
        registry.addInterceptor(resubmitInterceptor).addPathPatterns("/**");



    }


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //swagger增加url映射
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}
