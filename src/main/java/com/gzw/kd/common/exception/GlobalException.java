package com.gzw.kd.common.exception;


import com.gzw.kd.common.enums.ResultCodeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 高志伟
 */

@EqualsAndHashCode(callSuper = true)
@Data
public class GlobalException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private  Integer code;

    public GlobalException(String message,Integer code) {
        super(message);
        this.code = code;
    }

    public GlobalException(Integer code,String message, Throwable e) {
        super(message, e);
        this.code = code;
    }


    public  GlobalException(ResultCodeEnum resultCodeEnum){
        super(resultCodeEnum.getMessage());
        this.code = resultCodeEnum.getCode();
    }

    @Override
    public String toString() {
        return "GlobalException{" +"code=" + code +",message="+this.getMessage()+'}';
    }
}
