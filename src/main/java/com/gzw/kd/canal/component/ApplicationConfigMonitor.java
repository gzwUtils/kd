package com.gzw.kd.canal.component;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ClassUtil;
import java.nio.file.Files;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.springframework.cloud.context.refresh.ContextRefresher;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author 高志伟
 */
@Slf4j
@Component
public class ApplicationConfigMonitor {

    /** 重新加载配置文件间隔 */
    private static final long RELOAD_INTERVAL = 5000L;

    @Resource
    private ContextRefresher contextRefresher;

    @Resource
    private CanalClient canalClient;

    private FileAlterationMonitor fileMonitor;

    @PostConstruct
    public void init() {
        File classpath = FileUtil.file(ClassUtil.getClassPath());
        try {
            FileAlterationObserver observer = new FileAlterationObserver(classpath,
                    FileFilterUtils.and(FileFilterUtils.fileFileFilter(),
                            FileFilterUtils.prefixFileFilter("application-dev"),
                            FileFilterUtils.suffixFileFilter("properties")));
            FileListener listener = new FileListener();
            observer.addListener(listener);
            fileMonitor = new FileAlterationMonitor(3000, observer);
            fileMonitor.start();

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @PreDestroy
    public void destroy() {
        try {
            fileMonitor.stop();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private class FileListener extends FileAlterationListenerAdaptor {

        @Override
        public void onFileChange(File file) {
            super.onFileChange(file);
            try {
                // 检查yml格式
                new Yaml().loadAs(new InputStreamReader(Files.newInputStream(file.toPath()), StandardCharsets.UTF_8), Map.class);

                canalClient.stop();

                // refresh context
                contextRefresher.refresh();

                try {
                    Thread.sleep(RELOAD_INTERVAL);
                } catch (InterruptedException e) {
                    // ignore
                }
                canalClient.init();
                log.info("## adapter application config reloaded.");
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }
}
