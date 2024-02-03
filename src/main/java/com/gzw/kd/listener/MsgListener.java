package com.gzw.kd.listener;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.gzw.kd.common.R;
import com.gzw.kd.common.entity.TemplateDataStyle;
import com.gzw.kd.common.enums.OnlineStatusEnum;
import com.gzw.kd.common.exception.Asserts;
import com.gzw.kd.common.utils.AESCrypt;
import com.gzw.kd.common.entity.WeChatTemplateMsg;
import com.gzw.kd.common.entity.WxErrorCode;
import com.gzw.kd.common.entity.WxUserInfo;
import com.gzw.kd.listener.event.MsgEvent;
import com.gzw.kd.service.TemplateService;
import com.gzw.kd.service.WxUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import static com.gzw.kd.common.Constants.*;

/**
 * @author 高志伟
 */

@SuppressWarnings("all")
@Slf4j
@Component
public class MsgListener {


    @Value("${wx.mp.token:Abcd123$}")
    private String token;

    @Value("${wx.mp.appId:gzw01234}")
    private String appid;

    @Value("${wx.mp.secret:gzw01234}")
    private String secret;

    @Value("${natAppUrl}")
    private String natAppUrl;

    @Resource
    private TemplateService templateService;

    @Resource
    private WxUserService wxUserService;

    @Resource
    private RedisTemplate<String, String> redisTemplate;


    @EventListener(MsgEvent.class)
    public void sendMsg(MsgEvent event) throws Exception {
        int onlineNum = MysessionListener.sessionContext.getSessionMap().size();
        if (event.getStatus() == OnlineStatusEnum.OFF_LINE.getStatus()) {
            log.info("用户:{} {},时间:{} 在线人数:{}", event.getUserName(), event.getEvent(), event.getOnLineTime() + "-" + event.getOffLineTime(), onlineNum - 1);
        } else if (event.getStatus() == OnlineStatusEnum.ON_LINE.getStatus()) {
            log.info("用户:{} {},时间:{} 在线人数:{}", event.getUserName(), event.getEvent(), event.getOnLineTime(), onlineNum);
        } else {
            String name = event.getUserName();
            WxUserInfo user = wxUserService.getUserByName(AESCrypt.encrypt(name));
            if (user.getSubscribe() == INT_ZERO) {
                log.error("用户 {} 已经取消了关注 不能发送", name);
                Asserts.fail(String.format("用户 %s 已经取消了关注 不能发送", name));
                return;
            }
            WeChatTemplateMsg templateMsg = templateService.getTemplateByName(event.getEvent().substring(event.getEvent().indexOf(STRING_UNDERLINE) + 1));
            try {
                String dataFormat = templateMsg.getDataFormat();
                JSONArray jsonArray = JSONUtil.parseArray(dataFormat);
                Map<String, TemplateDataStyle> hashMap = new HashMap<>();
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    Map<String, String> map = com.alibaba.fastjson2.JSONObject.parseObject(object.toString(), Map.class);
                    for (Map.Entry<String, String> data1 : map.entrySet()) {
                        String value = event.getData().get(data1.getValue());
                        TemplateDataStyle style = new TemplateDataStyle(value);
                        hashMap.put(data1.getKey(), style);
                    }
                }
                templateMsg.setData(hashMap).setToUser(user.getOpenid());
                R msg = sendModelMsg(templateMsg, event.getId());
                if (msg.getSuccess()) {
                    log.info("用户 {} {}消息发送成功 请查收 时间:{}", name, event.getEvent(), LocalDateTime.now());
                } else {
                    Asserts.fail(String.format("用户 %s %s 消息发送失败 请查收 时间:%s message %s", name, event.getEvent(), LocalDateTime.now(), msg.getMessage()));
                    log.error("用户 {} {} 消息发送失败 请查收 时间:{} message {}", name, event.getEvent(), LocalDateTime.now(), msg.getMessage());
                }
            } catch (Exception e) {
                Asserts.fail(String.format("用户%s %s消息发送异常 时间:%s %s", name, event.getEvent(), LocalDateTime.now(), e));
                log.error("用户{} {}消息发送异常 时间:{}", name, event.getEvent(), LocalDateTime.now(), e);
            }
        }
    }


    @Transactional
    public R sendModelMsg(WeChatTemplateMsg weChatTemplateMsg, Integer id) {
        String url = checkToken(WX_MODEL_MSG_URL);
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("template_id", weChatTemplateMsg.getTemplateId());
        paramMap.put("touser", weChatTemplateMsg.getToUser());
        paramMap.put("url", natAppUrl + weChatTemplateMsg.getUrl() + id + "&openId=" + weChatTemplateMsg.getToUser());
        paramMap.put("topcolor", weChatTemplateMsg.getColor());
        paramMap.put("data", weChatTemplateMsg.getData());
        String result = HttpUtil.post(url, JSONUtil.toJsonStr(paramMap));
        WxErrorCode codes = com.alibaba.fastjson.JSONObject.parseObject(result, WxErrorCode.class);
        log.warn("message {} msgId {}", codes.getErrmsg(), codes.getMsgid());
        if (codes.getErrcode() != INT_ZERO) {
            return R.error().message(codes.getErrmsg());
        }
        return R.ok();
    }


    public String checkToken(String url) {
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        String accessToken = redisTemplate.opsForValue().get(WX_APP_ACCESS_TOKEN);
        String body = null;
        if (StringUtils.isBlank(accessToken)) {
            accessToken = getAccessToken();
        }
        url = url + accessToken;
        return url;
    }

    public String getAccessToken() {
        Map<String, Object> data = new HashMap<>();
        data.put(WX_APP_ID,appid);
        data.put(WX_APP_SECRET,secret);
        data.put("grant_type","client_credential");
        String result = HttpUtil.post(WX_ACCESS_TOKEN_URL,JSONUtil.toJsonStr(data));
        JSONObject jsonObject = JSONUtil.parseObj(result);
        String accessToken = jsonObject.getStr(WX_APP_ACCESS_TOKEN);
        if (StringUtils.isNotBlank(accessToken)) {
            redisTemplate.opsForValue().set(WX_APP_ACCESS_TOKEN, accessToken);
            redisTemplate.expire(WX_APP_ACCESS_TOKEN, WX_APP_EXPIRE_IN, TimeUnit.SECONDS);
        }
        return accessToken;
    }
}
