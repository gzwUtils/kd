package com.gzw.kd.assign.common;


@SuppressWarnings("all")
public class EsignCoreSdkInfo {


    private static final String SdkVersion="Esign-Sdk-Core1.0";
    private static final String SupportedVersion="JDK1.7 MORE THAN";
    private static final String Info="sdk-esign-api核心工具包,主要处理e签宝公有云产品接口调用时的签名计算以及网络请求,通过EsignHttpHelper.signAndBuildSignAndJsonHeader构造签名鉴权+json数据格式的请求头,通过HttpHelper.doCommHttp方法入参发起网络请求。让开发者无需关注具体的请求签名算法，专注于接口业务的json参数构造";


    public static String getSdkVersion() {
        return SdkVersion;
    }

    public static String getInfo() {
        return Info;
    }

    public static String getSupportedVersion() {
        return SupportedVersion;
    }
}
