package com.gzw.kd.learn.model.filter;

/**
 * 项目名称：spring-demo
 * 类 名 称：GzwFilter
 * 类 描 述：责任链模式-功能接口
 * 创建时间：2022/3/28 10:21 下午
 *
 * @author gzw
 */
@SuppressWarnings("all")
public interface GzwFilter<U> {


     U doFilter(U lastFilterStage);
}
