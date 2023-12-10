package com.gzw.kd.controller;


import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.gzw.kd.common.R;
import com.gzw.kd.common.annotation.Resubmit;
import com.gzw.kd.common.entity.*;
import com.gzw.kd.common.enums.EventTypeEnum;
import com.gzw.kd.common.enums.MenuTypeEnum;
import com.gzw.kd.common.enums.TemplateRoleEnum;
import com.gzw.kd.common.utils.ApplicationContextUtils;
import com.gzw.kd.listener.event.MsgEvent;
import com.gzw.kd.service.DocService;
import com.gzw.kd.service.WxUserService;
import com.gzw.kd.vo.input.RequestVo;
import io.swagger.annotations.Api;
import java.io.IOException;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;
import static com.gzw.kd.common.Constants.*;

/**
 * @author 高志伟
 */
@Api(tags = "微信公众号")
@Slf4j
@SuppressWarnings("all")
@RestController
@RequestMapping("/wx")
public class WechatPublicAccountController {



    @Value("${wx.mp.token:Abcd123$}")
    private String token;

    @Value("${wx.mp.appId:gzw01234}")
    private String appid;

    @Value("${wx.mp.secret:gzw01234}")
    private String secret;

    @Resource
    private WxUserService wxUserService;

    @Resource
    DocService m_docService;

    @Resource
    private RedisTemplate<String,String> redisTemplate;

    @GetMapping("/validate")
    @ResponseBody
    public String validate(String signature,String timestamp,String nonce,String echostr){
        String[] arr = {timestamp, nonce, token};
        Arrays.sort(arr);
        StringBuilder sb = new StringBuilder();
        for (String temp : arr) {
            sb.append(temp);
        }
        String sha1 = SecureUtil.sha1(sb.toString());
        if (sha1.equals(signature)){
            return echostr;
        }
        return null;
    }


    @PostMapping(value = "/validate", produces = "application/xml;charset=UTF-8")
    public Object handlerMessage(@RequestBody InMessage inMessage) throws Exception {
        OutMessage outMessage = new OutMessage();
        outMessage.setFromUserName(inMessage.getToUserName());
        outMessage.setToUserName(inMessage.getFromUserName());
        outMessage.setMsgType(inMessage.getMsgType());
        outMessage.setCreateTime(System.currentTimeMillis() / 1000);
        String msgType = inMessage.getMsgType();
        if ("text".equals(msgType)) {
            String inContent = inMessage.getContent();
            if (inContent.contains("万岁")) {
                outMessage.setContent("爱情万岁！！！！！！！");
            } else {
                outMessage.setContent(inContent);
            }
        } else if ("image".equals(msgType)) {
            outMessage.setMediaId(new String[]{inMessage.getMediaId()});
        } else if ("event".equals(msgType)) {
            String event = inMessage.getEvent();
            if (EventTypeEnum.SUB.getCode().equals(event)) {
                outMessage.setMsgType("text");
                WxUserInfo user = wxUserService.getUserByOpenId(inMessage.getFromUserName());
                if (ObjectUtil.isEmpty(user)) {
                    WxUserInfo wxUserInfo = wxUserinfoByOpenId(inMessage.getFromUserName());
                    if (ObjectUtil.isNotEmpty(wxUserInfo.getTagid_list())) {
                        wxUserInfo.setTagIdlist(wxUserInfo.getTagid_list().toString());
                    }
                    wxUserService.registerUser(wxUserInfo);
                    MsgEvent msgEvent = new MsgEvent().setEvent(" 公众号关注").setUserName(wxUserInfo.getNickname()).setStatus(TemplateRoleEnum.GUAN_ZHU.getStatus()).setOpenId(wxUserInfo.getOpenid());
                    ApplicationContext context = ApplicationContextUtils.getApplicationContext();
                    context.publishEvent(msgEvent);
                } else {
                    outMessage.setContent("欢迎您回来");
                    WxUserInfo info = new WxUserInfo().setOpenid(inMessage.getFromUserName()).setSubscribe(1);
                    wxUserService.updatePhoneByOpenId(info);
                }

            } else if (EventTypeEnum.TEMPLATE.getCode().equals(event)) {
                m_docService.updateResultForMsgId(inMessage.getTemplateMsgId().toString(), inMessage.getStatus());
            } else if (EventTypeEnum.UN_SUB.getCode().equals(event)) {
                WxUserInfo info = new WxUserInfo().setOpenid(inMessage.getFromUserName()).setSubscribe(0);
                wxUserService.updatePhoneByOpenId(info);
            }
            outMessage.setMediaId(new String[]{inMessage.getMediaId()});
        }
        return outMessage;
    }


    @GetMapping("/getAccessToken")
    public String getAccessToken(){
        Map<String, Object> data = new HashMap<>();
        data.put(WX_APP_ID,appid);
        data.put(WX_APP_SECRET,secret);
        data.put("grant_type","client_credential");
        data.put("force_refresh",true);
        String result = HttpUtil.post(WX_ACCESS_TOKEN_URL,data);
        JSONObject jsonObject = JSONUtil.parseObj(result);
        String accessToken = jsonObject.getStr(WX_APP_ACCESS_TOKEN);
        log.warn("access_token... .url.....{} data {},result {}",WX_ACCESS_TOKEN_URL,data,result);
        if (StringUtils.isNotBlank(accessToken)){
            redisTemplate.setValueSerializer(new StringRedisSerializer());
            redisTemplate.opsForValue().set(WX_APP_ACCESS_TOKEN, accessToken);
            redisTemplate.expire(WX_APP_ACCESS_TOKEN,WX_APP_EXPIRE_IN, TimeUnit.SECONDS);
        }
        return accessToken;
    }


    @Resubmit
    @PostMapping("/createMenu")
    public R createMenu(@Validated @RequestBody Menu menu) {
        String body = null;
        String url = checkToken(WX_CREATE_MENU_URL);
        if (menu.getType().equals(MenuTypeEnum.CLICK.getName())) {
            switch (menu.getMenuLevel()) {
                case INT_ONE:
                    body = "{\n" + "\"button\":[\n" + "{\t\n" + "\"type\":\"" + menu.getType() + "\",\n" + "\"name\":\"+" + menu.getName() + "+\",\n" + "\"key\":\"" + menu.getKey() + "\"\n" + "}]}";
                    break;
                case INT_TWO:
                    body = "{\"button\":[{\"name\":\"" + menu.getUpMenuName() + "\",\"sub_button\":[{\"type\":\"" + menu.getType() + "\",\"name\":\"" + menu.getName() + "\",\"key\":\"" + menu.getKey() + "\"}]}]}";
            }
        } else {
            switch (menu.getMenuLevel()) {
                case INT_ONE:
                    body = "{\n" + "\"button\":[\n" + "{\t\n" + "\"type\":\"" + menu.getType() + "\",\n" + "\"name\":\"+" + menu.getName() + "+\",\n" + "\"url\":\"" + menu.getUrl() + "?" + WX_APP_LOGIN_KEY + "=" + WX_APP_LOGIN_FLAG + "\"\n" + "}]}";
                    break;
                case INT_TWO:
                    body = "{\"button\":[{\"name\":\"" + menu.getUpMenuName() + "\",\"sub_button\":[{\"type\":\"" + menu.getType() + "\",\"name\":\"" + menu.getName() + "\",\"url\":\"" + menu.getUrl() + "?" + WX_APP_LOGIN_KEY + "=" + WX_APP_LOGIN_FLAG + "\"}]}]}";
            }
        }
        String result = HttpUtil.post(url, body);
        WxErrorCode codes = com.alibaba.fastjson.JSONObject.parseObject(result, WxErrorCode.class);
        if (codes.getErrcode() == INT_ZERO) {
            return R.ok();
        }
        return R.error().message(codes.getErrmsg());
    }

    @Resubmit
    @PostMapping("/queryMenu")
    public R queryMenu(@RequestBody Menu menu) {
        String body = null;
        String url = checkToken(WX_QUERY_MENU_URL);
        if(menu.getType().equals(MenuTypeEnum.CLICK.getName())){
            switch (menu.getMenuLevel()){
                case INT_ONE:
                    body ="{\"is_menu_open\":1,\"selfmenu_info\":{\"button\":[{\"type\":\"click\",\"name\":\""+menu.getName()+"\",\"key\":\""+menu.getKey()+"\"}]}}";
                    break;
                case INT_TWO:
                    body = "{\"is_menu_open\":1,\"selfmenu_info\":{\"button\":[{\"name\":\""+menu.getUpMenuName()+"\",\"sub_button\":{\"list\":[{\"type\":\"view\",\"name\":\""+menu.getName()+"\",\"key\":\""+menu.getKey()+"\"}]}}]}}";
            }
        } else {
            switch (menu.getMenuLevel()){
                case INT_ONE:
                    body = "{\"is_menu_open\":1,\"selfmenu_info\":{\"button\":[{\"type\":\"click\",\"name\":\""+menu.getName()+"\",\"url\":\""+menu.getUrl()+"?"+WX_APP_LOGIN_KEY+"="+WX_APP_LOGIN_FLAG+"\"}]}}";
                    break;
                case INT_TWO:
                    body = "{\"button\":[{\"name\":\""+menu.getUpMenuName()+"\",\"sub_button\":[{\"type\":\""+menu.getType()+"\",\"name\":\""+menu.getName()+"\",\"url\":\""+menu.getUrl()+"?"+WX_APP_LOGIN_KEY+"="+WX_APP_LOGIN_FLAG+"\"}]}]}";
            }
        }
        String result = HttpUtil.post(url, body);
        WxErrorCode codes = com.alibaba.fastjson.JSONObject.parseObject(result, WxErrorCode.class);
        if(codes.getErrcode() == INT_ZERO){
            Map<String, Object> map = new HashMap<>();
            map.put("result",result);
            return R.ok().data(map);
        }
        return R.error().message(codes.getErrmsg());
    }

    @PostMapping("/createMultiMenu")
    @Resubmit
    public R createMultiMenu(@RequestBody RequestVo requestVo) {
        log.info("开始创建多个菜单----------{}", JSONUtil.toJsonStr(requestVo));
        StringBuffer body = new StringBuffer();
        body.append("{\"button\":[");
        String TwoLevel = "";
        String url = checkToken(WX_CREATE_MENU_URL);
        List<Menu> menuList = requestVo.getMenuList();
        for (Menu menu : menuList) {
            if (StringUtils.isNotBlank(menu.getType()) && menu.getType().equals(MenuTypeEnum.CLICK.getName())) {
                switch (menu.getMenuLevel()) {
                    case INT_ONE:
                        body.append("{" + "\"type\"" + ":\"" + menu.getType() + "\"," + "\"name\"" + ":\"" + menu.getName() + "\"," + "\"key\"" + ":\"" + menu.getKey() + "\"},");
                        break;
                    case INT_TWO:
                        List<Menu> list = menu.getTwoMenuList();
                        body.append("{" + "\"name\"" + ":\"" + menu.getName() + "\",\"sub_button\":[");
                        for (Menu two : list) {
                            if (two.getType().equals(MenuTypeEnum.CLICK.getName())) {
                                body.append("{" + "\"type\"" + ":\"" + two.getType() + "\"," + "\"name\"" + ":\"" + two.getName() + "\"," + "\"key\"" + ":\"" + two.getKey() + "\"},");
                            } else {
                                body.append("{" + "\"type\"" + ":\"" + two.getType() + "\"," + "\"name\"" + ":\"" + two.getName() + "\"," + "\"url\"" + ":\"" + two.getUrl() + "\"},");
                            }
                        }
                        break;
                }
            } else {
                switch (menu.getMenuLevel()) {
                    case INT_ONE:
                        body.append("{" + "\"type\"" + ":\"" + menu.getType() + "\"," + "\"name\"" + ":\"" + menu.getName() + "\"," + "\"url\"" + ":\"" + menu.getUrl() + "\"},");
                        break;
                    case INT_TWO:
                        List<Menu> list = menu.getTwoMenuList();
                        body.append("{" + "\"name\"" + ":\"" + menu.getName() + "\",\"sub_button\":[");
                        for (Menu two : list) {
                            if (two.getType().equals(MenuTypeEnum.CLICK.getName())) {
                                body.append("{" + "\"type\"" + ":\"" + two.getType() + "\"," + "\"name\"" + ":\"" + two.getName() + "\"," + "\"key\"" + ":\"" + two.getKey() + "\"},");
                            } else {
                                body.append("{" + "\"type\"" + ":\"" + two.getType() + "\"," + "\"name\"" + ":\"" + two.getName() + "\"," + "\"url\"" + ":\"" + two.getUrl() + "\"},");
                            }
                        }
                        break;
                }
            }
        }
        String bodys = body.toString();
        String bodyes = bodys.substring(0, bodys.length() - 1) + "]}]}";
        log.info("---------组合完毕------------------" + bodyes);
        String result = HttpUtil.post(url, bodyes);
        WxErrorCode codes = com.alibaba.fastjson.JSONObject.parseObject(result, WxErrorCode.class);
        if(codes.getErrcode() == INT_ZERO){
            return R.ok();
        }
        return R.error().message(codes.getErrmsg());
    }


    @Resubmit
    @PostMapping("/deleteMenu")
    public R deleteMenu(@Validated @RequestBody Menu menu) {
        String body = null;
        String url = checkToken(WX_DELETE_MENU_URL);
        if(menu.getType().equals(MenuTypeEnum.CLICK.getName())){
            switch (menu.getMenuLevel()){
                case INT_ONE:
                    body = "{\n" + "\"button\":[\n" + "{\t\n" + "\"type\":\""+menu.getType()+"\",\n" + "\"name\":\"+"+menu.getName()+"+\",\n" + "\"key\":\""+menu.getKey()+"\"\n" + "}]}";
                    break;
                case INT_TWO:
                    body = "{\"button\":[{\"name\":\""+menu.getUpMenuName()+"\",\"sub_button\":[{\"type\":\""+menu.getType()+"\",\"name\":\""+menu.getName()+"\",\"key\":\""+menu.getKey()+"\"}]}]}";
            }
        } else {
            switch (menu.getMenuLevel()){
                case INT_ONE:
                    body = "{\n" + "\"button\":[\n" + "{\t\n" + "\"type\":\""+menu.getType()+"\",\n" + "\"name\":\"+"+menu.getName()+"+\",\n" + "\"url\":\""+menu.getUrl()+"?"+WX_APP_LOGIN_KEY+"="+WX_APP_LOGIN_FLAG+"\"\n" + "}]}";
                    break;
                case INT_TWO:
                    body = "{\"button\":[{\"name\":\""+menu.getUpMenuName()+"\",\"sub_button\":[{\"type\":\""+menu.getType()+"\",\"name\":\""+menu.getName()+"\",\"url\":\""+menu.getUrl()+"?"+WX_APP_LOGIN_KEY+"="+WX_APP_LOGIN_FLAG+"\"}]}]}";
            }
        }
        String result = HttpUtil.post(url, body);
        WxErrorCode codes = com.alibaba.fastjson.JSONObject.parseObject(result, WxErrorCode.class);
        if(codes.getErrcode() == INT_ZERO){
            return R.ok();
        }
        return R.error().message(codes.getErrmsg());
    }


    @Resubmit
    @RequestMapping(value = "/addCustomerAccount",method = RequestMethod.POST)
    private R addCustomerAccount(@RequestBody KfService kfService){
        String url = checkToken(WX_CUSTOMER_SERVICE_URL);
        String result = HttpUtil.post(url, kfService.toString());
       WxErrorCode codes = com.alibaba.fastjson.JSONObject.parseObject(result, WxErrorCode.class);
        if(codes.getErrcode() == INT_ZERO){
            return R.ok();
        }
        return R.error().message(codes.getErrmsg());
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


    @Resubmit(limit = 10, enable = true)
    @RequestMapping(value = "/addBlack", method = RequestMethod.POST)
    public R addBlackList(@RequestBody String blackList) {
        String url = checkToken(WX_ADD_BLACK_LIST_URL);
        if (StringUtils.isNotBlank(blackList)) {
            String result = HttpUtil.post(url, blackList);
            WxErrorCode codes = com.alibaba.fastjson.JSONObject.parseObject(result, WxErrorCode.class);
            if (codes.getErrcode() != INT_ZERO) {
                return R.error().message(codes.getErrmsg());
            }
        }
        return R.ok();
    }
    public WxUserInfo wxUserinfoByOpenId(String openId) throws IOException {
        System.out.println("===========WxUserinfoByOpenId");
        String url = checkToken(WX_USER_URL);
        String infoUrl = url+"&openid=" + openId +"&lang=zh_CN";
        String result = HttpUtil.get(infoUrl);
        log.info("wx user info {}",result);
        WxUserInfo codes = com.alibaba.fastjson.JSONObject.parseObject(result, WxUserInfo.class);
        return codes;
    }
}
