package com.gzw.kd.common.utils;
import com.gzw.kd.config.ThreadPoolExecutorShutdownDefinition;
import java.util.concurrent.ThreadPoolExecutor;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author gzw
 * @description： 线程池 工具类
 * @since：2023/5/19 16:03
 */
@Slf4j
@Component
public class ThreadUtils {

    @Resource
    private ThreadPoolExecutorShutdownDefinition threadPoolExecutorShutdownDefinition;

    /**
     * 注册 线程池 被Spring管理，优雅关闭
     * @param executor ex
     */
    public  void register(ThreadPoolExecutor executor){
        threadPoolExecutorShutdownDefinition.registryExecutor(executor);
    }


}
