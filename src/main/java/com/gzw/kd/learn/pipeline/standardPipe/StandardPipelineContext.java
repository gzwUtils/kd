package com.gzw.kd.learn.pipeline.standardPipe;

import com.google.common.collect.Maps;
import com.gzw.kd.learn.pipeline.pipe.PipelineContext;
import java.util.Map;

/**
 * @author gzw
 * @description：
 * @since：2024/1/17 22:49
 */
public class StandardPipelineContext implements PipelineContext {

    private Map<String, Object> contentMap = Maps.newConcurrentMap();

    @Override
    public void set(String contextKey, Object contextValue) {
        contentMap.put(contextKey, contextValue);
    }

    @Override
    public Object get(String contextKey) {
        return contentMap.get(contextKey);
    }
}
