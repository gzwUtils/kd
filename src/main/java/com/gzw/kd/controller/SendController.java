package com.gzw.kd.controller;
import com.gzw.kd.common.R;
import com.gzw.kd.common.annotation.OperatorLog;
import com.gzw.kd.send.service.SendService;
import com.gzw.kd.vo.input.BatchSendInput;
import com.gzw.kd.vo.input.SendInput;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * @author gzw
 * @description：
 * @since：2023/5/24 15:57
 */
@Api(tags = "消息推送")
@RequestMapping("/mq")
@RestController
public class SendController {

    @Resource
    private SendService sendService;



    /**
     * 单个文案下发相同的人
     *
     * @param sendInput input
     * @return result
     */
    @ApiOperation(value = "下发接口", notes = "多渠道多类型下发消息，目前支持邮件和短信，类型支持：验证码、通知类、营销类。")
    @PostMapping("/send")
    @OperatorLog(value = "下发消息",description = "多渠道多类型下发消息")
    public R send(@RequestBody SendInput sendInput) {
        return sendService.send(sendInput);
    }

    /**
     * 不同文案下发到不同的人
     *
     * @param batchSendInput batch input
     * @return result
     */
    @OperatorLog(value = "批量下发消息",description = "多渠道多类型下发消息")
    @ApiOperation(value = "batch下发接口", notes = "多渠道多类型下发消息，目前支持邮件和短信，类型支持：验证码、通知类、营销类。")
    @PostMapping("/batchSend")
    public R batchSend(@RequestBody BatchSendInput batchSendInput) {
        return sendService.batchSend(batchSendInput);
    }



}
