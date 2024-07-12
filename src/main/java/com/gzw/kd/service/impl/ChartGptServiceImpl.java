package com.gzw.kd.service.impl;

import cn.hutool.http.Header;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSONObject;
import com.gzw.kd.common.R;
import com.gzw.kd.common.utils.HttpClientUtil;
import com.gzw.kd.service.ChartGptService;
import com.gzw.kd.vo.output.GptOutput;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author gzw
 * @description： gpt
 * @since：2023/4/4 16:19
 */
@Slf4j
@Service
public class ChartGptServiceImpl implements ChartGptService {


    @Value("${ChatGPT.apiKey}")
    private String apiKey;

    @Value("${ChatGPT.maxTokens}")
    private String maxTokens;

    @Value("${ChatGPT.temperature}")
    private String temperature;

    @Value("${ChatGPT.model}")
    private String model;

    @Value("${ChatGPT.host}")
    private String host;


    @Override
    public String send(String prompt) throws Exception {
        log.warn("prompt.............................................{}",prompt);
        JSONObject bodyJson = new JSONObject();
        Map<String, String> header = new HashMap<>(2);
        header.put(Header.AUTHORIZATION+"","Bearer " + apiKey);
        bodyJson.put("prompt", prompt);
        bodyJson.put("max_tokens", Integer.parseInt(maxTokens));
        bodyJson.put("temperature", Double.parseDouble(temperature));
        R result= HttpClientUtil.syncDoPost(host+"/v1/engines/" + model + "/completions",JSONUtil.toJsonStr(bodyJson),header);
        log.info("resStr: {}", result.getData());
        GptOutput gptResponse = JSONUtil.toBean(JSONObject.toJSONString(result.getData()), GptOutput.class);
        return gptResponse.getChoices().get(0).getText().replaceAll("\\n","").replaceAll("//","");
    }
}
