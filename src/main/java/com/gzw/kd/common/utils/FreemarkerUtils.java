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
        try {
            freemarkerCfg = new Configuration(Configuration.VERSION_2_3_31);
            // 关键：设置模板加载路径为类路径
            freemarkerCfg.setClassForTemplateLoading(FreemarkerUtils.class, "/templates/");
            // 或者使用文件系统路径（如果模板在文件系统中）
            freemarkerCfg.setEncoding(Locale.CHINA, "UTF-8");
            freemarkerCfg.setDefaultEncoding("UTF-8");
            freemarkerCfg.setTagSyntax(Configuration.AUTO_DETECT_TAG_SYNTAX);
        } catch (Exception e) {
            log.error("初始化FreeMarker配置失败", e);
        }
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
