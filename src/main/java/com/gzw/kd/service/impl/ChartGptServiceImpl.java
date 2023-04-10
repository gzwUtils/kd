package com.gzw.kd.service.impl;

import cn.hutool.http.Header;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSONObject;
import com.gzw.kd.service.ChartGptService;
import com.gzw.kd.vo.output.GptOutput;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author gzw
 * @description：
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
    public String send(String prompt) throws IOException {
        JSONObject bodyJson = new JSONObject();
        bodyJson.put("prompt", prompt);
        bodyJson.put("max_tokens", Integer.parseInt(maxTokens));
        bodyJson.put("temperature", Double.parseDouble(temperature));
        HttpResponse execute = HttpUtil.createPost(host+"/v1/engines/" + model + "/completions")
                .header(Header.AUTHORIZATION, "Bearer " + apiKey).body(JSONUtil.toJsonStr(bodyJson)).execute();
        log.info("resStr: {}", execute.body());
        GptOutput gptResponse = JSONUtil.toBean(execute.body(), GptOutput.class);
        return gptResponse.getChoices().get(0).getText().replaceAll("\\n","").replaceAll("//","");
    }
}
