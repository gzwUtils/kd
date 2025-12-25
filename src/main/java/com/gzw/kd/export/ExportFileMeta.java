package com.gzw.kd.export;

import cn.hutool.core.io.FileUtil;
import com.gzw.kd.common.enums.ResultCodeEnum;
import com.gzw.kd.common.exception.GlobalException;
import lombok.Data;
import lombok.experimental.Accessors;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

import static com.gzw.kd.common.Constants.*;

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
        if (Boolean.FALSE.equals(this.isSucceed)) {
            throw new GlobalException(ResultCodeEnum.EXPORT_FILE_ERROR);
        }
        response.setContentType(this.contentType);
        response.setHeader(CONTENT_DISPOSITION_NAME, CONTENT_DISPOSITION_VALUE +
                 file.getName() +XLSX_EXPORT_FILE_SUFFIX);
        FileUtil.writeToStream(file, response.getOutputStream());
    }
}
