package com.gzw.kd.export.service;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson2.JSON;
import com.gzw.kd.common.enums.AsyncTaskTypeEnum;
import com.gzw.kd.common.enums.ResultCodeEnum;
import com.gzw.kd.common.exception.GlobalException;
import com.gzw.kd.common.utils.EsScrollIterable;
import com.gzw.kd.export.AsyncTaskService;
import com.gzw.kd.export.BigExcelWriterNew;
import com.gzw.kd.export.ExportFieldHandlerRegistry;
import com.gzw.kd.export.ExportSheet;
import com.gzw.kd.mapper.LogEsMapper;
import com.gzw.kd.service.CommonExportService;
import com.gzw.kd.vo.input.LogSearchInput;
import com.gzw.kd.vo.output.AsyncTaskOutput;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchResponse;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;
import static com.gzw.kd.common.Constants.*;
import static com.gzw.kd.common.enums.AsyncTaskTypeEnum.ALL_LOG_EXPORT;

/**
 * @author 高志伟
 */
@SuppressWarnings("all")
@Slf4j
@Service
public class LogExportAsyncTask implements AsyncTaskService, CommonExportService {


    @Resource
    private final ExportFieldHandlerRegistry exportFieldHandlerRegistry;


    @Resource
    private LogEsMapper logEsMapper;

    private static final long EXPORT_ROWS_LIMIT = 50_000L;

    public LogExportAsyncTask(ExportFieldHandlerRegistry exportFieldHandlerRegistry) {
        this.exportFieldHandlerRegistry = exportFieldHandlerRegistry;
    }


    @Override
    public AsyncTaskOutput processAsyncTask(String params, String creator, LocalDateTime createTime,String fileName) {
        LogSearchInput input = JSON.parseObject(params, LogSearchInput.class);
        if(input != null){
            final SearchResponse scrollResponse = logEsMapper.scrollBegin(input);
            final long dataRowCount = scrollResponse.getHits().getTotalHits().value;

            if ( dataRowCount > EXPORT_ROWS_LIMIT) {
                throw new GlobalException("The number of exported leads has exceeded the limit, " +
                        "limit is " + EXPORT_ROWS_LIMIT, ResultCodeEnum.UNKNOWN_ERROR.getCode());
            }

            if (dataRowCount > INT_ZERO) {

                // 计算需要生成的excel文件个数
                final int exportFileNum = calcExportFileNum(scrollResponse.getHits().getTotalHits().value);
                // 导出时间, 就取生成任务的时间

                // 按照需要导出的文件数量, 进行分批生成文件导出
                List<String> filePaths = new ArrayList<>(exportFileNum);

                // es scroll 迭代器
                EsScrollIterable iterable = new EsScrollIterable(
                        logEsMapper.scrollBegin(input), logEsMapper::scrollByPage);
                Iterator<List<Map<String, Object>>> itr = iterable.iterator();

                for (int i = 0; i < exportFileNum; ++i) {
                    final String fileNameAndSuffix = fileName + STRING_UNDERLINE + IdUtil.fastSimpleUUID() + XLSX_EXPORT_FILE_SUFFIX;
                    final String filePath = defaultStoragePath(fileNameAndSuffix);
                    log.info("async file path.....{}",filePath);
                    BigExcelWriterNew writer = new BigExcelWriterNew(filePath, fileName + STRING_UNDERLINE + i);

                    // 单个文件最多查询10个批次, 即最多50_000条数据
                    int batchSize = 10;
                    while (batchSize-- > 0 && itr.hasNext()) {
                        List<Map<String, Object>> dataSet = itr.next();
                        ExportSheet currentSheet =
                                new ExportSheet(
                                        Collections.singletonList(dataSet),
                                        exportFieldHandlerRegistry.getTest(),
                                        input.getExportFields().toArray(new String[0]));
                        if (batchSize == 9) {
                            currentSheet.write2File(writer);
                        } else {
                            currentSheet.write2FileWithoutHeader(writer);
                        }
                    }

                    // 关闭文件流
                    writer.close();
                    // 记录本次的文件存储路径
                    filePaths.add(filePath);
                }

                // 清除scroll context
                logEsMapper.scrollClear(iterable.getLastScrollId());

                return new AsyncTaskOutput()
                        .setFileName(input.getExportFileName())
                        .setNumberOfSuccesses(dataRowCount)
                        .setNumberOfFailed(0L).setFilePath(String.join(STRING_COMMA,filePaths));
            }
        }

        return null;

    }

    @Override
    public boolean supportTask(AsyncTaskTypeEnum type) {
        return type.equals(ALL_LOG_EXPORT);
    }

}
