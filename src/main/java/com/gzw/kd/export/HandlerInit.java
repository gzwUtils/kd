package com.gzw.kd.export;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * @author 高志伟
 */
@SuppressWarnings("all")
@Slf4j
public abstract class HandlerInit {

    private final Map<String, FieldHandler> handlerRegistry;

    /**
     * 初始化生成字段handler map
     *
     * @return 初始化完成的map
     */
    protected abstract Map<String, FieldHandler> initialize();

    public HandlerInit(Map<String, FieldHandler> handlerRegistry) {
        this.handlerRegistry = handlerRegistry;
    }

    @PostConstruct
    public void postConstruct() {
        handlerRegistry.putAll(initialize());
    }

    protected String formatLocalDateTime(final Object value) {
        if (ObjectUtil.isNull(value)) {
            return StrUtil.EMPTY;
        }
        String str = (String) value;
        if (StrUtil.isBlank(str)) {
            return StrUtil.EMPTY;
        }
        return str.replace("T", StrUtil.SPACE);
    }

    protected <T, R> R getOrDefaultVal(T t, Function<T, R> f, R defaultValue) {
        return Objects.nonNull(t) ? f.apply(t) : defaultValue;
    }
}
