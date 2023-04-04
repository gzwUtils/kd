package com.gzw.kd.controller;
import com.gzw.kd.common.R;
import com.gzw.kd.service.ChartGptService;
import com.gzw.kd.vo.input.GptInput;
import java.io.IOException;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author gzw
 * @description：
 * @since：2023/4/4 16:10
 */

@RestController
@RequestMapping("/chatGpt")
public class ChatGptController {

    @Resource
    private ChartGptService chartGptService;

    /**
     * openAI GPT-3
     *
     * @param gptInput 条件对象
     * @return 出参对象
     */
    @RequestMapping(value = "/askAi",method = RequestMethod.POST,produces = "application/json;charset=utf-8")
    public R askAi(@RequestBody GptInput gptInput) throws IOException {

        String replyStr = chartGptService.send(gptInput.getAskStr());

        return R.ok().data("gpt", replyStr);
    }
}
