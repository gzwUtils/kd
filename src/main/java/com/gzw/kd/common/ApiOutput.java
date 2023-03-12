package com.gzw.kd.common;

import com.gzw.kd.common.enums.ResultCodeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author gzw
 * @description：
 * @since：2022/7/6 下午4:26
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("all")
public class ApiOutput<T> {
    /** 状态码 */
    private String status;
    /** 消息 */
    private String msg;
    /** 请求状态，true正常、false异常 */
    private boolean success;
    /** 返回的结果对象 */
    private T result;


    public static ApiOutput<?> success(){
        return new ApiOutput().setSuccess(true);
    }

    public static <T> ApiOutput success(T result){
        return new ApiOutput().setSuccess(true).setResult(result);
    }

    public static ApiOutput<?> failure(String errorMsg) {
        return new ApiOutput().setMsg(errorMsg).setStatus(String.valueOf(ResultCodeEnum.UNKNOWN_ERROR.getCode()));
    }

    public static <T> ApiOutput<T> failure(String status, String errorMsg) {
        return new ApiOutput().setStatus(status).setMsg(errorMsg);
    }
}
