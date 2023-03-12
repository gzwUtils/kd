package com.gzw.kd.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.gzw.kd.export.ExportFileMeta;
import com.gzw.kd.export.ExportSheet;
import com.gzw.kd.export.FieldHandler;
import com.gzw.kd.service.CommonExportService;
import com.gzw.kd.service.ReportExportCommonService;
import com.gzw.kd.vo.output.CommonExportOutput;
import org.springframework.stereotype.Service;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import static com.gzw.kd.common.Constants.XLSX_DOWNLOAD_CONTENT_TYPE;
import static com.gzw.kd.common.Constants.XLSX_EXPORT_FILE_SUFFIX;

/**
 * @author 高志伟
 */

@Service
public class ReportExportCommonServiceImpl implements ReportExportCommonService, CommonExportService {

    @Override
    public ExportFileMeta assembleExportFile(String downloadFilePrefix, List<ExportSheet> list) {
        ExportFileMeta exportFileMeta = new ExportFileMeta().setIsSucceed(false);
        String fileName = downloadFilePrefix + DateUtil.date().toString(DatePattern.PURE_DATETIME_PATTERN) + XLSX_EXPORT_FILE_SUFFIX;
        String filePath = export(list, fileName);
        File downloadFile = new File(filePath);

        return exportFileMeta
                .setFile(downloadFile)
                .setContentType(XLSX_DOWNLOAD_CONTENT_TYPE)
                .setIsSucceed(true);
    }

    @Override
    public ExportSheet assembleExportFile(String sheetName, Map<String, FieldHandler> exportHandler, List<? extends CommonExportOutput> dataList, int batchSize) {
        List<List<Map<String, Object>>> list = new ArrayList<>(dataList.size());
        // 将对象列表转换为excel数据列表
        dataList.forEach(data -> list.add(CollUtil.newArrayList(Collections.singleton(JSONUtil.parseObj(data)))));
        return new ExportSheet(sheetName, list, exportHandler, batchSize);
    }
}
