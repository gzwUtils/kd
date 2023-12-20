package com.gzw.kd.config;

import com.gzw.kd.common.enums.DataSourceEnum;
import com.gzw.kd.config.datasource.DynamicDataSource;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.impl.AbstractEngineConfiguration;
import org.flowable.common.engine.impl.EngineConfigurator;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

/**
 * @author gzw
 * @description： flowable
 * @since：2023/12/18 22:12
 */
@SuppressWarnings("unused")
@Slf4j
public class FlowableEngineConfigurator implements EngineConfigurator {

    private static final AtomicBoolean INIT = new AtomicBoolean();




    @Override
    public void beforeInit(AbstractEngineConfiguration abstractEngineConfiguration) {
        if(INIT.compareAndSet(false,true)){
            DataSource source = abstractEngineConfiguration.getDataSource();
            if(source instanceof TransactionAwareDataSourceProxy){
               source =  ((TransactionAwareDataSourceProxy)source).getTargetDataSource();
            }
            if(source instanceof DynamicDataSource){
                Map<Object, DataSource> sources = ((DynamicDataSource) source).getResolvedDataSources();
                abstractEngineConfiguration.setDataSource(sources.get(DataSourceEnum.FLOWABLE));
                log.info("切换数据源 datasource .................");
            }

        }
    }



    @Override
    public void configure(AbstractEngineConfiguration abstractEngineConfiguration) {

    }

    @Override
    public int getPriority() {
        return 0;
    }
}
