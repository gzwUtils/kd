package com.gzw.kd.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.system.SystemUtil;
import com.gzw.kd.export.BigExcelWriterNew;
import com.gzw.kd.export.ExportSheet;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;
import static com.gzw.kd.common.Constants.XLSX_MAX_EXPORT_ROW_SIZE;

/**
 * 公共导出服务类
 *
 * @author gzw
 */
@SuppressWarnings("all")
public interface CommonExportService {

    /** 本地临时路径，末尾自带斜杠 */
    String FILE_STORAGE_PATH = StrUtil.addSuffixIfNot(SystemUtil.get(SystemUtil.TMPDIR), StrUtil.SLASH);

    String SUPPORT_FILE_SUFFIX_PATTERN = "^(.+\\.xlsx)$";

    /**
     * 文件导出
     *
     * @param exportSheets      文件的sheet配置
     * @param fileNameAndSuffix 文件名
     * @return 文件存储路径
     */
    default String export(List<ExportSheet> exportSheets, String fileNameAndSuffix) {

        if (!ReUtil.isMatch(SUPPORT_FILE_SUFFIX_PATTERN, fileNameAndSuffix)) {
            throw new IllegalArgumentException("Unsupported file export type");
        }

        final String storagePath = defaultStoragePath(fileNameAndSuffix);
        // 删除已经存在的文件
        FileUtil.del(storagePath);
        BigExcelWriterNew writer = null;

        for (ExportSheet s : exportSheets) {
            // 设置默认sheetName
            if (Objects.isNull(writer)) {
                writer = new BigExcelWriterNew(storagePath, s.getSheetName());
            }

            writer = writer.setSheet(s.getSheetName());

            // 分批写入数据
            s.write2File(writer);
        }

        if (null != writer) {
            // 关闭文件流
            writer.close();
        }

        return storagePath;
    }

    /**
     * 默认的文件存储路径
     *
     * @param fileNameAndSuffix 文件名
     * @return 文件存储路径
     */
    default String defaultStoragePath(String fileNameAndSuffix) {
        return FILE_STORAGE_PATH + fileNameAndSuffix;
    }

    /**
     * 指定文件存储路径
     *
     * @param fileNameAndSuffix 文件名
     * @return 文件存储路径
     */
    default String storagePath(String path,String fileNameAndSuffix) {

        // 生成日期目录
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateDir = dateFormat.format(System.currentTimeMillis());
        String projectPath = System.getProperty("user.dir");
        // 完整目录路径
        String fullDir = projectPath +File.separator + path + File.separator + dateDir;
        FileUtil.mkdir(fullDir);

        // 生成时间前缀的文件名
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        String timePrefix = timeFormat.format(System.currentTimeMillis());

        return fullDir + File.separator + timePrefix + "_" + fileNameAndSuffix;
    }


    /**
     * 根据查询结果计算文件数量
     *
     * @param totalHits es查询结果
     * @return 文件数
     */
    default int calcExportFileNum(final long totalHits) {
        int roughlyFileNum = (int) (totalHits / XLSX_MAX_EXPORT_ROW_SIZE);
        return totalHits % XLSX_MAX_EXPORT_ROW_SIZE > 0 ? roughlyFileNum + 1 : roughlyFileNum;
    }
}
