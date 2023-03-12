package com.gzw.kd.controller;

import com.alibaba.fastjson2.JSON;
import com.alipay.api.internal.util.AlipaySignature;
import com.gzw.kd.common.R;
import com.gzw.kd.common.generators.RandomIdGenerator;
import com.gzw.kd.common.utils.AliPayUtils;
import com.gzw.kd.common.utils.ToolUtil;
import com.gzw.kd.common.entity.Assign;
import com.gzw.kd.common.entity.ZFBFaceToFaceModel;
import com.gzw.kd.service.CustomerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gzw.kd.common.Constants.*;

/**
 * @author 高志伟
 */
@Slf4j
@Controller
@RequestMapping("/ali")
@SuppressWarnings("all")
public class AlipayController {

    /**
     * 支付宝应用设置本地公钥后生成对应的支付宝公钥（非本地生成的公钥）
     */
    @Value("${alipay_public_key}")
    private  String aliAppPublicKey;

    /**签名类型*/
    @Value("${sign_type}")
    private  String signType ;


    @Resource
    private AliPayUtils aliPayUtils;


    @Resource
    private CustomerService customerService;


    @Resource
    RandomIdGenerator randomIdGenerator;


    @Resource
    private RedisTemplate<String,String> redisTemplate;

    /**
     * 生成支付宝二维码
     */

    @RequestMapping(value = "/qrcode", method = RequestMethod.GET)
    public String qrCode(HttpServletRequest request) throws Exception {
        String openId = request.getParameter("openId");
        Assign userByOpenId = customerService.getUserByOpenId(openId);
        ZFBFaceToFaceModel model = new ZFBFaceToFaceModel();
        String generate = randomIdGenerator.generate(16);
        model.setOutTradeNo(generate).setTotalAmount(userByOpenId.getBalance()).setSubject("kd").setBody("gzw..");
        R order = aliPayUtils.newAliOrder(model);
        request.setAttribute("code",order.getData().get("code"));
        return "/zfb/zfbQrCode";
    }

    /**支付宝回调接口*/
    /**
     * 不返回success，支付宝会在25小时以内完成8次通知（通知的间隔频率一般是：4m,10m,10m,1h,2h,6h,15h）才会结束通知发送。
     */


    @RequestMapping(value = "/aliNotify")
    public R aliNotify(HttpServletRequest request) {
        try {
            log.info("进入支付宝回调地址");
            Map<String, String> params = new HashMap<>(16);
            Map<String, String[]> requestParams = request.getParameterMap();
            log.info("支付宝验签参数：{}", JSON.toJSONString(requestParams));
            for (String name : requestParams.keySet()) {
                String[] values = requestParams.get(name);
                String valueStr = "";
                for (int i = 0; i < values.length; i++) {
                    valueStr = (i == values.length - 1) ? valueStr + values[i]
                            : valueStr + values[i] + ",";
                }
                params.put(name, valueStr);
            }
            boolean flag = AlipaySignature.rsaCheckV1(params, aliAppPublicKey, "UTF-8", signType);
            if (flag) {
                aliPayUtils.aliNotify(params);
                log.info("aliNotify 支付宝通知更改状态成功！");
                //TODO 写合同
                String assign = redisTemplate.opsForValue().get(ASSIGN_INFO_KEY_);
                List<?> list = ToolUtil.jsonToList(assign, Assign.class);
                Assign assign1 = (Assign) list.get(0);
                assign1.setStatus(INT_ONE);
                customerService.registerUser(assign1);
                //TODO 写订单
                String service = redisTemplate.opsForValue().get(ORDER_INFO_KEY_);

                return R.ok();
            }
        } catch (Throwable e) {
            log.error("aliNotify exception: ", e);
        }
        return R.error();
    }
}
