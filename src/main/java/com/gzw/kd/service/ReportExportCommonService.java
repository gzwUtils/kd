package com.gzw.kd.service;


import com.gzw.kd.export.ExportFileMeta;
import com.gzw.kd.export.ExportSheet;
import com.gzw.kd.export.FieldHandler;
import com.gzw.kd.vo.output.CommonExportOutput;

import java.util.List;
import java.util.Map;

/**
 * 报表导出通用Service
 * @author gzw
 */
public interface ReportExportCommonService {

    /**
     * 组装项目报表文件内容
     * @param downloadFilePrefix downloadFilePrefix
     * @param list list
     * @return
     */
    ExportFileMeta assembleExportFile(String downloadFilePrefix, List<ExportSheet> list);

    /**
     * 组装项目报表文件内容
     * @param sheetName
     * @param exportHandler
     * @param dataList
     * @param batchSize
     * @return
     */
    ExportSheet assembleExportFile(String sheetName, Map<String, FieldHandler> exportHandler, List<? extends CommonExportOutput> dataList, int batchSize);

}
