package com.gzw.kd.config;

import cn.hutool.core.util.StrUtil;
import java.util.List;
import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author gzw
 */
@Configuration
@Data
@ConfigurationProperties(prefix = "canal")
public class CanalConfig {

    private String serverHost;

    private String zookeeperHosts;

    private String instance;

    private String username = StrUtil.EMPTY;

    private String password = StrUtil.EMPTY;

    private Integer batchSize;

    private Integer retries;

    private Integer timeout;

    private Long interval;

    private Map<String, List<String>> destinations;
}
