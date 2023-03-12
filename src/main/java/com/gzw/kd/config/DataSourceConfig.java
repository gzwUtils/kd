package com.gzw.kd.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.google.common.collect.Maps;
import java.util.Map;
import javax.sql.DataSource;

import com.gzw.kd.config.datasource.DynamicDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import static com.gzw.kd.common.Constants.INT_THREE;

/**
 * 项目名称：spring-demo
 * 类 名 称：DataSourceConfig
 * 类 描 述：多数据源配置
 * 创建时间：2022/4/7 9:49 下午
 *
 * @author gzw
 */
@Configuration
@Slf4j
public class DataSourceConfig {

    @ConfigurationProperties("spring.datasource.master")
    @Bean(initMethod = "init",destroyMethod = "close")
    public DruidDataSource oneDatasource(){
        log.debug("mysql master  druid data-source init...");
        return DruidDataSourceBuilder.create().build();
    }

    @ConfigurationProperties("spring.datasource.two")
    @Bean(initMethod = "init",destroyMethod = "close")
    public DruidDataSource twoDatasource(){
        log.debug("mysql two  druid data-source init...");
        return DruidDataSourceBuilder.create().build();
    }


    @ConfigurationProperties("spring.datasource.three")
    @Bean(initMethod = "init",destroyMethod = "close")
    public DruidDataSource threeDatasource(){
        log.debug("mysql three  druid data-source init...");
        return DruidDataSourceBuilder.create().build();
    }


    @Bean
    @Primary
    public DynamicDataSource dataSource(@Qualifier("oneDatasource") DataSource one,
                                        @Qualifier("twoDatasource") DataSource two,
                                        @Qualifier("threeDatasource") DataSource three) {
        Map<Object, Object> dsMap = Maps.newHashMapWithExpectedSize(INT_THREE);
        dsMap.put("MASTER", one);
        dsMap.put("SLAVE_2", two);
        dsMap.put("SLAVE_3", three);
        return new DynamicDataSource(dsMap, one);
    }
}
