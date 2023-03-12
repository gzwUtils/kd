package com.gzw.kd.config.datasource;

import java.util.Map;
import java.util.Stack;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 项目名称：spring-demo
 * 类 名 称：DynamicDataSource
 * 类 描 述：动态数据源配置
 * 创建时间：2022/4/7 10:33 下午
 *
 * @author gzw
 */
@Slf4j
public class DynamicDataSource extends AbstractRoutingDataSource {


    private static final ThreadLocal<Stack<String>> DATA_SOURCE_KEY=new InheritableThreadLocal<>();


    public static void setDataSourceKey(String dataSourceKey){
        log.info("setDataSourceKey datasource{} ",dataSourceKey);
        Stack<String> stack = DATA_SOURCE_KEY.get();
        if(stack==null){
            stack= new Stack<>();
            DATA_SOURCE_KEY.set(stack);
        }
        stack.push(dataSourceKey);
    }

    public static void cleanDataSourceKey(){
        Stack<String> stack = DATA_SOURCE_KEY.get();
        if(stack!=null){
            stack.pop();
            if(stack.isEmpty()){
                DATA_SOURCE_KEY.remove();
            }
        }
    }

    @Override
    protected Object determineCurrentLookupKey() {
        Stack<String> stack = DATA_SOURCE_KEY.get();
        if(stack!=null){
            return stack.peek();
        }
        return null;
    }

    /**
     * 构造
     * @param dataSourceMap 数据源map
     * @param concurrentDatasource 默认数据源
     */
    public DynamicDataSource(Map<Object, Object> dataSourceMap,DataSource concurrentDatasource) {
        super.setDefaultTargetDataSource(concurrentDatasource);
        super.setTargetDataSources(dataSourceMap);
    }
}
