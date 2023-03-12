package com.gzw.kd.service.impl;

import com.gzw.kd.common.entity.Log;
import com.gzw.kd.export.ExportFieldHandlerRegistry;
import com.gzw.kd.export.ExportFileMeta;
import com.gzw.kd.export.ExportSheet;
import com.gzw.kd.mapper.LogMapper;
import com.gzw.kd.service.CommonExportService;
import com.gzw.kd.service.LogService;
import com.gzw.kd.service.ReportExportCommonService;
import com.gzw.kd.vo.output.LogExportOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import static com.gzw.kd.common.Constants.DOWNLOAD_FILE_PREFIX;
import static com.gzw.kd.common.Constants.XLSX_MAX_EXPORT_ROW_SIZE;

/**
 * @author 高志伟
 */
@Service
public class LogServiceImpl implements LogService, CommonExportService {

    @Resource
    private LogMapper logMapper;


    @Autowired
    private ReportExportCommonService reportExportCommonService;

    @Resource
    private ExportFieldHandlerRegistry exportFieldHandlerRegistry;

    public LogServiceImpl(ReportExportCommonService reportExportCommonService, ExportFieldHandlerRegistry exportFieldHandlerRegistry) {
        this.reportExportCommonService = reportExportCommonService;
        this.exportFieldHandlerRegistry = exportFieldHandlerRegistry;
    }

    @Override
    public ExportFileMeta export(LocalDateTime time) {
        List<LogExportOutput> outputs = logMapper.getLogInfo(time);
        ExportSheet exportSheet = reportExportCommonService.assembleExportFile(
                DOWNLOAD_FILE_PREFIX,
                exportFieldHandlerRegistry.getTest(), outputs, XLSX_MAX_EXPORT_ROW_SIZE);
        ExportFileMeta fileMeta = reportExportCommonService.assembleExportFile(DOWNLOAD_FILE_PREFIX, Collections.singletonList(exportSheet));
        fileMeta.setSize(outputs.size());
        return fileMeta;
    }

    @Override
    public List<Log> getAllOperation(String name) {
        return logMapper.getAllOperation(name);
    }


}
