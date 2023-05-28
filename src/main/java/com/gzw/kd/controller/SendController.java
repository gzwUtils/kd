package com.gzw.kd.controller;
import com.gzw.kd.common.R;
import com.gzw.kd.common.annotation.OperatorLog;
import com.gzw.kd.common.entity.TemplateInfo;
import com.gzw.kd.send.service.SendService;
import com.gzw.kd.service.MessageTemplateService;
import com.gzw.kd.vo.input.BatchSendInput;
import com.gzw.kd.vo.input.SendInput;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * @author gzw
 * @description：
 * @since：2023/5/24 15:57
 */
@Api(tags = "mq")
@RequestMapping("/mq")
@RestController
public class SendController {

    @Resource
    private SendService sendService;

    @Resource
    private MessageTemplateService messageTemplateService;

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


    /**
    模版新增
     */
    @PostMapping("/save")
    @ApiOperation("/保存数据")
    @OperatorLog(value = "模版新增",description = "模版新增")
    public R save(@RequestBody TemplateInfo templateInfo) {
        Integer result = messageTemplateService.registerTemplate(templateInfo);
        if(result==null){
            return R.error();
        }
        return R.ok();
    }


    /**
     查询模版
     */
    @PostMapping("/find")
    @ApiOperation("/查询模版")
    @OperatorLog(value = "查询所有模版",description = "查询模版信息")
    public R select(@ApiParam("0 启用 1 停用") @RequestParam("isDeleted") String isDeleted) {
        List<TemplateInfo> allInfo = messageTemplateService.findAllByIsDeleted(Integer.valueOf(isDeleted));
        return R.ok().data("allInfo",allInfo);
    }

    /**
     统计未停用的条数
     */
    @PostMapping("/count")
    @ApiOperation("/统计模版")
    @OperatorLog(value = "统计未停用的条数",description = "统计未停用的条数")
    public R count(@ApiParam("0 启用 1 停用") @RequestParam("isDeleted") String isDeleted) {
        Long count = messageTemplateService.countByIsDeletedEquals(Integer.valueOf(isDeleted));
        return R.ok().data("count",count);
    }
}
