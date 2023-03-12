package com.gzw.kd.common.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSONObject;
import com.gzw.kd.common.R;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

@SuppressWarnings("all")
@Slf4j
@Component
public class HttpClientUtil {


    private OkHttpClient okHttpClient;

    private static OkHttpClient httpClient;

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final MediaType XML = MediaType.parse("text/xml; charset=utf-8");
    public static final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown; charset=utf-8");
    public static final MediaType FORM_URLENCODED = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
    public static final MediaType JSONNoCharset = MediaType.parse("application/json");

    @PostConstruct
    public void init() {
        httpClient = this.okHttpClient;
    }


    public static final Callback CALL_BACK = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            e.printStackTrace();
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if (response.isSuccessful()) {
                log.info("success ...");
            } else {
                throw new IOException("Unexpected code " + response);
            }
        }
    };


    /**
     * 发送Get请求
     *
     * @param url 请求的URL
     * @return R
     */
    public static R doGet(String url) throws IOException {
        Request request = new Request.Builder().url(url).build();
        return execRequest(request);
    }

    public static R doGet(String url, Map<String, String> headers ) throws IOException {
        Request.Builder requestBuilder = new Request.Builder().url(url);
        if (CollUtil.isNotEmpty(headers)) {
            headers.forEach(requestBuilder::addHeader);
        }
        return execRequest(requestBuilder.build());
    }

    public static void asyncDoPost(String url, String json) {
        try {
            httpClient.newCall(buildRequest(url, json, null)).enqueue(CALL_BACK);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送同步的Post请求
     *
     * @param url     请求的URL
     * @param json    参数，可以是json或者xml格式
     * @param headers 请求头
     * @return R，包含 msg code result
     */
    public static R syncDoPost(String url, String json, Map<String, String> headers) throws IOException {
        Request request = buildRequest(url, json, headers);
        return execRequest(request);
    }

    /**
     * 发送同步的Post请求, xml格式
     *
     * @param url     请求的URL
     * @param json    参数，xml格式
     * @param headers 请求头
     * @return R，包含 msg code result
     */
    public static R syncDoXMLPost(String url, String json, Map<String, String> headers) throws IOException {
        Request request = buildXMLRequest(url, json, headers);
        return execRequest(request);
    }

    /**
     * 发送同步的Post请求, json格式, mediaType为单纯的application/json 不带UTF-8
     *
     * @param url     请求的URL
     * @param json    参数，json格式
     * @param headers 请求头
     * @return R，包含 msg code result
     */
    public static R syncDoJSONPostNOCharset(String url, String json, Map<String, String> headers) throws IOException {
        Request request = buildRequestNoCharset(url, json, headers);
        return execRequest(request);
    }

    /**
     * 发送同步的Post请求，指定client【需要特殊配置某些参数】
     *
     * @param url     请求的URL
     * @param json    参数，可以是json或者xml格式
     * @param headers 请求头
     * @param client  指定client
     * @return R，包含 msg code result
     */
    public static R syncDoPostWithClient(String url, String json, Map<String, String> headers, OkHttpClient client) throws IOException {
        Request request = buildRequest(url, json, headers);
        return execRequestWithClient(request, client);
    }


    /**
     * 发送同步的Post请求，参数为表单格式
     *
     * @param url 请求的URL
     * @param map 参数
     * @return R，包含 msg code result
     */
    public static R syncDoPostForForm(String url, Map<String, String> map) throws IOException {
        Request request = buildRequestForForm(url, map, null);
        return execRequest(request);
    }

    /**
     * 发送同步的Post请求，参数为表单格式
     *
     * @param url     请求的URL
     * @param map     参数
     * @param headers 请求头
     * @return R，包含 msg code result
     */
    public static R syncDoPostForForm(String url, Map<String, String> map, Map<String, String> headers) throws IOException {
        Request request = buildRequestForForm(url, map, headers);
        return execRequest(request);
    }

    /**
     * 执行http请求方法
     *
     * @param request 构建的请求体
     * @return R，包含 msg code result
     */
    private static R execRequest(Request request) throws IOException {
        Response response = httpClient.newCall(request).execute();
        return buildRestResponse(response);
    }

    private static R execRequestWithClient(Request request, OkHttpClient client) throws IOException {
        Response response = client.newCall(request).execute();
        return buildRestResponse(response);
    }

    private static R buildRestResponse(Response response) {

        if (ObjectUtil.isEmpty(response.body())) {
            return R.ok();
        }
        String json = JSONObject.toJSONString(response.body());
        Map map = JSONObject.parseObject(json, Map.class);
        return R.ok().data(map);
    }

    /**
     * 构建XML或json类型的request
     *
     * @param url     URL
     * @param param   参数，可能是json或者键值对/xml格式
     * @param headers header，非必须
     * @return 返回request
     */
    public static Request buildRequest(String url, String param, Map<String, String> headers) {
        Request.Builder requestBuilder = new Request.Builder().url(url);
        if (CollUtil.isNotEmpty(headers)) {
            headers.forEach(requestBuilder::addHeader);
        }

        boolean isJson = JSONUtil.isJson(param);
        if (param == null) {
            param = isJson ? "{}" : "";
        }
        return requestBuilder.post(RequestBody.create(JSON, param)).build();
    }

    /**
     * 构建XML类型的request
     *
     * @param url     URL
     * @param param   参数，xml格式
     * @param headers header，非必须
     * @return 返回request
     */
    public static Request buildXMLRequest(String url, String param, Map<String, String> headers) {
        Request.Builder requestBuilder = new Request.Builder().url(url);
        if (CollUtil.isNotEmpty(headers)) {
            headers.forEach(requestBuilder::addHeader);
        }
        return requestBuilder.post(RequestBody.create(XML, param)).build();
    }

    /**
     * json类型的request
     *
     * @param url     URL
     * @param param   参数，可能是json或者键值对/xml格式
     * @param headers header，非必须
     * @return 返回request
     */
    public static Request buildRequestNoCharset(String url, String param, Map<String, String> headers) {
        Request.Builder requestBuilder = new Request.Builder().url(url);
        if (CollUtil.isNotEmpty(headers)) {
            headers.forEach(requestBuilder::addHeader);
        }

        boolean isJson = JSONUtil.isJson(param);
        if (param == null) {
            param = isJson ? "{}" : "";
        }
        Charset charset = CharsetUtil.CHARSET_UTF_8;
        byte[] bytes = param.getBytes(charset);
        return requestBuilder.post(RequestBody.create(JSONNoCharset, bytes)).build();
    }

    /**
     * 构建form表单类型请求
     *
     * @param url     请求的URL
     * @param param   表单参数
     * @param headers 请求头
     * @return 构建的request
     */
    public static Request buildRequestForForm(String url, Map<String, String> param, Map<String, String> headers) {
        Request.Builder requestBuilder = new Request.Builder().url(url);
        if (CollUtil.isNotEmpty(headers)) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue() == null ? StrUtil.EMPTY : entry.getValue();
                requestBuilder.addHeader(key, value);
            }
        }

        FormBody.Builder builder = new FormBody.Builder();
        if (CollUtil.isNotEmpty(param)) {
            for (Map.Entry<String, String> entry : param.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue() == null ? StrUtil.EMPTY : entry.getValue();
                builder.addEncoded(key, value);
            }
        }
        return requestBuilder.post(builder.build()).build();
    }


    /**
     * 发送同步的Post请求，参数为表单格式 不对参数进行转码
     *
     * @param url     请求的URL
     * @param map     参数
     * @param headers 请求头
     * @return R，包含 msg code result
     */
    public static R syncDoPostForFormNoEncoded(String url, Map<String, String> map, Map<String, String> headers) throws IOException {
        Request request = buildRequestForFormNoEncoded(url, map, headers);
        return execRequest(request);
    }

    /**
     * 构建form表单类型请求 不对参数进行转码
     *
     * @param url     请求的URL
     * @param param   表单参数
     * @param headers 请求头
     * @return 构建的request
     */
    public static Request buildRequestForFormNoEncoded(String url, Map<String, String> param, Map<String, String> headers) {
        Request.Builder requestBuilder = new Request.Builder().url(url);
        if (CollUtil.isNotEmpty(headers)) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue() == null ? StrUtil.EMPTY : entry.getValue();
                requestBuilder.addHeader(key, value);
            }
        }

        FormBody.Builder builder = new FormBody.Builder();
        if (CollUtil.isNotEmpty(param)) {
            for (Map.Entry<String, String> entry : param.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue() == null ? StrUtil.EMPTY : entry.getValue();
                builder.add(key, value);
            }
        }
        return requestBuilder.post(builder.build()).build();
    }


}
