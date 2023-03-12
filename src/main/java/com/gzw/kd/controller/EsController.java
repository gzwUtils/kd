package com.gzw.kd.controller;

import com.gzw.kd.common.R;
import com.gzw.kd.controller.es.OperatorLogIndex;
import com.gzw.kd.vo.input.OperatorLogInput;
import com.gzw.kd.vo.output.EsLogSearchIndex;
import com.itextpdf.xmp.impl.Base64;
import io.swagger.annotations.Api;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * @author gzw
 * @description：
 * @since：2023/3/2 20:36
 */
@Api(value = "es接口")
@RequestMapping("/es")
@RestController
public class EsController {

    @Resource
    private OperatorLogIndex operatorLogIndex;

    @RequestMapping(value = "/getAllLog",method = RequestMethod.POST,produces = "application/json;charset=utf-8")
    public R getAllLog(@RequestBody OperatorLogInput operatorLogInput){
        List<EsLogSearchIndex> allLog = operatorLogIndex.getAllLog(operatorLogInput);
        return R.ok().data("allLog",allLog);
    }

    @RequestMapping(value = "/saveLog",method = RequestMethod.POST,produces = "application/json;charset=utf-8")
    public R saveLog(@RequestBody OperatorLogInput operatorLogInput){
         operatorLogIndex.save(operatorLogInput);
         return R.ok();
    }

    @RequestMapping(value = "/base64",method = RequestMethod.POST,produces = "application/json;charset=utf-8")
    public R base64Decode(@RequestParam("param") String param){
        String decode = Base64.decode(param);
        return R.ok().data("code",decode);
    }
}
