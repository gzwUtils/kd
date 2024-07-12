package com.gzw.kd.common.exception;

import com.gzw.kd.common.R;
import com.gzw.kd.common.enums.ResultCodeEnum;
import javax.servlet.http.HttpServletRequest;
import com.gzw.kd.mail.MailUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.NoHandlerFoundException;


/**
 * @author gzw
 * @version 1.0
 * @Date 2022/10/13
 * @dec
 */
@SuppressWarnings("all")
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @Value("${mail.auto:false}")
    private boolean  mailIsEnabled;

    @Value("${mail.mailtoHtml:false}")
    private  boolean  mailtoHtml;

    @Value("${mail.address: }")
    private String [] mails;



    /**-------- 通用异常处理方法 --------**/
    @ExceptionHandler(Exception.class)
    public R error(HttpServletRequest request,Exception e) {
        StringBuffer url = request.getRequestURL();
        log.error("error url {} message {}",url.toString(),e.getMessage(),e);
        if(mailIsEnabled){
            MailUtil.getMailSend().sendEmail(url.toString(),e.getMessage(),mails,mailtoHtml);
        }
        return R.error().message(e.getMessage());
    }

    /**-------- 指定异常处理方法 --------**/
    @ExceptionHandler(NullPointerException.class)
    public R error(HttpServletRequest request,NullPointerException e) {
        StringBuffer url = request.getRequestURL();
        log.error("error url {} message {}",url.toString(),e.getMessage(),e);
        if(mailIsEnabled){
            MailUtil.getMailSend().sendEmail(url.toString(),e.getMessage(),mails,mailtoHtml);
        }
        return R.setResult(ResultCodeEnum.NULL_POINT);
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public R error(HttpServletRequest request,HttpClientErrorException e) {
        StringBuffer url = request.getRequestURL();
        log.error("error url {} message {}",url.toString(),e.getMessage(),e);
        if(mailIsEnabled){
            MailUtil.getMailSend().sendEmail(url.toString(),e.getMessage(),mails,mailtoHtml);
        }
        return R.setResult(ResultCodeEnum.HTTP_CLIENT_ERROR);
    }

    /**-------- 自定义定异常处理方法 --------**/
    @ExceptionHandler(GlobalException.class)
    public R error(HttpServletRequest request,GlobalException e) {
        StringBuffer url = request.getRequestURL();
        log.error("GlobalException error url {} message {}",url.toString(),e.getMessage(),e);
        if(mailIsEnabled){
            MailUtil.getMailSend().sendEmail(url.toString(),e.getMessage(),mails,mailtoHtml);
        }
        return R.error().message(e.getMessage()).code(e.getCode());
    }

    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    public R error(HttpServletRequest request, HttpRequestMethodNotSupportedException e) {
        String path = request.getRequestURL().toString();
        log.error("HttpRequestMethodNotSupportedException error ,接口=[{}],错误信息=[{}]", path, e.getMessage());
        if(mailIsEnabled){
            MailUtil.getMailSend().sendEmail(path,e.getMessage(),mails,mailtoHtml);
        }
        return R.error().code(ResultCodeEnum.HTTP_CLIENT_ERROR.getCode()).message(e.getMessage());
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public R error(HttpServletRequest request, MethodArgumentNotValidException e) {
        String path = request.getRequestURL().toString();
        log.error("MethodArgumentNotValidException error ,接口=[{}],错误信息=[{}]", path, e.getMessage());
        if(mailIsEnabled){
            MailUtil.getMailSend().sendEmail(path,e.getMessage(),mails,mailtoHtml);
        }
        return R.error().code(ResultCodeEnum.PARAM_ERROR.getCode()).message(e.getBindingResult().getFieldError().getDefaultMessage());
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public R handlerNoFoundException(HttpServletRequest request,NoHandlerFoundException e) {
        String path = request.getRequestURL().toString();
        log.error("handlerNoFoundException error ,接口=[{}],错误信息=[{}]", path, e.getMessage());
        if(mailIsEnabled){
            MailUtil.getMailSend().sendEmail(path,e.getMessage(),mails,mailtoHtml);
        }
        return R.setResult(ResultCodeEnum.NoHandler_Found);
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public R handleDuplicateKeyException(HttpServletRequest request,DuplicateKeyException e){
        String path = request.getRequestURL().toString();
        log.error("handleDuplicateKeyException error ,接口=[{}],错误信息=[{}]", path, e.getMessage());
        if(mailIsEnabled){
            MailUtil.getMailSend().sendEmail(path,e.getMessage(),mails,mailtoHtml);
        }
        return R.setResult(ResultCodeEnum.DuplicateKey);
    }
}
