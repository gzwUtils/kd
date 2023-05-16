package com.gzw.kd.controller;

import com.alibaba.fastjson.JSON;
import static com.gzw.kd.common.Constants.LOGIN_USER_SESSION_KEY;
import com.gzw.kd.common.R;
import com.gzw.kd.common.annotation.OperatorLog;
import com.gzw.kd.common.entity.Operator;
import com.gzw.kd.common.entity.User;
import com.gzw.kd.common.generators.SnowIdGenerator;
import com.gzw.kd.common.utils.ContextUtil;
import com.gzw.kd.common.utils.SnowFlakeIdUtils;
import com.gzw.kd.controller.es.OperatorLogIndex;
import com.gzw.kd.learn.model.filter.DemoGzwFilterChain;
import com.gzw.kd.learn.model.filter.DemoGzwFilterStage;
import com.gzw.kd.vo.input.OperatorLogInput;
import com.gzw.kd.vo.output.EsLogSearchIndex;
import com.itextpdf.xmp.impl.Base64;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
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

    @Resource
    private DemoGzwFilterChain demoGzwFilterChain;

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

    @ApiOperation(value = "base64解码")
    @RequestMapping(value = "/base64",method = RequestMethod.POST,produces = "application/json;charset=utf-8")
    public R base64Decode(@RequestParam("param") String param){
        String decode = Base64.decode(param);
        return R.ok().data("code",decode);
    }

    @ApiOperation(value = "雪花ID生成")
    @OperatorLog(value = "雪花ID生成",description = "学习")
    @RequestMapping(value = "/snoIdGenerator", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public R snoIdGenerator(){
        Long generatorId = SnowFlakeIdUtils.generatorId();
        return R.ok().data("id",generatorId);
    }

    @ApiOperation(value = "ID生成")
    @OperatorLog(value = "ID生成",description = "学习")
    @RequestMapping(value = "/idGenerator", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public R idGenerator(@RequestParam(value = "bizTypePrefix") String bizTypePrefix,@RequestParam(value = "dateFormat",defaultValue = "yyMMdd",required = false) String dateFormat){
        SnowIdGenerator idGenerator = new SnowIdGenerator(bizTypePrefix, dateFormat);
        return R.ok().data("id",idGenerator.generate());
    }


    @ApiOperation(value = "责任链模式")
    @OperatorLog(value = "责任链模式",description = "学习")
    @RequestMapping(value = "/filterChain", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public R filterChain(){
        Operator operator = (Operator) ContextUtil.getHttpRequest().getSession().getAttribute(LOGIN_USER_SESSION_KEY);
        DemoGzwFilterStage demoGzwFilterStage = new DemoGzwFilterStage();
        User user = new User();
        BeanUtils.copyProperties(operator,user);
        demoGzwFilterStage.setUser(user);
        DemoGzwFilterStage stage = demoGzwFilterChain.doFilter(demoGzwFilterStage);
        List<Map<String, Object>> result = stage.getStageHandlerResult();
        log.info("chain end {}", JSON.toJSONString(result));
        return R.ok().data("result",result);
    }
}
