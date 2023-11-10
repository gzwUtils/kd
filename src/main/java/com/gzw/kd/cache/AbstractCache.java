package com.gzw.kd.cache;

/**
 * @author gzw
 * @description：cache
 * @since： 2023/11/8  11:20
 */
public abstract class AbstractCache {

    /**
     * 缓存
     */
    protected abstract void init();
    /**
     * 获取缓存
     *
     * @param <T> T
     * @return T
     */
    public abstract <T> T get();
    /**
     * 清理缓存
     */
    public abstract void clear();
    /**
     * 重新加载
     */
    public void reload() {
        clear();
        init();
    }
}
