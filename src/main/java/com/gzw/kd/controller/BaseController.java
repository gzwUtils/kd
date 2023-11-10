package com.gzw.kd.controller;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.gzw.kd.common.R;
import com.gzw.kd.common.annotation.OperatorLog;
import com.gzw.kd.common.annotation.Resubmit;
import com.gzw.kd.common.entity.Operator;
import com.gzw.kd.common.generators.SnowIdGenerator;
import com.gzw.kd.common.init.ServiceConfigInit;
import com.gzw.kd.common.utils.*;
import com.gzw.kd.common.entity.MyQueue;
import com.gzw.kd.learn.model.callback.Client;
import com.gzw.kd.learn.model.callback.Server;
import com.gzw.kd.learn.model.filter.DemoGzwFilterChain;
import com.gzw.kd.learn.model.filter.DemoGzwFilterStage;
import com.gzw.kd.learn.model.model.GzwThreadDemo;
import com.gzw.kd.service.ConfigService;
import com.gzw.kd.vo.input.ValidTest;
import com.itextpdf.xmp.impl.Base64;
import io.swagger.annotations.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import static com.gzw.kd.common.Constants.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author gzw
 * @version 1.0
 * @Date 2022/10/20
 * @dec
 */
@Slf4j
@Api(tags = "学习")
@RequestMapping("/base")
@RestController
@SuppressWarnings("all")
public class BaseController {

    @Resource
    private MyLinkedBlockQueue<Object> linkedBlockQueue;


    @Autowired
    private GzwThreadDemo gzwThreadDemo;

    @Resource
    private EventBusUtils  eventBusUtils;

    @Resource
    private DemoGzwFilterChain demoGzwFilterChain;

    @Resource
    private ServiceConfigInit serviceConfigInit;

    @Resource
    private ConfigService configService;


    @Resource
    FileUploadUtil fileUploadUtil;

    @ApiOperation(value = "入队 阻塞")
    @OperatorLog(value = "入队 阻塞 ",description = "入队 阻塞")
    @ApiImplicitParams({@ApiImplicitParam(name = "Accept-Language", value = "语言", paramType = "header", dataTypeClass = String.class, required = false),@ApiImplicitParam(name = "other-token", value = "token校验", paramType = "header", dataTypeClass = String.class, required = false),@ApiImplicitParam(name = "sign", value = "签名", paramType = "header", dataTypeClass = String.class, required = false),@ApiImplicitParam(name = "timestamp", value = "时间戳", paramType = "header", dataTypeClass = String.class, required = false)})
    @RequestMapping(value = "/put", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public R put(@RequestBody MyQueue myQueue) {
        if (myQueue.getSize() != null) {
            linkedBlockQueue.setSize(myQueue.getSize());
        }
        log.info("--------------put------------------- ,queueSize {} dataSize {}", myQueue.getSize(), myQueue.getData().size());
        new Thread(() -> {
            for (int i = INT_ZERO; i < myQueue.getData().size(); i++) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    log.error("test put method Interrupt", e);
                }
                linkedBlockQueue.put(myQueue.getData().get(i));
                log.info("入队：{} {}", i, JSONObject.toJSONString(myQueue.getData().get(i)));
            }
        }, "put").start();
        return R.ok();
    }

    @ApiOperation(value = "出队 阻塞")
    @OperatorLog(value = "出队 阻塞 ",description = "出队 阻塞")
    @ApiImplicitParams({@ApiImplicitParam(name = "Accept-Language", value = "语言", paramType = "header", dataTypeClass = String.class, required = false),@ApiImplicitParam(name = "other-token", value = "token校验", paramType = "header", dataTypeClass = String.class, required = false),@ApiImplicitParam(name = "sign", value = "签名", paramType = "header", dataTypeClass = String.class, required = false),@ApiImplicitParam(name = "timestamp", value = "时间戳", paramType = "header", dataTypeClass = String.class, required = false)})
    @RequestMapping(value = "/take", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public R take(@RequestBody MyQueue myQueue) {
        if (myQueue.getSize() != null) {
            linkedBlockQueue.setSize(myQueue.getSize());
        }
        log.info("---------------take------------------,queueSize {}", myQueue.getSize());
        new Thread(() -> {
            for (int i = INT_ZERO; i < INT_180; i++) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    log.error("test take method Interrupt", e);
                }
                Object take = linkedBlockQueue.take();
                log.info("出队：{} {}", i,JSONObject.toJSONString(take));
            }
        }, "take").start();
        return R.ok();
    }

    @ApiOperation(value = "redis 限流")
    @OperatorLog(value = "redis limit flow ",description = "redis 限流")
    @RequestMapping(value = "/limitFlow",method = RequestMethod.POST,produces = "application/json;charset=utf-8")
    public  R limitFlow (){
        return RedisLimitFlow.limitFlow("limitFlow",60000l,5);
    }


    @ApiOperation(value = "开启线程")
    @OperatorLog(value = "开启线程",description = "学习")
    @RequestMapping(value = "/gzwThreadDemo",method = RequestMethod.POST,produces = "application/json;charset=utf-8")
    public  R start (){
        gzwThreadDemo.start();
        return R.ok();
    }

    @ApiOperation(value = "关闭线程")
    @OperatorLog(value = "关闭线程",description = "学习")
    @RequestMapping(value = "/shutdown", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public R shutdown() {
        gzwThreadDemo.shutdown();
        return R.ok();
    }


    @ApiImplicitParams({@ApiImplicitParam(name = "Accept-Language", value = "语言", paramType = "header", dataTypeClass = String.class, required = false),@ApiImplicitParam(name = "other-token", value = "token校验", paramType = "header", dataTypeClass = String.class, required = false),@ApiImplicitParam(name = "sign", value = "签名", paramType = "header", dataTypeClass = String.class, required = false),@ApiImplicitParam(name = "timestamp", value = "时间戳", paramType = "header", dataTypeClass = String.class, required = false)})
    @ApiOperation(value = "参数校验")
    @OperatorLog(value = "参数校验",description = "学习")
    @RequestMapping(value = "/valid", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public R valid(@RequestBody @Validated ValidTest validTest){
        log.info("valid ........{}", JSON.toJSONString(validTest));
        return R.ok();
    }


    @ApiOperation(value = "消息事件推送")
    @OperatorLog(value = "观察者模式",description = "学习")
    @RequestMapping(value = "/push", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public R push(@RequestParam("msg") @ApiParam("消息") String msg) {
        eventBusUtils.asyncEventPost(msg);
        return R.ok();
    }

    @ApiOperation(value = "回调")
    @OperatorLog(value = "回调",description = "学习")
    @RequestMapping(value = "/call", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public R call(@RequestParam("msg") @ApiParam("消息") String msg) {
        Client client = new Client(new Server());
        client.sendMsg(msg);
        return R.ok();
    }


    @ApiOperation(value = "base64解码")
    @RequestMapping(value = "/base64",method = RequestMethod.POST,produces = "application/json;charset=utf-8")
    public R base64Decode(@RequestParam("param") @ApiParam("参数") String param){
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
    public R idGenerator(@RequestParam(value = "bizTypePrefix") @ApiParam("前缀") String bizTypePrefix,@RequestParam(value = "dateFormat",defaultValue = "yyMMdd",required = false) @ApiParam("时间格式") String dateFormat){
        SnowIdGenerator idGenerator = new SnowIdGenerator(bizTypePrefix, dateFormat);
        return R.ok().data("id",idGenerator.generate());
    }


    @ApiOperation(value = "责任链模式")
    @OperatorLog(value = "责任链模式",description = "学习")
    @RequestMapping(value = "/filterChain", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public R filterChain(){
        Operator operator = (Operator) ContextUtil.getHttpRequest().getSession().getAttribute(LOGIN_USER_SESSION_KEY);
        DemoGzwFilterStage demoGzwFilterStage = new DemoGzwFilterStage();
        demoGzwFilterStage.setOperator(operator);
        DemoGzwFilterStage stage = demoGzwFilterChain.doFilter(demoGzwFilterStage);
        List<Map<String, Object>> result = stage.getStageHandlerResult();
        log.info("chain end {}", JSON.toJSONString(result));
        return R.ok().data("result",result);
    }

    @ApiOperation(value = "pdf上传加密")
    @OperatorLog(value = "pdf上传加密",description = "学习")
    @RequestMapping(value = "/uploadFileEcrypt", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public R uploadFileEcrypt(@RequestParam("file") @ApiParam("文件") MultipartFile file,@RequestParam(value = "password",required = false) @ApiParam("密码") String password) throws IOException {
        Operator operator = (Operator) ContextUtil.getHttpRequest().getSession().getAttribute(LOGIN_USER_SESSION_KEY);
        password = password == null ? operator.getPassword() : password;
        fileUploadUtil.encrypt(file, password);
        return R.ok();
    }

    @ApiOperation(value = "获取配置")
    @Resubmit(limit = 3)
    @OperatorLog(value = "获取服务配置",description = "学习")
    @RequestMapping(value = "/configInfo", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public R configInfo() {
        return R.ok().data("config",JSONObject.parse(serviceConfigInit.get()));
    }
}
