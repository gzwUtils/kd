package com.gzw.kd.common.utils;

import cn.hutool.core.util.ObjectUtil;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("all")
@Slf4j
@Data
public class QrCodeUtils {
    /**二维码内容*/
    private String codeText;
    /**二维码类型*/
    private BarcodeFormat barcodeFormat;
    private int width;
    private int height;
    /**图片格式*/
    private String imageFormat;
    /**背景色，颜色RGB的数值既可以用十进制表示，也可以用十六进制表示*/
    private int backColorRGB;
    /**二维码颜色*/
    private int codeColorRGB;
    /**二维码纠错能力*/
    private ErrorCorrectionLevel errorCorrectionLevel;

    private String encodeType;


    public QrCodeUtils(String text){
        codeText = text;
        barcodeFormat = BarcodeFormat.PDF_417;
        width = 400;
        height = 400;
        imageFormat = "png";
        backColorRGB = 0xFFFFFFFF;
        codeColorRGB = 0xFF000000;
        errorCorrectionLevel = ErrorCorrectionLevel.H;
        encodeType = "UTF-8";
    }


    private BufferedImage toBufferedImage(BitMatrix bitMatrix) {
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, bitMatrix.get(x, y) ? this.codeColorRGB: this.backColorRGB);
            }
        }
        return image;
    }

    private byte[] writeToBytes(BitMatrix bitMatrix) {

        try {
            BufferedImage bufferedimage = toBufferedImage(bitMatrix);
            //将图片保存到临时路径中
            File file = File.createTempFile("~pic","."+ this.imageFormat);
            ImageIO.write(bufferedimage,this.imageFormat,file);
            //获取图片转换成的二进制数组
            FileInputStream fis = new FileInputStream(file);
            byte[] imageBytes = new byte[fis.available()];
            fis.read(imageBytes);
            fis.close();
            //删除临时文件
            if (file.exists()) {
                file.delete();
            }

            return imageBytes;
        } catch (Exception e) {
            log.error("Image err :{}",e.getMessage());
            return null;
        }

    }


    //获取二维码图片的字节数组
    public byte[] getQRCodeBytes() {

        try {
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

            //设置二维码参数
            Map hints = new HashMap();
            if (ObjectUtil.isNotEmpty(errorCorrectionLevel)) {
                //设置二维码的纠错级别
                hints.put(EncodeHintType.ERROR_CORRECTION, this.errorCorrectionLevel);
            }

            if (StringUtils.isNotBlank(encodeType)) {
                //设置编码方式
                hints.put(EncodeHintType.CHARACTER_SET, this.encodeType);
            }

            BitMatrix bitMatrix = multiFormatWriter.encode(this.codeText, BarcodeFormat.QR_CODE, this.width, this.height, hints);
            byte[] bytes = writeToBytes(bitMatrix);

            return bytes;
        } catch (Exception e) {
            log.error("qrcode generate  err :{}",e.getMessage());
            return null;
        }

    }
}
