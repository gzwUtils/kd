package com.gzw.kd.config;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.pool.DruidDataSource;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.quartz.SchedulerException;
import org.quartz.utils.ConnectionProvider;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author 高志伟
 */
@SuppressWarnings("all")
@Data
public class DruidPoolingConnectionProvider implements ConnectionProvider {


    public String driver;


    public String URL;


    public String user;


    public String password;

    public int maxConnections;

    public int minIdle;

    public long maxWait ;

    public String validationQuery;

    private boolean validateOnCheckout;

    private int idleConnectionValidationSeconds;

    public String maxCachedStatementsPerConnection;

    private String discardIdleConnectionsSeconds;

    public static final int DEFAULT_DB_MAX_CONNECTIONS = 10;

    public static final int DEFAULT_DB_MAX_CACHED_STATEMENTS_PER_CONNECTION = 120;


    public List<Filter> filters;


    private DruidDataSource datasource;

    public DruidPoolingConnectionProvider() {
        this.filters = new ArrayList<>();
        StatFilter filter = new StatFilter();
        filter.setLogSlowSql(true);
        filter.setSlowSqlMillis(2000);
        filters.add(filter);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return datasource.getConnection();
    }

    @Override
    public void shutdown() {
        datasource.close();
    }

    @Override
    public void initialize() throws SQLException {
        if (this.URL == null) {
            throw new SQLException("DBPool could not be created: DB URL cannot be null");
        }
        if (this.driver == null) {
            throw new SQLException("DBPool driver could not be created: DB driver class name cannot be null!");
        }
        if (this.maxConnections < 0) {
            throw new SQLException("DBPool maxConnections could not be created: Max connections must be greater than zero!");
        }
        datasource = new DruidDataSource();
        try {
            datasource.setDriverClassName(this.driver);
        } catch (Exception e) {
            try {
                throw new SchedulerException("Problem setting driver class name on datasource: " + e.getMessage(), e);
            } catch (SchedulerException ignored) {
            }
        }
        datasource.setUrl(this.URL);
        datasource.setUsername(this.user);
        datasource.setPassword(this.password);
        datasource.setMaxActive(this.maxConnections);
        datasource.setMinIdle(this.minIdle);
        datasource.setMaxWait(this.maxWait);
        datasource.setProxyFilters(filters);
        datasource.setMaxPoolPreparedStatementPerConnectionSize(DEFAULT_DB_MAX_CONNECTIONS);
        if (this.validationQuery != null) {
            datasource.setValidationQuery(this.validationQuery);
            if (!this.validateOnCheckout) {
                datasource.setTestOnReturn(true);
            } else {
                datasource.setTestOnBorrow(true);
                datasource.setValidationQueryTimeout(this.idleConnectionValidationSeconds);
            }
        }
    }
}
