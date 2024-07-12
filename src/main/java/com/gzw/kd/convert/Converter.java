package com.gzw.kd.convert;

import java.util.function.Function;

/**
 * @author gzw
 * @description： 类型转化
 * @since：2023/5/31 11:27
 */
@SuppressWarnings("all")
public abstract class Converter<S,T> implements Function<S,T> {

    /**
     * s转t
     * @param s input
     * @return t
     */
    protected abstract T doForward(S s);

    /**
     * t转s
     * @param t input
     * @return s
     */

    protected abstract S doBackward(T t);
}
