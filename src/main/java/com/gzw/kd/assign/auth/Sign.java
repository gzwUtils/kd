package com.gzw.kd.assign.auth;

import com.gzw.kd.assign.common.EsignHttpHelper;
import com.gzw.kd.assign.config.EsignConfig;
import com.gzw.kd.assign.enums.EsignRequestType;
import com.gzw.kd.common.R;
import com.gzw.kd.common.exception.GlobalException;
import java.util.Map;

/**
 * @author 高志伟
 */
public class Sign {

    private static final String E_SIGN_HOST = EsignConfig.EsignHost;
    private static final String E_SIGN_APP_ID = EsignConfig.EsignAppId;
    private static final String E_SIGN_APP_SECRET = EsignConfig.EsignAppSecret;


    public static R signRescissionUrl(String param,String apiUrl) throws GlobalException {
        EsignRequestType requestType = EsignRequestType.POST;
        //生成签名鉴权方式的的header
        Map<String, String> header = EsignHttpHelper.signAndBuildSignAndJsonHeader(E_SIGN_APP_ID,E_SIGN_APP_SECRET,param,requestType.name(),apiUrl,true);
        //发起接口请求
        return EsignHttpHelper.doCommHttp(E_SIGN_HOST, apiUrl,requestType , param, header,true);
    }


}
