package com.gzw.kd.common.utils;

import com.alibaba.fastjson2.JSONObject;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradePrecreateModel;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.gzw.kd.common.R;
import com.gzw.kd.common.enums.ResultCodeEnum;
import com.gzw.kd.common.exception.GlobalException;
import com.gzw.kd.common.entity.AlipayJsonRootBean;
import com.gzw.kd.common.entity.ZFBFaceToFaceModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Map;
import static com.gzw.kd.common.Constants.*;

/**
 * @author 高志伟
 */
@SuppressWarnings("all")
@Component
@Slf4j
public class AliPayUtils  {

    /**支付宝请求地址*/
    @Value("${open_api_domain}")
    private  String openApiDomain;
    /**支付宝应用ID*/
    @Value("${appId}")
    private  String aliAppId;
    /**本地通过"支付宝开放平台开发助手"生成的私钥*/
    @Value("${private_key}")
    private  String aliAppPrivateKey;
    /**支付宝应用设置本地公钥后生成对应的支付宝公钥（非本地生成的公钥）*/
    @Value("${alipay_public_key}")
    private  String aliAppPublicKey;
    /**支付宝回调的接口地址*/
    @Value("${NotifyUrl}")
    private  String aliNotifyUrl ;

    /**签名类型*/
    @Value("${sign_type}")
    private  String signType ;

    /**
     * 预支付
     * @param zfbFaceToFaceModel zf
     * @return
     * @throws Exception
     */
    public R newAliOrder(ZFBFaceToFaceModel zfbFaceToFaceModel) throws Exception {
        log.info("开始调用支付宝生成支付二维码...");
        //实例化客户端
        AlipayClient alipayClient = new DefaultAlipayClient(openApiDomain, aliAppId, aliAppPrivateKey, "json", "utf-8", aliAppPublicKey, signType);
        //设置请求参数
        AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();
        AlipayTradePrecreateModel model = new AlipayTradePrecreateModel();
        model.setOutTradeNo(zfbFaceToFaceModel.getOutTradeNo());
        model.setTotalAmount("0.01");
        model.setSubject(zfbFaceToFaceModel.getSubject());
        model.setBody(zfbFaceToFaceModel.getBody());
        model.setQrCodeTimeoutExpress(zfbFaceToFaceModel.getTimeoutExpress());
        request.setBizModel(model);
        //支付宝异步通知地址
        request.setNotifyUrl(aliNotifyUrl);
        log.info("创建支付宝订单，请求参数：{} ", JSONObject.toJSONString(request));
        //调用接口
        AlipayTradePrecreateResponse response = alipayClient.execute(request);

        log.info("创建支付宝订单，返回值：{} ", JSONObject.toJSONString(response));
        if (!response.isSuccess()) {
            throw new GlobalException(ResultCodeEnum.MachineOrderAlipayException);
        }
        AlipayJsonRootBean alipayJsonRootBean = JSONObject.parseObject(response.getBody(), AlipayJsonRootBean.class);
        if(!SUCCESS_CODE.equals(alipayJsonRootBean.getAlipay_trade_precreate_response().getCode())){
            throw new GlobalException(ResultCodeEnum.MachineOrderAlipayException);
        }
        log.info("交易订单号outTradeNo：{} ", response.getOutTradeNo());
        log.info("支付二维码qrCode：{} ", response.getQrCode());
        return R.ok().data("code",response.getQrCode());
    }

    public void aliNotify(Map<String, String> param) throws Exception {
        log.info("支付宝异步回调接口数据处理");
        //只有支付成功后，支付宝才会回调应用接口，可直接获取支付宝响应的参数
        String order_id = param.get(A_LI_OUT_TRADE_NO);
        //出于安全考虑，通过支付宝回传的订单号查询支付宝交易信息
        AlipayTradeQueryResponse aliResp = queryOrder(order_id);
        if (!SUCCESS_CODE.equals(aliResp.getCode())) {
            //返回值非10000
            throw new GlobalException(aliResp.getSubMsg(),ResultCodeEnum.MachineOrderAlipayException.getCode());
        }
        if (!A_LI_TRADE_SUCCESS.equals(aliResp.getTradeStatus()) && !TRADE_FINISHED.equals(aliResp.getTradeStatus())) {
            //支付宝订单状态不是支付成功
            throw new GlobalException(ResultCodeEnum.MachineOrderAliUnPay);
        }
        //可对支付宝响应参数AlipayTradeQueryResponse进行处理

    }

    public AlipayTradeQueryResponse queryOrder(String orderId) throws Exception {
        log.info("查询支付宝订单，订单编号为：{}", orderId);
        AlipayClient alipayClient = new DefaultAlipayClient(openApiDomain, aliAppId, aliAppPrivateKey, "json", "utf-8", aliAppPublicKey, signType);
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        AlipayTradeQueryModel model = new AlipayTradeQueryModel();
        model.setOutTradeNo(orderId);
        request.setBizModel(model);
        AlipayTradeQueryResponse response = alipayClient.execute(request);
        log.info("查询支付宝订单，返回数据：{}", response);
        return response;
    }
}
