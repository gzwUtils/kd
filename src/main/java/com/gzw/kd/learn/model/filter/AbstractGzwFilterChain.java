package com.gzw.kd.learn.model.filter;
import java.util.List;

/**
 * 项目名称：spring-demo
 * 类 名 称：GzwFilterChain
 * 类 描 述：责任链模式-抽象处理者
 * 创建时间：2022/3/28 10:27 下午
 *
 * @author gzw
 */

public  abstract  class AbstractGzwFilterChain<U>  {


    private List<GzwFilter<U>> gzwFilterList;


    protected AbstractGzwFilterChain(List<GzwFilter<U>> gzwFilters) {
        if (null != gzwFilters && !gzwFilters.isEmpty()) {
            this.gzwFilterList = gzwFilters;
        }
    }
    /**
     * 处理方法
     */
    public U doFilter(U initStage) {
        if (null == gzwFilterList) {
            return null;
        }

        U stage = initStage;
        for (GzwFilter<U> filter : gzwFilterList) {
            stage = filter.doFilter(stage);
            // filter返回null, 则中断职责链的执行
            if (null == stage) {
                return null;
            }
        }
        return stage;
    }
}
