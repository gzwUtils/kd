package com.gzw.kd.config;
import com.gzw.kd.common.exception.GlobalException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import static com.gzw.kd.common.Constants.*;
import static com.gzw.kd.common.enums.ResultCodeEnum.END_POINT_ABSENT;

/**
 * es config
 *
 * @author 高志伟
 */
@SuppressWarnings("all")
@Data
@Slf4j
@Configuration
public class ElasticsearchConfiguration {


    @Value("${spring.data.elasticsearch.endpoints}")
    private String endpoints;

    /**
     * 连接超时时间
     */

    @Value("${spring.data.elasticsearch.connection-timeout}")
    private Integer connectTimeout;

    /**
     * 连接超时时间
     */

    @Value("${spring.data.elasticsearch.socket-timeout}")
    private Integer socketTimeout;

    /**
     * 获取连接的超时时间
     */
    @Value("${spring.data.elasticsearch.connection-request-timeout}")
    private Integer connReqTimeout;

    /**
     * 最大连接数
     */
    @Value("${spring.data.elasticsearch.max-conn-total}")
    private Integer maxConnTotal;

    /**
     * 最大路由连接数
     */

    @Value("${spring.data.elasticsearch.max-conn-per-route}")
    private Integer maxConnPerRoute;


    @Value("${spring.data.elasticsearch.username}")
    private String username;

    @Value("${spring.data.elasticsearch.password}")
    private String password;




    @Bean(name = "restHighLevelClient", destroyMethod = "close")
    public RestHighLevelClient restHighLevelClient() throws GlobalException {
        log.info("restHighLevelClient build 正在创建 ------------------------------------------------------");
        try {
            if (StringUtils.isBlank(endpoints) || StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
                throw new GlobalException(END_POINT_ABSENT);
            }
            String[] split = endpoints.split(STRING_COMMA);
            HttpHost[] httpHosts = new HttpHost[split.length];
            for (int i = 0; i < split.length; i++) {
                String[] split1 = split[i].split(STRING_COLON);
                httpHosts[i] = new HttpHost(split1[0], Integer.parseInt(split1[1]), SCHEME);
            }
            RestClientBuilder builder = RestClient.builder(httpHosts);
            final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY,
                    new UsernamePasswordCredentials(username, password));
            builder.setRequestConfigCallback(requestConfigBuilder -> {
                requestConfigBuilder.setConnectTimeout(connectTimeout);
                requestConfigBuilder.setSocketTimeout(socketTimeout);
                requestConfigBuilder.setConnectionRequestTimeout(connReqTimeout);
                return requestConfigBuilder;
            });
            builder.setHttpClientConfigCallback(httpClientBuilder -> {
                httpClientBuilder.disableAuthCaching();
                httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                httpClientBuilder.setMaxConnTotal(maxConnTotal);
                httpClientBuilder.setMaxConnPerRoute(maxConnPerRoute);
                return httpClientBuilder;
            });
            log.info("restHighLevelClient build 创建成功 ------------------------------------------------------");
            return new RestHighLevelClient(builder);
        } catch (GlobalException e) {
            log.error("restHighLevelClient build 创建失败 {}", e.getMessage(), e);
        }
        return null;
    }
}
