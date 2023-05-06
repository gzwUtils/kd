package com.gzw.kd.common.utils;

import com.gzw.kd.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.Properties;

/**
 * @author gzw
 * @version 1.0
 * @Date 2022/10/21
 * @dec
 */
@SuppressWarnings("all")
@Slf4j
public class PropertiesUtils extends Properties {


    public PropertiesUtils(String files){
        loadPropertiesFiles(files);
    }


    /**
     * 加载properties文件  多个文件用,隔开
     */
    public void loadPropertiesFiles(String files) {
        if (StringUtils.isBlank(files)) {
            return;
        }
        String[] properties = files.split(Constants.STRING_COMMA);
        BufferedReader bufferedReader = null;
        InputStream inputStream = null;

        for (String pro : properties) {
            try {
                inputStream = new FileInputStream(pro);
                bufferedReader = new BufferedReader(new UnicodeReader(inputStream, "UTF-8"));
                load(bufferedReader);
            } catch (UnsupportedEncodingException e) {
                log.error("加载parameters文件发生异常",e);
            } catch (IOException e) {
                log.error("发生IOException异常",e);
            } finally {
                try {
                    if (null != inputStream) {
                        inputStream.close();
                    }
                    if (bufferedReader != null) {
                        bufferedReader.close();
                    }
                } catch (IOException e) {
                    log.error("properties文件流关闭出现异常",e);
                }
            }
        }
    }
}
