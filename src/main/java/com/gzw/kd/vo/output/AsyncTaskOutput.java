package com.gzw.kd.vo.output;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author 高志伟
 */

@Data
@Accessors(chain = true)
public class AsyncTaskOutput {

    private Long numberOfSuccesses;

    private Long numberOfFailed;

    private String fileName;

    private String filePath;
}
