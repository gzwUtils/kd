package com.gzw.kd.common.utils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @author gzw
 * @version 1.0
 * @Date 2022/10/20
 * @dec SM4Utils
 */
@SuppressWarnings("all")
public class SM4Utils {


    /**
     * 默认密码，禁止修改
     */
    public static final String DEFAULT_KEY = "Superred-Abcd!@#";
    /**
     * 默认偏移量，禁止修改
     */
    public static final String DEFAULT_IV = "UISwD9fW6cFh9SNS";
    /**
     * 密文类型BASE64字符串
     */
    public static final String CIPHER_TEXT_BASE64 = "base64";

    /**
     * ECB 模式加密
     *
     * @param plainText      需要加密的文本内容
     * @param secretKey      密钥(字符串长度：16，16进制字符串长度：32)
     * @param hexString      密钥是否是16进制字符串（true/false）
     * @param cipherTextType 密文类型，默认16进制字符串， base64（base64字符串）。
     * @author gzw
     * @date 2020/11/24 15:19
     */
    public static String encryptData_ECB(String plainText, String secretKey, boolean hexString, String cipherTextType) throws Exception {
        SM4_Context ctx = new SM4_Context();
        ctx.isPadding = true;
        ctx.mode = SM4.SM4_ENCRYPT;

        byte[] keyBytes;
        if (hexString) {
            keyBytes = Util.hexStringToBytes(secretKey);
        } else {
            keyBytes = secretKey.getBytes();
        }

        SM4 sm4 = new SM4();
        sm4.sm4_setkey_enc(ctx, keyBytes);
        byte[] encrypted = sm4.sm4_crypt_ecb(ctx, plainText.getBytes(StandardCharsets.UTF_8));
        return getCipherText(encrypted, cipherTextType);
    }

    /**
     * ECB 模式解密
     *
     * @param cipherText     密文
     * @param secretKey      密钥(字符串长度：16，16进制字符串长度：32)
     * @param hexString      密钥是否是16进制字符串（true/false）
     * @param cipherTextType 密文类型，默认16进制字符串， base64（base64字符串）。
     * @author gzw
     * @date 2020/11/24 15:13
     */
    public static String decryptData_ECB(String cipherText, String secretKey, boolean hexString, String cipherTextType) throws Exception {
        SM4_Context ctx = new SM4_Context();
        ctx.isPadding = true;
        ctx.mode = SM4.SM4_DECRYPT;

        byte[] keyBytes;
        if (hexString) {
            keyBytes = Util.hexStringToBytes(secretKey);
        } else {
            keyBytes = secretKey.getBytes();
        }
        SM4 sm4 = new SM4();
        sm4.sm4_setkey_dec(ctx, keyBytes);

        byte[] cipher = null;
        if (CIPHER_TEXT_BASE64.equals(cipherTextType)) {
            cipher = Base64.getDecoder().decode(cipherText);
        } else {
            cipher = Util.hexStringToBytes(cipherText);
        }
        if (cipher != null) {
            byte[] decrypted = sm4.sm4_crypt_ecb(ctx, cipher);
            return new String(decrypted, StandardCharsets.UTF_8);
        }
        return null;
    }

    /**
     * CBC模式加密
     *
     * @param plainText      需要加密的字符串内容
     * @param secretKey      密钥(字符串长度：16，16进制字符串长度：32)
     * @param iv             偏移量(字符串长度：16，16进制字符串长度：32)
     * @param hexString      密钥是否是16进制字符串（true/false）
     * @param cipherTextType 密文类型，默认16进制字符串， base64（base64字符串）。
     * @author gzw
     * @date 2020/11/24 15:14
     */
    public static String encryptData_CBC(String plainText, String secretKey, String iv, boolean hexString, String cipherTextType) throws Exception {
        SM4_Context ctx = new SM4_Context();
        ctx.isPadding = true;
        ctx.mode = SM4.SM4_ENCRYPT;

        byte[] keyBytes;
        byte[] ivBytes;
        if (hexString) {
            keyBytes = Util.hexStringToBytes(secretKey);
            ivBytes = Util.hexStringToBytes(iv);
        } else {
            keyBytes = secretKey.getBytes();
            ivBytes = iv.getBytes();
        }

        SM4 sm4 = new SM4();
        sm4.sm4_setkey_enc(ctx, keyBytes);
        byte[] encrypted = sm4.sm4_crypt_cbc(ctx, ivBytes, plainText.getBytes(StandardCharsets.UTF_8));

        return getCipherText(encrypted, cipherTextType);
    }

    /**
     * 获取密文信息
     *
     * @param encrypted 编码后的byte数组
     * @param type      密文类型,默认16进制字符串， base64（base64字符串）。
     * @author gzw
     * @date 2020/11/24 16:39
     */
    private static String getCipherText(byte[] encrypted, String type) {
        String cipherText = null;
        if (CIPHER_TEXT_BASE64.equals(type)) {
            cipherText = Base64.getEncoder().encodeToString(encrypted);
        } else {
            cipherText = Util.byteToHex(encrypted);
        }
        return cipherText;
    }

    /**
     * CBC模式 解密
     *
     * @param cipherText     密文
     * @param secretKey      密钥(字符串长度：16，16进制字符串长度：32)
     * @param iv             偏移量(字符串长度：16，16进制字符串长度：32)
     * @param hexString      密钥是否是16进制字符串（true/false）
     * @param cipherTextType 密文类型，默认16进制字符串， base64（base64字符串）。
     * @author gzw
     * @date 2020/11/24 15:16
     */
    public static String decryptData_CBC(String cipherText, String secretKey, String iv, boolean hexString, String cipherTextType) throws Exception {
        SM4_Context ctx = new SM4_Context();
        ctx.isPadding = true;
        ctx.mode = SM4.SM4_DECRYPT;

        byte[] keyBytes;
        byte[] ivBytes;
        if (hexString) {
            keyBytes = Util.hexStringToBytes(secretKey);
            ivBytes = Util.hexStringToBytes(iv);
        } else {
            keyBytes = secretKey.getBytes();
            ivBytes = iv.getBytes();
        }
        SM4 sm4 = new SM4();
        sm4.sm4_setkey_dec(ctx, keyBytes);
        byte[] cipher = null;
        if (CIPHER_TEXT_BASE64.equals(cipherTextType)) {
            cipher = Base64.getDecoder().decode(cipherText);
        } else {
            cipher = Util.hexStringToBytes(cipherText);
        }
        if (cipher != null) {
            byte[] decrypted = sm4.sm4_crypt_cbc(ctx, ivBytes, cipher);
            return new String(decrypted, StandardCharsets.UTF_8);
        }
        return null;
    }
}
