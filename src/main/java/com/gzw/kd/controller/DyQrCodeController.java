package com.gzw.kd.controller;

import cn.hutool.core.lang.UUID;
import cn.hutool.extra.qrcode.QrCodeUtil;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.gzw.kd.common.R;
import com.gzw.kd.common.utils.JwtUtils;
import com.gzw.kd.common.utils.MD5Util;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 高志伟
 */
@Api(tags = "二维码生成")
@Slf4j
@RestController
@RequestMapping("/qrt")
public class DyQrCodeController {


    private static final String SSO_LOGIN_KEY = "sso_login_:key:";

    @Resource
    private RedisTemplate<String,String> redisTemplate;


    /**
     * 二维码生成
     * @param response res
     */

    @ApiOperation(value = "二维码生成")
    @GetMapping("/dyQrCodeWriter")
    public void gen(HttpServletResponse response) {
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/jpeg");
        try {
            String uid = MD5Util.inputPassToFormPass(UUID.fastUUID().toString());
            System.out.println(uid);
            redisTemplate.opsForValue().set(SSO_LOGIN_KEY+uid,uid,5, TimeUnit.MINUTES);
            QrCodeUtil.generate(uid, 300, 300, "jpg", response.getOutputStream());
        } catch (Exception e) {
            log.error("generate qrcode error ", e);
        }

    }


    /**
     * web二维码登录地址，这里为了测试才传{user},实际项目中user是通过其他方式传值
     *
     * @param uid uid
     * @param user user
     * @return result
     */
    @ApiOperation("模拟用户扫码确认")
    @GetMapping("/ssoLogin/{uid}/{user}")
    public R setUser(@ApiParam(value = "唯一ID") @PathVariable String uid, @ApiParam(value = "用户信息") @PathVariable String user) {

        String value = redisTemplate.opsForValue().get(SSO_LOGIN_KEY + uid);

        if (StringUtils.isNotBlank(value)) {
            String token = JwtUtils.getJwtToken(uid, user);
            redisTemplate.opsForValue().set(SSO_LOGIN_KEY + uid, token, 1, TimeUnit.MINUTES);
        } else {
            return R.error().data("stats","refresh");
        }
        return R.ok().data("uid",uid).data("user",user);
    }



    /**
     * 等待二维码扫码结果的长连接
     *
     * @param uid uid
     * @return result
     */
    @ApiOperation("等待二维码扫码结果的长连接")
    @GetMapping("/ssoLogin/{uid}")
    public R  getResponse(@ApiParam(value = "唯一ID") @PathVariable String uid) {

        String token = redisTemplate.opsForValue().get(SSO_LOGIN_KEY + uid);
        // 长时间不扫码，二维码失效。需重新获二维码
        if (StringUtils.isBlank(token)) {
            return R.error().data("stats", "refresh");
        }
        // 登录扫码二维码
        if (token.equals(uid)) {
            return R.error().data("stats","waiting");
        }

        // 登录成,认证信息写入session
        return R.ok().data("stats", "ok");
    }



    @SuppressWarnings("unused")
    public byte[] imageBytes() throws Exception {

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, Object> hints = new HashMap<>(2);
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        BitMatrix bitMatrix = qrCodeWriter.encode(UUID.fastUUID().toString(), BarcodeFormat.QR_CODE, 200, 200, hints);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "jpg", outputStream);
        return outputStream.toByteArray();

    }
}
