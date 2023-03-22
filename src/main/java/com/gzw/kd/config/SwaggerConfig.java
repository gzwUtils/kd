package com.gzw.kd.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author gzw
 * @version 1.0
 * @Date 2022/10/12
 * @dec
 */
@EnableSwagger2
@Configuration
@ConditionalOnProperty(prefix = "kd",value = "enable",havingValue = "true")
public class SwaggerConfig {

    @Value("${spring.application.name:}")
    private String applicationName;


    @Bean
    public Docket docket(){
        return new Docket(DocumentationType.OAS_30).groupName(applicationName).apiInfo(apiInfo())
                .select()
                //这里指定Controller扫描包路径
                .apis(RequestHandlerSelectors
                        .withClassAnnotation(RestController.class))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("API接口文档")
                .version("1.0")
                .contact(new Contact("kd", "http://localhost:8092/pc/login", "2876533492@qq.com"))
                .description("Demo Service")
                .build();
    }
}
