package com.gzw.kd.assign.common;


import com.gzw.kd.assign.enums.EsignHeaderConstant;
import com.gzw.kd.assign.enums.EsignRequestType;
import com.gzw.kd.common.R;
import com.gzw.kd.common.exception.GlobalException;
import lombok.extern.slf4j.Slf4j;
import java.util.HashMap;
import java.util.Map;
import static com.gzw.kd.common.enums.ResultCodeEnum.UNKNOWN_ERROR;


@Slf4j
@SuppressWarnings("all")
public class EsignHttpHelper {


	/**
	 * 不允许外部创建实例
	 */
	private EsignHttpHelper() {

	}

	/**
	 * @description 发送常规HTTP 请求
	 * @param reqType 请求方式
	 * @param url 请求路径
	 * @param paramStr 请求参数
	 * @throws EsignDemoException
	 */
	public static R doCommHttp(String host, String url, EsignRequestType reqType, Object paramStr , Map<String,String> httpHeader, boolean debug) throws GlobalException {
		return EsignHttpCfgHelper.sendHttp(reqType, host+url,httpHeader, paramStr, debug);
	}


	/**
	 * @description 发送文件流上传 HTTP 请求
	 * @param reqType 请求方式
	 * @param uploadUrl 请求路径
	 * @param param 请求参数
	 * @param fileContentMd5 文件fileContentMd5
	 * @param contentType 文件MIME类型
	 * @throws EsignDemoException
	 */
	public static R doUploadHttp( String uploadUrl,EsignRequestType reqType,byte[] param, String fileContentMd5,
												 String contentType, boolean debug) throws GlobalException {
		Map<String, String> uploadHeader = buildUploadHeader(fileContentMd5, contentType);
		return EsignHttpCfgHelper.sendHttp(reqType,uploadUrl, uploadHeader, param,debug);
	}



	/**
	 * @description 构建一个签名鉴权+json数据的esign请求头
	 * @return
	 */
	public static Map<String, String> buildSignAndJsonHeader(String projectId,String contentMD5,String accept,String contentType,String authMode) {

		Map<String, String> header = new HashMap<>();
		header.put("X-Tsign-Open-App-Id", projectId);
		header.put("X-Tsign-Open-Version-Sdk",EsignCoreSdkInfo.getSdkVersion());
		header.put("X-Tsign-Open-Ca-Timestamp", EsignEncryption.timeStamp());
		header.put("Accept",accept);
		header.put("Content-MD5",contentMD5);
		header.put("Content-Type", contentType);
		header.put("X-Tsign-Open-Auth-Mode", authMode);
		return header;
	}

	/**
	 * 签名计算并且构建一个签名鉴权+json数据的esign请求头
	 * @param  httpMethod
	 *      *         The name of a supported {@linkplain java.nio.charset.Charset
	 *      *         charset}
	 * @return
	 */
	public static Map<String,String> signAndBuildSignAndJsonHeader(String projectId, String secret,String paramStr,String httpMethod,String url,boolean debug) throws GlobalException {
		String contentMD5="";
		//统一转大写处理
		httpMethod = httpMethod.toUpperCase();
		if("GET".equals(httpMethod)||"DELETE".equals(httpMethod)){
			paramStr=null;
			contentMD5="";
		} else if("PUT".equals(httpMethod)||"POST".equals(httpMethod)){
			//对body体做md5摘要
			contentMD5=EsignEncryption.doContentMD5(paramStr);
		}else{
			throw new GlobalException(String.format("不支持的请求方法%s",httpMethod),UNKNOWN_ERROR.getCode());
		}
		//构造一个初步的请求头
		Map<String, String> esignHeaderMap = buildSignAndJsonHeader(projectId, contentMD5, EsignHeaderConstant.ACCEPT.VALUE(), EsignHeaderConstant.CONTENTTYPE_JSON.VALUE(), EsignHeaderConstant.AUTHMODE.VALUE());
		//排序
		url=EsignEncryption.sortApiUrl(url);
		//传入生成的bodyMd5,加上其他请求头部信息拼接成字符串
		String message = EsignEncryption.appendSignDataString(httpMethod, esignHeaderMap.get("Content-MD5"),esignHeaderMap.get("Accept"),esignHeaderMap.get("Content-Type"),esignHeaderMap.get("Headers"),esignHeaderMap.get("Date"), url);
		//整体做sha256签名
		String reqSignature = EsignEncryption.doSignatureBase64(message, secret);
		//请求头添加签名值
		esignHeaderMap.put("X-Tsign-Open-Ca-Signature",reqSignature);
		return esignHeaderMap;
	}


	/**
	 * @description 构建一个Token鉴权+jsons数据的esign请求头
	 * @return
	 */
	public static Map<String, String> buildTokenAndJsonHeader(String appid,String token) {
		Map<String, String> esignHeader = new HashMap<>();
		esignHeader.put("X-Tsign-Open-Version-Sdk",EsignCoreSdkInfo.getSdkVersion());
		esignHeader.put("Content-Type", EsignHeaderConstant.CONTENTTYPE_JSON.VALUE());
		esignHeader.put("X-Tsign-Open-App-Id", appid);
		esignHeader.put("X-Tsign-Open-Token", token);
		return esignHeader;
	}

	/**
	 * @description 构建一个form表单数据的esign请求头
	 * @return
	 */
	public static Map<String, String> buildFormDataHeader(String appid) {
		Map<String, String> esignHeader = new HashMap<>();
		esignHeader.put("X-Tsign-Open-Version-Sdk",EsignCoreSdkInfo.getSdkVersion());
		esignHeader.put("X-Tsign-Open-Authorization-Version","v2");
		esignHeader.put("Content-Type", EsignHeaderConstant.CONTENTTYPE_FORMDATA.VALUE());
		esignHeader.put("X-Tsign-Open-App-Id", appid);
		return esignHeader;
	}

	/**
	 * @description 创建文件流上传 请求头
	 *
	 * @param fileContentMd5
	 * @param contentType
	 * @return
	 */
	public static Map<String, String> buildUploadHeader(String fileContentMd5, String contentType) {
		Map<String, String> header = new HashMap<>();
		header.put("Content-MD5", fileContentMd5);
		header.put("Content-Type", contentType);

		return header;
	}

	// ------------------------------私有方法end----------------------------------------------
}
