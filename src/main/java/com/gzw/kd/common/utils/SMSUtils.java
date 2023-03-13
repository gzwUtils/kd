package com.gzw.kd.common.utils;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSONObject;
import com.gzw.kd.common.R;
import com.gzw.kd.common.entity.SmsResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import static com.gzw.kd.common.Constants.SMS_SEND_URL;
import static com.gzw.kd.common.Constants.STRING_TWO;

/**
 * @author 高志伟 短信验证码发送
 */
@Component
@SuppressWarnings("all")
public class SMSUtils {

    @Value("${smsAccount}")
    private String account;

    @Value("${smsPassword}")
    private String smsPassword;


    public  R send(String phone, String numbers, HttpSession httpSession) throws IOException {
        Map<String, Object> map = new HashMap<>();
        map.put("account",account);
        map.put("password",smsPassword);
        map.put("mobile",phone);
        map.put("content","您的验证码是："+numbers+"。请不要把验证码泄露给其他人。");
        map.put("format","json");
        String result = HttpUtil.post(SMS_SEND_URL, map);
        SmsResult smsResult = JSONObject.parseObject(result, SmsResult.class);
        if(!smsResult.getCode().equals(STRING_TWO)){
            return R.error().message(smsResult.getMsg());
        }
        httpSession.setAttribute("smsCode",numbers);
        httpSession.setMaxInactiveInterval(60);
        return R.ok();
    }


    public  R sendMessage(String phone, String message) throws IOException {
        Map<String, Object> map = new HashMap<>();
        map.put("account",account);
        map.put("password",smsPassword);
        map.put("mobile",phone);
        map.put("content","亲爱的"+message+"，虽然经历了岁月的洗礼，但真挚的感情没有磨灭。愿我们友谊长存。");
        map.put("format","json");
        String result = HttpUtil.post(SMS_SEND_URL, map);
        SmsResult smsResult = JSONObject.parseObject(result, SmsResult.class);
        if(!smsResult.getCode().equals(STRING_TWO)){
            return R.error().message(smsResult.getMsg());
        }
        return R.ok();
    }
}
