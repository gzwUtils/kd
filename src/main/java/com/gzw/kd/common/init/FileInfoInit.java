package com.gzw.kd.common.init;

import com.github.benmanes.caffeine.cache.Cache;
import com.gzw.kd.cache.AbstractCache;
import com.gzw.kd.common.Constants;
import com.gzw.kd.vo.output.FileOutput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
/**
 * 上传文件信息初始化
 *
 * @author gzw
 * @since 2023/5/15
 */
@Slf4j
@Order(3)
@Component
public class FileInfoInit extends AbstractCache {

    private static final String DOWNLOAD_PATH_PREFIX = "download?path=";
    private static final int FILENAME_PREFIX_LENGTH = 5;

    @Resource
    private Cache<String, Object> caffeineCache;

    @Value("${bio.uploadPath}")
    private  String baseDir;

    @Override
    protected void init() {
        try {
            String projectPath = System.getProperty("user.dir");
            String fullPath = projectPath + baseDir + File.separator;
            List<FileOutput> fileList = new ArrayList<>();
            scanFiles(new File(fullPath), fileList);
            fileList.sort((o1, o2) -> o2.getUploadTime().compareTo(o1.getUploadTime()));
            caffeineCache.put(Constants.UPLOAD_FILE, fileList);
            log.info("文件信息初始化成功，共加载 {} 个文件", fileList.size());
        } catch (Exception e) {
            log.error("文件信息初始化失败", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get() {
        if (caffeineCache.getIfPresent(Constants.UPLOAD_FILE) == null) {
            log.warn("{} 配置缓存失效，重新加载", this.getClass().getSimpleName());
            reload();
        }
        return (T) caffeineCache.getIfPresent(Constants.UPLOAD_FILE);
    }

    @Override
    public void clear() {
        log.warn("{} 清除文件信息缓存", this.getClass().getSimpleName());
        caffeineCache.invalidate(Constants.UPLOAD_FILE); // 只清除自己模块的缓存
    }

    private void scanFiles(File dir, List<FileOutput> result) {
        if (!dir.exists() || !dir.isDirectory()) return;

        File[] files = dir.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isHidden()) continue;

            if (file.isDirectory()) {
                scanFiles(file, result);
            } else {
                Optional<FileOutput> output = buildFileOutput(file);
                output.ifPresent(result::add);
            }
        }
    }

    private Optional<FileOutput> buildFileOutput(File file) {
        try {
            String name = file.getName();
            if (name.length() < FILENAME_PREFIX_LENGTH) return Optional.empty();

            String parentName = Optional.ofNullable(file.getParentFile())
                    .map(File::getName)
                    .orElse("unknown");

            FileOutput output = new FileOutput()
                    .setAttachName(name.substring(FILENAME_PREFIX_LENGTH+1))
                    .setAttachSize(String.valueOf(file.length()))
                    .setUploadTime(parentName + " " + name.substring(0, FILENAME_PREFIX_LENGTH))
                    .setAttachUrl(DOWNLOAD_PATH_PREFIX + parentName + "@" + name);

            return Optional.of(output);
        } catch (Exception e) {
            log.warn("解析文件信息失败: {}", file.getAbsolutePath(), e);
            return Optional.empty();
        }
    }
}