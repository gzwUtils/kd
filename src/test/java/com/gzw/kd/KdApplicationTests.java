package com.gzw.kd;
import cn.hutool.core.codec.Base64;
import com.alibaba.fastjson.JSON;
import com.gzw.kd.common.entity.Operator;
import com.gzw.kd.common.exception.GlobalException;
import com.gzw.kd.common.generators.RandomIdGenerator;
import com.gzw.kd.common.utils.MyLinkedBlockQueue;
import com.gzw.kd.common.utils.SM4Utils;
import com.gzw.kd.learn.model.filter.DemoGzwFilterChain;
import com.gzw.kd.learn.model.filter.DemoGzwFilterStage;
import com.gzw.kd.learn.model.model.GzwThreadDemo;
import com.gzw.kd.scheduletask.ScheduleTask;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("all")
@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class KdApplicationTests {

    @Autowired
    private ScheduleTask scheduleTask;

    @Autowired
    private DemoGzwFilterChain demoGzwFilterChain;

    @Autowired
    private GzwThreadDemo gzwThreadDemo;

    @Test
    void contextLoads() throws GlobalException {
        RandomIdGenerator randomIdGenerator = new RandomIdGenerator();
        String generate = randomIdGenerator.generate(8);
        log.info("unique id ...{}", generate);
    }

    @Test
    void match() throws GlobalException {
        String regex = "^\\s*|^[0-9]{11}$";
        Pattern p = Pattern.compile(regex);
        Matcher matcher = p.matcher("abcd");
        log.info("regx pattern  ...is success {}", matcher.matches());
    }

    @Test
    void scheduleTask() {
        log.info("new cron ");
        scheduleTask.setCron("0/5 * * * * ?");
    }


    @Test
    void base64() {
        String code = Base64.decodeStr("cGljb0NURns1M3J2M3JfNTNydjNyXzUzcnYzcl81M3J2M3JfNTNydjNyfQ");
        System.out.println(code+"=================================================");
        System.out.println(new Date().getHours());
    }

    @Test
    void put() {
        MyLinkedBlockQueue<Integer> linkedBlockQueue = new MyLinkedBlockQueue<>();
        log.info("---------------------------------");
        new Thread(() -> {
            for (int i = 1; i <= 100; i++) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    log.error("test put method Interrupt", e);
                }
                linkedBlockQueue.put(i);
                log.info("生产者生产了：{}", i);
            }
        }, "put").start();
    }

    @Test
    void smc() throws Exception {
        String password = SM4Utils.encryptData_CBC("abdc1234", SM4Utils.DEFAULT_KEY, SM4Utils.DEFAULT_IV, false, SM4Utils.CIPHER_TEXT_BASE64);
        String plainPassword = SM4Utils.decryptData_CBC(password, SM4Utils.DEFAULT_KEY, SM4Utils.DEFAULT_IV, false, SM4Utils.CIPHER_TEXT_BASE64);
        log.info(password + "-----------------------------------encrryPass---------------------------------------------------------------------");
        log.info(plainPassword + "------------------------------plainPassword-------------------------------------------------------------------------");
    }

    @Test
    void chain() throws Exception {
        DemoGzwFilterStage demoGzwFilterStage = new DemoGzwFilterStage();
        Operator operator = new Operator();
        operator.setAccount("admin").setIsAdmin(1);
        demoGzwFilterStage.setOperator(operator);
        DemoGzwFilterStage stage = demoGzwFilterChain.doFilter(demoGzwFilterStage);
        List<Map<String, Object>> result = stage.getStageHandlerResult();
        log.info("chain end {}", JSON.toJSONString(result));
    }


    @Test
    void gzwThreadDemo() {
       gzwThreadDemo.start();
    }
}
