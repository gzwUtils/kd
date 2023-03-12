package com.gzw.kd.vo.output;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 高志伟
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class LogExportOutput extends CommonExportOutput{

    private String username;

    private String operation;

    private String createTime;

    private String result;

    private String ip;

    private int  id;

}
