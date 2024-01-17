package com.gzw.kd.learn.spi.load;

import com.google.common.collect.Maps;
import java.util.Map;

/**
 * @author gzw
 * @description：
 * @since：2024/1/17 23:47
 */
@SuppressWarnings({"unused","rawtypes"})
public class DependServiceRegistryHelper {

    /**
     * 存储策略依赖的服务,统一管理
     */
    private static final Map<String, Object> DEPEND_MANAGER_MAP = Maps.newHashMap();


    public static boolean registryMap(Map<Class, Object> dependManagerMap) {
        for (Map.Entry<Class, Object> dependEntry :
                dependManagerMap.entrySet()) {
            registry(dependEntry.getKey(), dependEntry.getValue());
        }
        return true;
    }
    public static void registry(Class cls, Object dependObject) {
        DEPEND_MANAGER_MAP.put(cls.getCanonicalName(), dependObject);
    }
    public static Object getDependObject(Class cls) {
        return DEPEND_MANAGER_MAP.get(cls.getCanonicalName());
    }
}
