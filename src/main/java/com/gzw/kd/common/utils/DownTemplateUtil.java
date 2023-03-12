package com.gzw.kd.common.utils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

/**
 * 下载模板工具
 * @author gaozhiwei
 */
public class DownTemplateUtil {

    public static synchronized void downTemplate(HttpServletResponse response, Class<?> classes, String path, String excelName) throws IOException {
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        try {
            InputStream is = classes.getResourceAsStream(path + excelName);
            response.setHeader("Content-Disposition",
                    "attachment;fileName=" + new String(excelName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1));
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/force-download");
            in = new BufferedInputStream(is);
            ServletOutputStream os = response.getOutputStream();
            out = new BufferedOutputStream(os);
            byte[] data = new byte[1024];
            int len;
            while (-1 != (len = in.read(data, 0, data.length))) {
                out.write(data, 0, len);
            }
        } finally {
            close(in, out);
        }
    }

    public static void close(Closeable... cloneables) throws IOException {
        if (cloneables == null) {
            return;
        }
        for (Closeable cloneable : cloneables) {
            if (cloneable != null) {
                cloneable.close();
            }
        }
    }
}
