package com.gzw.kd.vo.output;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author gzw
 * @description：
 * @since：2023/2/14 18:12
 */
@Accessors(chain = true)
@Data
public class FileOutput {

    private String attachName;

    private String attachSize;

    private String uploadTime;

    private String attachUrl;
}
