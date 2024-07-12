package com.gzw.kd.common.utils;
import freemarker.template.Configuration;
import freemarker.template.Template;
import java.io.*;
import java.util.Locale;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

/**
 * @author gzw
 * @description： freemarker
 * @since：2022/11/5 16:59
 */
@SuppressWarnings("all")
@Slf4j
public class FreemarkerUtils {


    private static Configuration freemarkerCfg = null;

    static {
        freemarkerCfg = new Configuration();
        //freemarker的模板目录
        freemarkerCfg.setEncoding(Locale.CHINA, "UTF-8");
        freemarkerCfg.setTagSyntax(Configuration.AUTO_DETECT_TAG_SYNTAX);
    }

    /**
     * freemarker渲染html
     */
    public static String freeMarkerRender(Map<String, Object> data, String htmlTmp) {
        Writer out = new StringWriter();
        try {
            // 获取模板,并设置编码方式
            Template template = freemarkerCfg.getTemplate(htmlTmp, "UTF-8");
            template.setEncoding("UTF-8");
            // 合并数据模型与模板
            template.process(data, out);
            out.flush();
            return out.toString();
        } catch (Exception e) {
            log.error("freemaker异常 message:{}", e.getMessage(), e);
        } finally {
            try {
                out.close();
            } catch (IOException ex) {
                log.error("freemaker异常 message:{}", ex.getMessage(), ex);
            }
        }
        return null;
    }

}
