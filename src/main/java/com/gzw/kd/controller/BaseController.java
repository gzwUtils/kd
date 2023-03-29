package com.gzw.kd.controller;
import com.alibaba.fastjson2.JSONObject;
import com.gzw.kd.common.R;
import com.gzw.kd.common.annotation.OperatorLog;
import com.gzw.kd.common.utils.MyLinkedBlockQueue;
import com.gzw.kd.common.entity.MyQueue;
import com.gzw.kd.common.utils.RedisLimitFlow;
import com.gzw.kd.learn.model.model.GzwThreadDemo;
import io.swagger.annotations.Api;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import static com.gzw.kd.common.Constants.*;

/**
 * @author gzw
 * @version 1.0
 * @Date 2022/10/20
 * @dec
 */
@Slf4j
@Api(value = "base")
@RequestMapping("/base")
@RestController
@SuppressWarnings("all")
public class BaseController {

    @Resource
    private MyLinkedBlockQueue<Object> linkedBlockQueue;


    @Autowired
    private GzwThreadDemo gzwThreadDemo;


    private static List<String> put_list = new ArrayList<>();

    private static List<String> take_list = new ArrayList<>();


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
                log.info("生产者生产了：{} {}", i, JSONObject.toJSONString(myQueue.getData().get(i)));
                put_list.add("生产者:"+myQueue.getData().get(i));
            }
        }, "put").start();
        return R.ok().data("pushs", String.join(",",put_list));
    }


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
                log.info("消费者消费了：{} {}", i,JSONObject.toJSONString(take));
                take_list.add("消费者"+JSONObject.toJSONString(take));
            }
        }, "take").start();
        return R.ok().data("takes",String.join(",",take_list));
    }


    @RequestMapping(value = "/clear",method = RequestMethod.POST,produces = "application/json;charset=utf-8")
    public  R clear (){
        put_list.clear();
        take_list.clear();
        return R.ok();
    }

    @OperatorLog(value = "redis limit flow ",description = "redis 限流")
    @RequestMapping(value = "/limitFlow",method = RequestMethod.POST,produces = "application/json;charset=utf-8")
    public  R limitFlow (){
        return RedisLimitFlow.limitFlow(60000l,5);
    }


    @RequestMapping(value = "/gzwThreadDemo",method = RequestMethod.POST,produces = "application/json;charset=utf-8")
    public  R start (){
        gzwThreadDemo.start();
        return R.ok();
    }

    @RequestMapping(value = "/shutdown", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public R shutdown() {
        gzwThreadDemo.shutdown();
        return R.ok();
    }
}
