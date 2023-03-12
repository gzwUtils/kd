package com.gzw.kd.service;

import com.gzw.kd.common.entity.Log;
import com.gzw.kd.export.ExportFileMeta;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author 高志伟
 */
public interface LogService  {

    /**
     * 日志导出
     * @param time time
     * @return
     */
    public ExportFileMeta export(LocalDateTime time );


    /**
     * 获取操作记录
     * @param name
     * @return
     */
    List<Log> getAllOperation(String name);
}
