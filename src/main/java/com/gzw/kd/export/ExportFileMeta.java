package com.gzw.kd.export;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.experimental.Accessors;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;

import static com.gzw.kd.common.Constants.CONTENT_DISPOSITION_NAME;
import static com.gzw.kd.common.Constants.CONTENT_DISPOSITION_VALUE;

/**
 * @author gzw
 */
@Data
@Accessors(chain = true)
public class ExportFileMeta {

    /**
     * 是否导出成功
     */
    private Boolean isSucceed = true;

    /**
     * 导出文件
     */
    private File file;

    /**
     * 导出记录数
     */
    private int size;

    /**
     * 响应流content-type
     */
    private String contentType;

    /**
     * 写下载响应头
     *
     * @param response HttpServletResponse
     */
    public void writeResponse(final HttpServletResponse response) throws IOException {
        if (!this.isSucceed) {
            throw new RuntimeException("Export file failed");
        }
        response.setContentType(this.contentType);
        response.setHeader(CONTENT_DISPOSITION_NAME, CONTENT_DISPOSITION_VALUE +
                URLEncoder.encode(FileUtil.mainName(file), "UTF-8") + StrUtil.DOT + FileUtil.extName(file));
        FileUtil.writeToStream(file, response.getOutputStream());
    }
}
