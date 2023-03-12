package com.gzw.kd.assign.auth;

import com.gzw.kd.assign.common.EsignHttpHelper;
import com.gzw.kd.assign.config.EsignConfig;
import com.gzw.kd.assign.enums.EsignRequestType;
import com.gzw.kd.common.R;
import com.gzw.kd.common.exception.GlobalException;
import java.util.Map;

import static com.gzw.kd.common.Constants.API_UTL;

/**
 * @author 高志伟
 */
@SuppressWarnings("all")
public class Auth {


    private static final String E_SIGN_HOST = EsignConfig.EsignHost;
    private static final String E_SIGN_APP_ID = EsignConfig.EsignAppId;
    private static final String E_SIGN_APP_SECRET = EsignConfig.EsignAppSecret;



    /**
     * 获取个人认证链接
     * @return R
     * @param authUrl url
     * @param param json
     * @throws GlobalException
     */
    public static R getPsnAuthUrl(String authUrl,String param) throws GlobalException {

        //请求方法
        EsignRequestType requestType= EsignRequestType.POST;
        //生成签名鉴权方式的的header
        Map<String, String> header = EsignHttpHelper.signAndBuildSignAndJsonHeader(E_SIGN_APP_ID,E_SIGN_APP_SECRET,param,requestType.name(),authUrl,true);
        //发起接口请求
        return EsignHttpHelper.doCommHttp(E_SIGN_HOST, authUrl,requestType , param, header,true);
    }

    /**
     * 查询个人认证授权状态
     */
    public static R getPsnIdentityInfo(String psnAccount,String param) throws GlobalException {
        String apiUrl= API_UTL + psnAccount;
        //请求方法
        EsignRequestType requestType= EsignRequestType.GET;
        //生成签名鉴权方式的的header
        Map<String, String> header = EsignHttpHelper.signAndBuildSignAndJsonHeader(E_SIGN_APP_ID,E_SIGN_APP_SECRET,param,requestType.name(),apiUrl,true);
        //发起接口请求
        return EsignHttpHelper.doCommHttp(E_SIGN_HOST, apiUrl,requestType , param, header,true);
    }

}
