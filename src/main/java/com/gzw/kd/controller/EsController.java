package com.gzw.kd.controller;


import com.gzw.kd.common.R;
import com.gzw.kd.common.annotation.OperatorLog;
import com.gzw.kd.controller.es.OperatorLogIndex;
import com.gzw.kd.vo.input.OperatorLogInput;
import com.gzw.kd.vo.output.EsLogSearchIndex;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * @author gzw
 * @description：
 * @since：2023/3/2 20:36
 */
@Api(tags = "es接口")
@RequestMapping("/es")
@RestController
@Slf4j
public class EsController {

    @Resource
    private OperatorLogIndex operatorLogIndex;



    @ApiOperation(value = "获取操作日志")
    @OperatorLog(value = "获取操作日志",description = "学习")
    @RequestMapping(value = "/getAllLog",method = RequestMethod.POST,produces = "application/json;charset=utf-8")
    public R getAllLog(@RequestBody OperatorLogInput operatorLogInput){
        List<EsLogSearchIndex> allLog = operatorLogIndex.getAllLog(operatorLogInput);
        return R.ok().data("allLog",allLog);
    }

    @ApiOperation(value = "保存日志")
    @RequestMapping(value = "/saveLog",method = RequestMethod.POST,produces = "application/json;charset=utf-8")
    public R saveLog(@RequestBody OperatorLogInput operatorLogInput){
         operatorLogIndex.save(operatorLogInput);
         return R.ok();
    }


}
