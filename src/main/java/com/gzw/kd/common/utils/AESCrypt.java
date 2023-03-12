package com.gzw.kd.common.utils;

import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.symmetric.AES;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;

/**
 * @author gzw
 * @version 1.0
 * @Date 2022/10/9
 * @dec
 */
@Slf4j
@Component
public class AESCrypt {

    public static final String KEY = "fac416c49e8e4d7bb08e08952ae4ec32";
    public static final String IV = "c49e8e4d7fac4165";

    private  static   AES  aes ;

    @PostConstruct
    public void init() {
         aes = new AES(Mode.CBC, Padding.ZeroPadding, KEY.getBytes(StandardCharsets.UTF_8), IV.getBytes(StandardCharsets.UTF_8));
         log.info("初始化成功了");
    }

    //解密
    public static String decrypt(String data) {
        return aes.decryptStr(data);
    }

    //解密
    public static String encrypt(String data) {
        return aes.encryptBase64(data);
    }

}
