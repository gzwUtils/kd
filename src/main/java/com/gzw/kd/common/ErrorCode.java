package com.gzw.kd.common;

/**
 * @author gzw
 * @description： 业务上抛异常的错误码，对应错误详情在resources下的i18n中
 * @since：2023/5/6 16:10
 */
public interface ErrorCode {

    /* *********************  1000-1999 ********************* */

    /**
     * 第三方登陆失败
     */
    int ERR_NO_LOGIN = 1001;

    /**
     * 没有接入权限
     */
   int  ERR_NO_ACCESS_PRIVILEGE = 1002;

}
