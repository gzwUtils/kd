package com.gzw.kd.controller;
import com.gzw.kd.common.R;
import com.gzw.kd.common.enums.ResultCodeEnum;
import com.gzw.kd.service.ChartGptService;
import com.gzw.kd.vo.input.GptInput;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import javax.annotation.Resource;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author gzw
 * @description：
 * @since：2023/4/4 16:10
 */
@Api(tags = "gpt")
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
    @Retryable(value = {SocketTimeoutException.class, ConnectException.class},maxAttempts = 2,backoff = @Backoff(delay = 1000,multiplier = 2))
    @ApiOperation(value = "提问")
    @RequestMapping(value = "/askAi",method = RequestMethod.POST,produces = "application/json;charset=utf-8")
    public R askAi(@RequestBody GptInput gptInput) throws Exception {
        String replyStr = chartGptService.send(gptInput.getAskStr());
        return R.ok().data("gpt", replyStr);
    }


    @SuppressWarnings("unused")
    @Recover
    public R fallback() {
        return R.setResult(ResultCodeEnum.Failed);
    }
}
