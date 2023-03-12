package com.gzw.kd.common;

import com.gzw.kd.common.enums.ResultCodeEnum;
import lombok.Data;

/**
 * @author gzw
 * @version 1.0
 * @Date 2022/10/13
 * @dec
 */

@Data
public class Response{

    private Integer code;

    private boolean success;

    private String message;

    private Object data;

    public static Response success(){

        return success(ResultCodeEnum.SUCCESS);
    }

    public static Response success(ResultCodeEnum resultCodeEnum) {
        return success(resultCodeEnum.getCode(), resultCodeEnum.getMessage(), null);
    }

    public static Response success(Object data) {
        return success(ResultCodeEnum.SUCCESS.getCode(), ResultCodeEnum.SUCCESS.getMessage(), data);
    }

    public static Response success(Integer code, String message, Object data) {
        Response response = new Response();
        response.setCode(code);
        response.setMessage(message);
        response.setData(data);
        response.setSuccess(true);
        return response;
    }

    public static Response error(Integer code, String message, Object data) {
        Response response = new Response();
        response.setCode(code);
        response.setMessage(message);
        response.setData(data);
        return response;
    }

    public static Response error() {
        return error(ResultCodeEnum.UNKNOWN_ERROR);
    }

    public static Response error(ResultCodeEnum resultCodeEnum) {
        return error(resultCodeEnum.getCode(), resultCodeEnum.getMessage());
    }

    public static Response error(Integer code, String message) {
        return error(code, message, null);
    }
}
