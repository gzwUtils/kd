package com.gzw.kd.export.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.gzw.kd.common.enums.AsyncTaskTypeEnum;
import com.gzw.kd.common.enums.EnumUtils;
import com.gzw.kd.common.enums.ResultCodeEnum;
import com.gzw.kd.common.exception.GlobalException;
import com.gzw.kd.common.utils.EsScrollIterable;
import com.gzw.kd.export.AsyncTaskService;
import com.gzw.kd.export.BigExcelWriterNew;
import com.gzw.kd.export.ExportFieldHandlerRegistry;
import com.gzw.kd.export.ExportSheet;
import com.gzw.kd.mapper.LogEsMapper;
import com.gzw.kd.mapper.LogMapper;
import com.gzw.kd.service.CommonExportService;
import com.gzw.kd.vo.input.LogSearchInput;
import com.gzw.kd.vo.output.AsyncTaskOutput;
import com.gzw.kd.vo.output.LogExportOutput;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import static com.gzw.kd.common.Constants.*;

@SuppressWarnings("all")
@Slf4j
@Service
public class LogExportAsyncTask implements AsyncTaskService, CommonExportService {

    @Value("${system.startEsQuery}")
    private boolean startEsQuery;

    @Value("${bio.uploadPath}")
    private  String basedir;

    @Resource
    private final ExportFieldHandlerRegistry exportFieldHandlerRegistry;

    private final LogEsMapper logEsMapper;
    private final LogMapper logMapper;

    private static final long EXPORT_ROWS_LIMIT = 50_000L;

    public LogExportAsyncTask(ExportFieldHandlerRegistry exportFieldHandlerRegistry,
                              LogEsMapper logEsMapper,
                              LogMapper logMapper) {
        this.exportFieldHandlerRegistry = exportFieldHandlerRegistry;
        this.logEsMapper = logEsMapper;
        this.logMapper = logMapper;
    }

    @Override
    public AsyncTaskOutput processAsyncTask(String params, String creator, LocalDateTime createTime, String fileName) {
        LogSearchInput input = JSON.parseObject(params, LogSearchInput.class);

        if (input == null) {
            return null;
        }
        input.setUserName(creator);
        if (startEsQuery) {
            // ES查询导出
            return exportFromEs(input, fileName);
        } else {
            // 数据库查询导出
            return exportFromDatabase(input, fileName);
        }
    }

    /**
     * ES查询导出
     */
    private AsyncTaskOutput exportFromEs(LogSearchInput input, String fileName) {
        final SearchResponse scrollResponse = logEsMapper.scrollBegin(input);
        final long dataRowCount = scrollResponse.getHits().getTotalHits().value;

        if (dataRowCount > EXPORT_ROWS_LIMIT) {
            throw new GlobalException("The number of exported leads has exceeded the limit, " +
                    "limit is " + EXPORT_ROWS_LIMIT, ResultCodeEnum.UNKNOWN_ERROR.getCode());
        }

        if (dataRowCount <= 0) {
            return new AsyncTaskOutput()
                    .setFileName(input.getExportFileName())
                    .setNumberOfSuccesses(0L)
                    .setNumberOfFailed(0L)
                    .setFilePath("");
        }

        // 计算需要生成的excel文件个数
        int exportFileNum = calcExportFileNum(dataRowCount);

        // ES scroll 迭代器
        EsScrollIterable iterable = new EsScrollIterable(
                logEsMapper.scrollBegin(input), logEsMapper::scrollByPage);
        Iterator<List<Map<String, Object>>> itr = iterable.iterator();

        List<String> filePaths = exportToExcelFiles(fileName, exportFileNum, itr, input.getExportFields());

        // 清除scroll context
        logEsMapper.scrollClear(iterable.getLastScrollId());

        return new AsyncTaskOutput()
                .setFileName(input.getExportFileName())
                .setNumberOfSuccesses(dataRowCount)
                .setNumberOfFailed(0L)
                .setFilePath(String.join(STRING_COMMA, filePaths));
    }

    /**
     * 数据库查询导出
     */
    private AsyncTaskOutput exportFromDatabase(LogSearchInput input, String fileName) {
        // 查询数据库数据
        List<LogExportOutput> logInfo = logMapper.getLogInfo(input);

        if (CollUtil.isEmpty(logInfo)) {
            return new AsyncTaskOutput()
                    .setFileName(input.getExportFileName())
                    .setNumberOfSuccesses(0L)
                    .setNumberOfFailed(0L)
                    .setFilePath("");
        }

        long dataRowCount = logInfo.size();

        if (dataRowCount > EXPORT_ROWS_LIMIT) {
            throw new GlobalException("The number of exported leads has exceeded the limit, " +
                    "limit is " + EXPORT_ROWS_LIMIT, ResultCodeEnum.UNKNOWN_ERROR.getCode());
        }

        // 计算需要生成的excel文件个数
        int exportFileNum = calcExportFileNum(dataRowCount);

        // 将对象列表转换为excel数据列表
        List<List<Map<String, Object>>> list = new ArrayList<>(logInfo.size());
        for (LogExportOutput data : logInfo) {
            list.add(CollUtil.newArrayList(Collections.singleton(JSONUtil.parseObj(data))));
        }

        // 由于数据量不大，我们可以一次性处理
        // 创建分页迭代器
        Iterator<List<Map<String, Object>>> itr = createPagedIterator(list, 5000); // 每批5000条

        List<String> filePaths = exportToExcelFiles(fileName, exportFileNum, itr, input.getExportFields());

        return new AsyncTaskOutput()
                .setFileName(input.getExportFileName())
                .setNumberOfSuccesses(dataRowCount)
                .setNumberOfFailed(0L)
                .setFilePath(String.join(STRING_COMMA, filePaths));
    }

    /**
     * 创建分页迭代器（用于数据库查询结果）
     */
    private Iterator<List<Map<String, Object>>> createPagedIterator(List<List<Map<String, Object>>> dataList, int pageSize) {
        return new Iterator<List<Map<String, Object>>>() {
            private int currentIndex = 0;

            @Override
            public boolean hasNext() {
                return currentIndex < dataList.size();
            }

            @Override
            public List<Map<String, Object>> next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }

                int fromIndex = currentIndex;
                int toIndex = Math.min(currentIndex + pageSize, dataList.size());
                List<Map<String, Object>> batch = new ArrayList<>();

                for (int i = fromIndex; i < toIndex; i++) {
                    if (!dataList.get(i).isEmpty()) {
                        batch.addAll(dataList.get(i));
                    }
                }

                currentIndex = toIndex;
                return batch;
            }
        };
    }

    /**
     * 导出数据到Excel文件
     */
    private List<String> exportToExcelFiles(String fileName, int exportFileNum,
                                            Iterator<List<Map<String, Object>>> iterator,
                                            List<String> exportFields) {
        List<String> filePaths = new ArrayList<>(exportFileNum);

        for (int i = 0; i < exportFileNum; ++i) {
            final String fileNameAndSuffix = fileName + STRING_UNDERLINE + IdUtil.fastSimpleUUID() + XLSX_EXPORT_FILE_SUFFIX;
            final String filePath = storagePath(basedir,fileNameAndSuffix);
            log.info("async file path.....{}", filePath);

            BigExcelWriterNew writer = new BigExcelWriterNew(filePath, fileName + STRING_UNDERLINE + i);

            try {
                // 单个文件最多查询10个批次, 即最多50_000条数据
                int batchSize = 10;
                while (batchSize-- > 0 && iterator.hasNext()) {
                    List<Map<String, Object>> dataSet = iterator.next();

                    ExportSheet currentSheet = new ExportSheet(
                            Collections.singletonList(dataSet),
                            exportFieldHandlerRegistry.getTest(),
                            exportFields.toArray(new String[0]));

                    if (batchSize == 9) { // 第一个批次写表头
                        currentSheet.write2File(writer);
                    } else {
                        currentSheet.write2FileWithoutHeader(writer);
                    }
                }
            } finally {
                // 确保关闭文件流
                writer.close();
            }

            // 记录本次的文件存储路径
            filePaths.add(filePath);
        }

        return filePaths;
    }

    /**
     * 计算需要生成的文件数量
     */
    public int calcExportFileNum(long totalRows) {
        if (totalRows <= 0) {
            return 0;
        }
        // 每个文件最多50,000行
        int fileNum = (int) (totalRows / EXPORT_ROWS_LIMIT);
        if (totalRows % EXPORT_ROWS_LIMIT > 0) {
            fileNum++;
        }
        return fileNum;
    }

    @Override
    public boolean supportTask(AsyncTaskTypeEnum type) {
        return type.equals(EnumUtils.getEnumByCode(type.getCode(),AsyncTaskTypeEnum.class));
    }
}