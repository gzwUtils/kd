package com.gzw.kd.controller;

import com.gzw.kd.common.R;
import com.gzw.kd.common.generators.SnowFlakeIdGenerator;
import com.gzw.kd.controller.es.OperatorLogIndex;
import com.gzw.kd.vo.input.OperatorLogInput;
import com.gzw.kd.vo.output.EsLogSearchIndex;
import com.itextpdf.xmp.impl.Base64;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * @author gzw
 * @description：
 * @since：2023/3/2 20:36
 */
@Api(tags = "es接口")
@RequestMapping("/es")
@RestController
public class EsController {

    @Resource
    private OperatorLogIndex operatorLogIndex;

    @ApiOperation(value = "获取操作日志")
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

    @ApiOperation(value = "base64解码")
    @RequestMapping(value = "/base64",method = RequestMethod.POST,produces = "application/json;charset=utf-8")
    public R base64Decode(@RequestParam("param") String param){
        String decode = Base64.decode(param);
        return R.ok().data("code",decode);
    }

    @ApiImplicitParams({@ApiImplicitParam(name = "datacenterId", value = "数据中心ID", paramType = "query", dataTypeClass = Long.class, required = true),@ApiImplicitParam(name = "workerId", value = "工作ID", paramType = "query", dataTypeClass = Long.class, required = true)})
    @ApiOperation(value = "雪花ID生成")
    @RequestMapping(value = "/snoIdGenerator", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public R snoIdGenerator(long datacenterId,long workerId){
        SnowFlakeIdGenerator idGenerator = new SnowFlakeIdGenerator(workerId, datacenterId);
        System.out.println(System.currentTimeMillis());
        return R.ok().data("id",idGenerator.nextId());
    }
}
