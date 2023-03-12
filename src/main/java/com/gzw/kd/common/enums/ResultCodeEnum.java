package com.gzw.kd.common.enums;

import lombok.Getter;

/**
 * @author gzw
 * @version 1.0
 * @Date 2022/10/11
 * @dec
 */

@Getter
public enum ResultCodeEnum {


    /**
     * result
     */

    SUCCESS(true,20000,"成功"),
    UNKNOWN_ERROR(false,20001,"未知错误"),
    PARAM_ERROR(false,20002,"参数错误"),
    PARAM_ABSENT(false,20003,"参数缺失"),
    OPERATOR_FREQUENTLY(false,20004,"操作过于频繁，请稍后再试"),
    BLACK_LIST(false,20005,"黑名单"),
    NULL_POINT(false,20006,"空指针异常"),
    HTTP_CLIENT_ERROR(false,20007,"http 请求异常"),
    HTTP_SIGN_NULL(false,20008,"http 请求签名为空"),
    HTTP_TIMESTAMP_NULL(false,20009,"http 请求时间戳为空"),
    HTTP_TIMESTAMP_EXPIRE(false,20010,"http 请求时间已失效"),
    HTTP_SIGN_ERROR(false,20011,"http 请求签名异常"),
    NoHandler_Found(false,20012,"路径不存在，请检查路径是否正确"),
    DuplicateKey(false,20013,"已存在该记录"),
    USER_STOP(false,20014,"用户被禁用 请联系管理员"),
    FILE_NOT_EXIST(false,20015,"文件不存在"),
    SIGN_ERROR(false,20016,"加密签名异常"),
    ID_GENERATOR_ERROR(false,20017,"id generator error"),
    Failed(false, 20018,"服务繁忙（1），请稍后重试"),


    /**
     * 支付宝
     */
    MachineOrderAlipayException(false,7003, "支付宝接口调用成功，但返回错误"),
    MachineOrderAliUnPay(false,7004,"支付宝订单状态错误"),

    /**
     * canal
     */
    ERR_CANAL_MESSAGE_PARSE(false,10001, "canal message 解析异常"),
    ERR_WRITE_OUT(false,10002,"write out异常"),
    ERR_ADAPTER_FAILURE(false,10003,"adapter数据同步处理失败 "),
    ERR_ADAPTER(false,10004,"adapter数据同步处理异常"),
    ERR_ES_DEL(false,10005,"Elasticsearch执行DELETE同步失败！"),
    ERR_ES_UPS(false,10006,"Elasticsearch执行UPSERT同步失败！"),
    ERR_REDIS_SYNC(false,10007,"Redis执行同步失败！"),


    /**
     * es
     */

    END_POINT_ABSENT(false,30001,"elasticsearch 节点信息获取失败，请在配置文件中配置"),

    /**
     * redis
     */

    REDIS_LOCK_EXIST(false,40001,"you can not do it , because another has get the lock  ===");



    private final Boolean success;

    private final Integer code;

    private final String message;

    ResultCodeEnum(Boolean success, Integer code, String message) {
        this.success = success;
        this.code = code;
        this.message = message;
    }
}
