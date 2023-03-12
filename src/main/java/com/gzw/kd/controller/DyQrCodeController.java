package com.gzw.kd.controller;

import cn.hutool.core.lang.UUID;
import cn.hutool.extra.qrcode.QrCodeUtil;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.gzw.kd.common.utils.JwtUtils;
import com.gzw.kd.common.utils.MD5Util;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 高志伟
 */
@SuppressWarnings("all")
@Slf4j
@RestController
@RequestMapping("/qrt")
public class DyQrCodeController {

    @GetMapping("/dyQrCodeWriter")
    public void gen(HttpServletResponse response) {
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/jpeg");
        try {
            String uid = MD5Util.inputPassToFormPass("uid");
            String token = JwtUtils.getJwtToken(uid, "kd....");
            String str = "/bindUserIdAndToken?token=" + token + "&uuid=" + uid;
            System.out.println(str);
            QrCodeUtil.generate(str, 300, 300, "jpg", response.getOutputStream());
        } catch (Exception e) {
            log.error("generate qrcode error ", e);
        }

    }


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
