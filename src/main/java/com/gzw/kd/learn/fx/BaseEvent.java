package com.gzw.kd.learn.fx;

import lombok.Data;
import org.springframework.core.ResolvableType;
import org.springframework.core.ResolvableTypeProvider;

/**
 * @author gzw
 * @description： 泛型擦除
 * @since：2024/2/1 12:53
 */
@Data
public class BaseEvent<T> implements ResolvableTypeProvider {


    private T data;

    private String type;

    public BaseEvent(T data, String type) {
        this.data = data;
        this.type = type;
    }

    @Override
    public ResolvableType getResolvableType() {
        return ResolvableType.forClassWithGenerics(getClass(),ResolvableType.forInstance(getData()));
    }
}
