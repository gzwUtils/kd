package com.gzw.kd.common.exception;

import com.gzw.kd.common.enums.ResultCodeEnum;

/**
 * @author gzw
 * @description： 用于抛出各种API异常
 * @since：2023/4/20 20:44
 */
@SuppressWarnings("all")
public class Asserts {


    public static void fail(int  code) {
        throw new GlobalException(code);
    }

    public static void fail(String message) {
        throw new GlobalException(message, ResultCodeEnum.UNKNOWN_ERROR.getCode());
    }

    public static void fail(ResultCodeEnum resultCodeEnum){
        throw new GlobalException(resultCodeEnum);
    }
}
