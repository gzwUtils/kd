package com.gzw.kd.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author gzw
 * @version 1.0
 * @Date 2022/10/14
 * @dec
 */
@Data
@Configuration
public class AuthConfig {

    @Value("${app.auth.enable:false}")
    private boolean isEnabled;

    @Value("${app.auth.excludeUrl: }")
    private String [] excludeUrls;
}
