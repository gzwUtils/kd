package com.gzw.kd.config;

import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * 通用JdbcTemplate配置，主要为了使用数据库游标
 * @author gzw
 * @since 2022-06-18
 */
@Slf4j
@Configuration
public class CommonJdbcTemplateConfig {
    @Bean
    public JdbcTemplate configJdbcTemplate(@Autowired DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
