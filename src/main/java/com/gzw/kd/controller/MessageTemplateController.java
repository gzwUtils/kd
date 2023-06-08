package com.gzw.kd.controller;

import cn.hutool.core.util.StrUtil;
import com.gzw.kd.common.R;
import com.gzw.kd.common.annotation.OperatorLog;
import com.gzw.kd.common.entity.TemplateInfo;
import com.gzw.kd.common.enums.ResultCodeEnum;
import com.gzw.kd.common.exception.GlobalException;
import com.gzw.kd.service.MessageTemplateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * @author gzw
 * @description： 消息模版
 * @since：2023/5/29 10:34
 */

@Api(tags = "消息模版")
@RequestMapping("/mt")
@RestController
public class MessageTemplateController {


    @Resource
    private MessageTemplateService messageTemplateService;

    /**
     * 模版新增
     */
    @PostMapping("/save")
    @ApiOperation("/保存数据")
    @OperatorLog(value = "模版新增", description = "模版新增")
    public R save(@RequestBody TemplateInfo templateInfo) {
        if (StringUtils.isBlank(templateInfo.getCreator())) {
            throw new GlobalException(ResultCodeEnum.PARAM_ERROR);
        }
        messageTemplateService.registerTemplate(templateInfo);
        return R.ok();
    }


    /**
     * 查询模版
     */
    @PostMapping("/find")
    @ApiOperation("/查询模版")
    @OperatorLog(value = "查询所有模版", description = "查询模版信息")
    public R select(@ApiParam("0 启用 1 停用") @RequestParam(value = "isDeleted", defaultValue = "0", required = false) String isDeleted) {
        List<TemplateInfo> allInfo = messageTemplateService.findAllByIsDeleted(Integer.valueOf(isDeleted));
        return R.ok().data("allInfo", allInfo);
    }

    /**
     * 统计未停用的条数
     */
    @PostMapping("/count")
    @ApiOperation("/统计模版")
    @OperatorLog(value = "统计条数", description = "统计条数")
    public R count(@ApiParam("0 启用 1 停用") @RequestParam(value = "isDeleted", defaultValue = "0", required = false) String isDeleted) {
        Long count = messageTemplateService.countByIsDeletedEquals(Integer.valueOf(isDeleted));
        return R.ok().data("count", count);
    }

    /**
     * 根据Id复制
     */
    @PostMapping("copy/{id}")
    @ApiOperation("/根据Id复制")
    public void copyById(@PathVariable("id") Long id) {
        messageTemplateService.copy(id);
    }


    /**
     * 根据Id删除
     * id多个用逗号分隔开
     */
    @DeleteMapping("delete/{id}")
    @ApiOperation("/根据Ids删除")
    public void deleteByIds(@PathVariable("id") String id) {
        if (StrUtil.isNotBlank(id)) {
            List<Long> idList = Arrays.stream(id.split(StrUtil.COMMA)).map(Long::valueOf).collect(Collectors.toList());
            messageTemplateService.deleteByIds(idList);
        }
    }

    /**
     * 启动模板的定时任务
     */
    @PostMapping("start/{id}")
    @ApiOperation("/启动模板的定时任务")
    public R start(@RequestBody @PathVariable("id") Long id) {
        return messageTemplateService.startCronTask(id);
    }

    /**
     * 暂停模板的定时任务
     */
    @PostMapping("stop/{id}")
    @ApiOperation("/暂停模板的定时任务")
    public R stop(@RequestBody @PathVariable("id") Long id) {
        return messageTemplateService.stopCronTask(id);
    }

}
