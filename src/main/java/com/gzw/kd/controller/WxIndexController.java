package com.gzw.kd.controller;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import static com.gzw.kd.common.Constants.*;
import com.gzw.kd.common.R;
import com.gzw.kd.common.entity.*;
import com.gzw.kd.common.enums.StatusEnum;
import com.gzw.kd.common.generators.RandomIdGenerator;
import com.gzw.kd.common.utils.AESCrypt;
import com.gzw.kd.common.utils.AliPayUtils;
import com.gzw.kd.common.utils.ApplicationContextUtils;
import com.gzw.kd.listener.event.MsgEvent;
import com.gzw.kd.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.gzw.kd.vo.input.AssignInput;
import com.gzw.kd.vo.input.WxUserInput;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author gzw
 * @description：
 * @since：2022/11/1 02:16
 */

@Api(tags = "微信")
@Slf4j
@SuppressWarnings("all")
@Controller
@RequestMapping("/wx")
public class WxIndexController {


    @Resource
    private RedisTemplate<String,String> redisTemplate;


    @Resource
    DocService m_docService;

    @Resource
    private WxUserService wxUserService;

    @Resource
    private OrderService orderService;


    @Resource
    private CustomerService customerService;


    @Resource
    RandomIdGenerator randomIdGenerator;


    @Resource
    TemplateService templateService;


    @Resource
    private AliPayUtils aliPayUtils;

    @Value("${wx.mp.appId:gzw01234}")
    private String appid;

    @Value("${wx.mp.secret:gzw01234}")
    private String secret;

    @Value("${natAppUrl}")
    private String natAppUrl;


    @ApiOperation(value = "注册")
    @RequestMapping(value = "/register",method = RequestMethod.GET)
    public String register(HttpServletRequest request) {
        String openId = request.getParameter("openId");
        request.setAttribute("openId", openId);
        return "/wx/register";
    }


    @ApiOperation(value = "支付宝二维码")
    @RequestMapping(value = "/zfbQrCode",method = RequestMethod.GET)
    public String zfbQrCode(HttpServletRequest request) {
        return "/zfb/zfbQrCode";
    }

    @ApiOperation(value = "首页")
    @RequestMapping(value = "/index",method = RequestMethod.GET)
    public String index(HttpServletRequest request) {
        return "/wx/index";
    }


    @ApiOperation(value = "详情")
    @RequestMapping(value = "/xq",method = RequestMethod.GET)
    public String xq(HttpServletRequest request) throws Exception {
        String id = request.getParameter("id");
        String openId = request.getParameter("openId");
        Doc docById = m_docService.getDocById(Integer.parseInt(id));
        docById.setAppointDates(docById.getAppointDate().format(DATE_TIME_FORMAT_S));
        request.setAttribute("docById",docById);
        request.setAttribute("openId", openId);
        return "/wx/xq";
    }

    @ApiOperation(value = "账户")
    @RequestMapping(value = "/account",method = RequestMethod.GET)
    public String account(HttpServletRequest request) throws Exception {
        //TODO
        String openId = request.getParameter("openId");
        List<Doc> docs = new ArrayList<>();
        WxUserInfo userInfo = wxUserService.getUserByOpenId(openId);
        request.setAttribute("openId", openId);
        request.setAttribute("userInfo", userInfo);
        return "/wx/account";
    }

    @ApiOperation(value = "签署")
    @RequestMapping(value = "/assign",method = RequestMethod.GET)
    public String assign(HttpServletRequest request) throws Exception {
        Assign assign = new Assign();
        String openId = request.getParameter("openId");
        String redisAssign = redisTemplate.opsForValue().get(ASSIGN_INFO_KEY_+openId);
        if(StringUtils.isBlank(redisAssign)){
            assign = customerService.getUserByOpenId(openId);
        } else {
            redisAssign = StringEscapeUtils.unescapeJava(redisAssign);
             assign = com.alibaba.fastjson.JSONObject.parseObject(redisAssign, Assign.class);
        }
        if(ObjectUtil.isNotEmpty(assign)){
            if(assign.getStatus()==INT_ZERO){
                ZFBFaceToFaceModel model = new ZFBFaceToFaceModel();
                String generate = randomIdGenerator.generate(16);
                model.setOutTradeNo(generate).setTotalAmount(assign.getServiceBalance()).setSubject("test").setBody("kd");
                R order = aliPayUtils.newAliOrder(model);
                if(!order.getSuccess()){
                    return "/error/500";
                } else {
                    request.setAttribute("code",order.getData().get("code"));
                    return "/zfb/zfbQrCode";
                }
            } else {
                request.setAttribute("contract", assign);
                log.info("开始生成合同PDF..................................................................");
                return "/wx/contract";
            }
        }
        request.setAttribute("openId", openId);
        return "/wx/assign";
    }

    @ApiOperation(value = "代办")
    @RequestMapping(value = "/daiban",method = RequestMethod.GET)
    public String daiban(HttpServletRequest request) throws Exception {
        String openId = request.getParameter("openId");
        List<Doc> docs = new ArrayList<>();
        WxUserInfo userInfo = wxUserService.getUserByOpenId(openId);
        if(ObjectUtil.isNotEmpty(userInfo)){
             docs = m_docService.getDocByNameAndStatus(AESCrypt.decrypt(userInfo.getName()),1);
            docs.forEach(p->{
                p.setAppointDates(p.getAppointDate().format(DATE_TIME_FORMAT_S));
            });
        }
        request.setAttribute("openId",openId);
        request.setAttribute("docs",docs);
        return "/wx/daiBanDocList";
    }

    @ApiOperation(value = "更新")
    @RequestMapping(value = "/updateStatus",method = RequestMethod.GET)
    @ResponseBody
    public R updateStatus(String status, String id, String openId) throws Exception {
        Doc doc = new Doc();
        int stat = Integer.parseInt(status) - 1;
        WxUserInfo user = wxUserService.getUserByOpenId(openId);
        if(ObjectUtil.isNotEmpty(user)){
            if (stat == StatusEnum.APPROVE.getStatus()) {
                doc.setAudit(AESCrypt.decrypt(user.getName()));
            } else if (stat == StatusEnum.REVIEW_DRIFT.getStatus()) {
                doc.setVerify(AESCrypt.decrypt(user.getName()));
            }
            doc.setStatus(stat);
            doc.setIssueDate(LocalDateTime.now());
            doc.setId(Integer.parseInt(id));
            int count = m_docService.updateStatusForDocById(doc);
            if (count != 0) {
                if (stat == StatusEnum.APPROVE.getStatus() || stat == StatusEnum.REVIEW_DRIFT.getStatus()) {
                    List<Doc> docs = m_docService.getDocByNameAndStatus(AESCrypt.decrypt(user.getName()), stat);
                    docs.forEach(p->{
                        daiban(p);
                    });
                } else {
                    Assign byOpenId = customerService.getUserByOpenId(openId);
                    String config = redisTemplate.opsForValue().get(SERVICE_COFIG);
                    Configs configs = JSON.parseObject(config, Configs.class);
                    byOpenId.setBalance(byOpenId.getBalance().subtract(configs.getAnnualBalance())).setSyNumber(byOpenId.getSyNumber()-1);
                    customerService.updateAssignByOpenId(byOpenId);
                }
                return R.ok();
            }
        }
        return R.error();
    }

    private void daiban(Doc doc) {
        ApplicationContext context = ApplicationContextUtils.getApplicationContext();
        Map<String, String> data = new HashMap<>();
        data.put("user","客户"+doc.getCustomerName());
        data.put("address",doc.getAddress());
        data.put("desc",doc.getDesc());
        data.put("issueDate",doc.getIssueDate().format(DATE_TIME_FORMAT_S));
        WeChatTemplateMsg template = templateService.getTemplateById(doc.getTempId());
        MsgEvent event = new MsgEvent().setEvent(doc.getTempSys()+STRING_UNDERLINE+template.getTemplateName()).setStatus(template.getRole()).setUserName(doc.getAppoint()).setData(data).setId(doc.getId()).setTemplateId(doc.getTempId());
        context.publishEvent(event);
    }

    @ResponseBody
    @RequestMapping(value = "/updateAction",method = RequestMethod.POST,produces = "application/json;charset=utf-8")
    public R updateAction(@RequestBody WxUserInput wxUserInput) throws Exception {
        if (StringUtils.isBlank(wxUserInput.getPhone()) || StringUtils.isBlank(wxUserInput.getOpenId()) || StringUtils.isBlank(wxUserInput.getName())) {
            return R.error();
        }
        String encryptName = AESCrypt.encrypt(wxUserInput.getName());
        String encryptPhone= AESCrypt.encrypt(wxUserInput.getPhone());
        Integer integer = wxUserService.updatePhone(wxUserInput.getOpenId(), encryptPhone,encryptName);
        return R.ok();
    }

    @RequestMapping(value = "/paycallback",method = RequestMethod.POST,produces = "application/json;charset=utf-8")
    public R paycallback(String openId) throws Exception {
        String redisAssign = redisTemplate.opsForValue().get(ASSIGN_INFO_KEY_+openId);
        String order = redisTemplate.opsForValue().get(ORDER_INFO_KEY_+openId);
        OrderInfo orderInfo = null;
        if(StringUtils.isNotBlank(redisAssign)){
            if(StringUtils.isNotBlank(order)){
                 orderInfo = JSON.parseObject(order, OrderInfo.class);
                 orderInfo.setStatus(1);
                Integer orderL = orderService.registerOrder(orderInfo);
                if(orderL == null){
                    return R.error().message("支付成功 订单写入异常.....请核查");
                }
            }
            Assign assign = JSON.parseObject(redisAssign, Assign.class);
            assign.setUpdateTime(LocalDateTime.now()).setCreateTime(LocalDateTime.now()).setBalance(orderInfo.getServiceBalance());
            Integer flag = customerService.registerUser(assign);
            if(flag == null){
                return R.error().message("支付成功 合同写入异常.....请核查");
            }
        }
        return R.ok();
    }

    @Transactional
    @ResponseBody
    @RequestMapping(value = "/updateAssignAction",method = RequestMethod.POST,produces = "application/json;charset=utf-8")
    public R updateAssignAction(@RequestBody AssignInput assignInput) throws Exception {
        if (StringUtils.isBlank(assignInput.getPhone()) || StringUtils.isBlank(assignInput.getOpenId()) || StringUtils.isBlank(assignInput.getCustomerName()) || StringUtils.isBlank(assignInput.getAddress())) {
            return R.error();
        }
        Assign assign = new Assign();
        String encryptName = AESCrypt.encrypt(assignInput.getCustomerName());
        String encryptPhone= AESCrypt.encrypt(assignInput.getPhone());
        wxUserService.updatePhone(assignInput.getOpenId(), encryptPhone,encryptName);
        //补充合同信息
        BeanUtils.copyProperties(assignInput,assign);
        assign.setCreateTime(LocalDateTime.now()).setCustomerName(encryptName).setPhone(encryptPhone);
        redisTemplate.opsForValue().set(ASSIGN_INFO_KEY_+assign.getOpenId(), JSON.toJSONString(assign),ASSIGN_INFO_EXPIRE_TIME,TimeUnit.SECONDS);
        //补充订单信息
        OrderInfo orderInfo =new OrderInfo();
        BeanUtils.copyProperties(assignInput,orderInfo);
        orderInfo.setStatus(INT_ZERO);
        String config = redisTemplate.opsForValue().get(SERVICE_COFIG);
        JSONObject object = JSON.parseObject(config);
        if(assignInput.getNumber()!=12){
            orderInfo.setServiceBalance( object.getBigDecimal("annualBalance").multiply(new BigDecimal(assignInput.getNumber())));
        } else {
            orderInfo.setServiceBalance( object.getBigDecimal("yearBalance"));
        }
        redisTemplate.opsForValue().set(ORDER_INFO_KEY_+assign.getOpenId(),JSON.toJSONString(orderInfo),ORDER_INFO_EXPIRE_TIME,TimeUnit.SECONDS);
        return R.ok();
    }


    @RequestMapping(value = "/login",method = RequestMethod.GET)
    public void wxAuth(HttpServletResponse response,HttpServletRequest request) throws IOException {
        String STATE = request.getParameter("index");
        String callBack = natAppUrl+"/wx/callBack?index="+STATE;
        //请求地址
        String url = "https://open.weixin.qq.com/connect/oauth2/authorize" +
                "?appid=" + appid +
                "&redirect_uri=" + URLEncoder.encode(callBack) +
                "&response_type=code" +
                "&scope=snsapi_userinfo" +
                "&state="+STATE+"#wechat_redirect";
        //重定向
        response.sendRedirect(url);
    }


    @RequestMapping(value = "/callBack",method = RequestMethod.GET)
    public void wxCallBack(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String STATE = request.getParameter("index");
        String accessToken = getAuthTokenAndOpenid(request);
        String[] split = accessToken.split("#");
        //请求获取userInfo
        String infoUrl = "https://api.weixin.qq.com/sns/userinfo" +
                "?access_token=" + split[0] +
                "&openid=" + split[1] +
                "&lang=zh_CN";

        String resultInfo = HttpUtil.get(infoUrl);
        WxUserInfo wxUserInfo = com.alibaba.fastjson.JSONObject.parseObject(resultInfo, WxUserInfo.class);
        //去数据库查询此微信是否绑定过手机
        WxUserInfo user = wxUserService.getUserByOpenId(split[1]);
        if (ObjectUtil.isNotEmpty(user)) {
            if (StringUtils.isBlank(user.getPhone())) {
                //如果无手机信息,则跳转手机绑定页面
                BeanUtils.copyProperties(wxUserInfo,user);
                wxUserService.updatePhoneByOpenId(user);
                response.sendRedirect("/wx/register?openId="+split[1]);
            } else {
                if (STATE.equals(STRING_ONE)) {
                    //代办
                    response.sendRedirect("/wx/daiban?openId=" + split[1]);
                } else if (STATE.equals(STRING_TWO)) {
                    //我的合同
                    response.sendRedirect("/wx/assign?openId=" + split[1]);
                } else if (STATE.equals(STRING_THREE)) {
                    //我的账户
                    response.sendRedirect("/wx/account?openId=" + split[1]);
                } else {
                    //否则直接跳转首页
                    response.sendRedirect("/wx/index?openId=" + split[1]);
                }
            }
        } else {
            if(ObjectUtil.isNotEmpty(wxUserInfo.getTagid_list())){
                wxUserInfo.setTagIdlist(wxUserInfo.getTagid_list().toString());
            }
            wxUserService.registerUser(wxUserInfo);
            response.sendRedirect("/wx/register?openId="+split[1]);
        }
    }



    private String getAuthTokenAndOpenid(HttpServletRequest request) {
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        String accesstoken = redisTemplate.opsForValue().get(WX_USER_AUTH_ACCESS_TOKEN);
        String openId,refreshToken;
        if (StringUtils.isBlank(accesstoken)) {
            String code = request.getParameter("code");
            String url = "https://api.weixin.qq.com/sns/oauth2/access_token" +
                    "?appid=" + appid +
                    "&secret=" + secret +
                    "&code=" + code +
                    "&grant_type=authorization_code";

            String result = HttpUtil.get(url);
            com.alibaba.fastjson2.JSONObject object = com.alibaba.fastjson2.JSONObject.parseObject(result);
            if (ObjectUtil.isNotEmpty(object)) {
                accesstoken = object.getString(WX_APP_ACCESS_TOKEN);
                openId = object.getString(WX_USER_AUTH_OPENID);
                refreshToken = object.getString(WX_USER_AUTH_OPENID);
                accesstoken = accesstoken + "#" + openId + "#" + refreshToken;
                redisTemplate.opsForValue().set(WX_USER_AUTH_ACCESS_TOKEN, accesstoken + "#" + openId + "#" + refreshToken);
                redisTemplate.expire(WX_USER_AUTH_ACCESS_TOKEN, WX_APP_EXPIRE_IN, TimeUnit.SECONDS);
            }
        }
        return accesstoken;
    }
}
