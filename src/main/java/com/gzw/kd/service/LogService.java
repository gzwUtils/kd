package com.gzw.kd.service;

import com.gzw.kd.common.entity.Log;
import com.gzw.kd.export.ExportFileMeta;
import com.gzw.kd.vo.input.LogSearchInput;
import java.util.List;

/**
 * @author 高志伟
 */
public interface LogService  {

    /**
     * 日志导出
     * @param logSearchInput param
     * @return res
     */
    ExportFileMeta export(LogSearchInput logSearchInput);


    /**
     * 获取操作记录
     * @param name req
     * @return res
     */
    List<Log> getAllOperation(String name);
}
