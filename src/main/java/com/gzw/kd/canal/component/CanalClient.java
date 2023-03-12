package com.gzw.kd.canal.component;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.thread.NamedThreadFactory;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.client.impl.ClusterCanalConnector;
import com.alibaba.otter.canal.client.impl.SimpleCanalConnector;
import com.alibaba.otter.canal.protocol.Message;
import com.gzw.kd.canal.adapter.MessageAdapter;
import com.gzw.kd.canal.bo.CanalData;
import com.gzw.kd.common.enums.ResultCodeEnum;
import com.gzw.kd.common.exception.GlobalException;
import com.gzw.kd.common.utils.CanalMessageUtil;
import com.gzw.kd.config.CanalConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.*;
import static com.gzw.kd.common.Constants.*;

@SuppressWarnings("all")
@RefreshScope
@Slf4j
@Component
public class CanalClient {

    private static final long local_thread_sleep = 1000L;
    @Autowired
    private CanalConfig canalConfig;

    @Autowired
    private CanalClientAdapterFactory canalClientAdapterFactory;

    private Map<String, MessageAdapter> adapters;

    private ExecutorService canalClientAdapterExecutorService;

    /** 是否运行中 */
    private volatile boolean running = false;
    private Thread canalClientThread = null;
    private Thread.UncaughtExceptionHandler handler = (t, e) -> log.error("parse events has an error", e);

    private CanalConnector connector;

    public CanalClient() {
    }

    @PostConstruct
    public void init() {
        // canal client启动，判断是否为集群
        SocketAddress socketAddress = null;
        if (StrUtil.isNotBlank(canalConfig.getServerHost())) {
            String[] ipPort = canalConfig.getServerHost().split(STRING_COLON);
            socketAddress = new InetSocketAddress(ipPort[INT_ZERO], Integer.parseInt(ipPort[INT_ONE]));
        }

        if (ObjectUtil.isNotNull(socketAddress)) {
            connector = CanalConnectors.newSingleConnector(socketAddress, canalConfig.getInstance(), STRING_EMPTY, STRING_EMPTY);
        } else {
            connector = CanalConnectors.newClusterConnector(
                    canalConfig.getZookeeperHosts(),
                    canalConfig.getInstance(),
                    canalConfig.getUsername(),
                    canalConfig.getPassword());
            ((ClusterCanalConnector) connector).setSoTimeout(canalConfig.getTimeout());
        }

        // 从spring容器获取实现了MessageAdapter接口的所有适配器实例类
        adapters = canalClientAdapterFactory.getMessageAdapters();

        // 设置批处理数量
        adapters.values().forEach(e -> e.setBatchSize(canalConfig.getBatchSize()));

        int size = adapters.size();

        canalClientAdapterExecutorService = new ThreadPoolExecutor(
                size,
                size,
                500L,
                TimeUnit.MILLISECONDS,
                new SynchronousQueue<>(),
                new NamedThreadFactory("canal-adapter-pool-", false),
                (r, exe) -> {
                    if (!exe.isShutdown()) {
                        try {
                            exe.getQueue().put(r);
                        } catch (InterruptedException e) {
                            // ignore
                        }
                    }
                });

        // 启动
        start();
    }

    @PreDestroy
    public void stop() {
        try {
            if (!running) {
                return;
            }

            if (connector instanceof ClusterCanalConnector) {
                ((ClusterCanalConnector) connector).stopRunning();
            } else if (connector instanceof SimpleCanalConnector) {
                ((SimpleCanalConnector) connector).stopRunning();
            }

            running = false;

            log.info("instance {} is waiting for adapters' worker thread die!", canalConfig.getInstance());

            if (canalClientThread != null) {
                try {
                    canalClientThread.join();
                } catch (InterruptedException e) {
                    // ignore
                }
            }
            canalClientAdapterExecutorService.shutdown();
            log.info("instance {} adapters worker thread dead!", canalConfig.getInstance());
            adapters.values().forEach(MessageAdapter::destroy);
            log.info("instance {} all adapters destroyed!", canalConfig.getInstance());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public void start() {
        if (!running) {
            canalClientThread = new Thread(this::process);
            canalClientThread.setName("canalClient-thread");
            canalClientThread.setUncaughtExceptionHandler(handler);
            canalClientThread.start();
            running = true;
        }
    }

    private void process() {
        // waiting until running == true
        while (!running) {
            try {
                Thread.sleep(local_thread_sleep);
            } catch (InterruptedException e) {
            }
        }

        int retry = Optional.ofNullable(canalConfig.getRetries()).orElse(INT_ZERO);
        if (log.isDebugEnabled()) {
            log.debug("loaded retry {} from config file.", retry);
        }
        if (retry == INT_NEGATIVE_ONE) {
            // 重试次数-1代表异常时一直阻塞重试
            retry = Integer.MAX_VALUE;
        } else {
            // 第一次请求不算retry所以配置值加1
            retry++;
        }

        try {
            log.info("=============> Start to connect destination: {} <=============", canalConfig.getInstance());
            connector.connect();
            log.info("=============> Start to subscribe destination: {} <=============", canalConfig.getInstance());
            connector.subscribe();
            log.info("=============> Subscribe destination: {} succeed <=============", canalConfig.getInstance());

            while (running) {
                for (int i = 0; i < retry; i++) {
                    if (!running) {
                        break;
                    }
                    Message message = connector.getWithoutAck(canalConfig.getBatchSize());
                    long batchId = message.getId();
                    try {
                        int size = message.getEntries().size();
                        if (batchId == INT_NEGATIVE_ONE || size == INT_ZERO) {
                            Thread.sleep(local_thread_sleep);
                        } else {
                            if (log.isDebugEnabled()) {
                                log.debug("instance: {} batchId: {} batchSize: {} ",
                                        canalConfig.getInstance(),
                                        batchId,
                                        size);
                            }
                            long begin = System.currentTimeMillis();
                            writeOut(message);
                            if (log.isDebugEnabled()) {
                                log.debug("instance: {} batchId: {} elapsed time: {} ms",
                                        canalConfig.getInstance(),
                                        batchId,
                                        System.currentTimeMillis() - begin);
                            }
                        }
                        // 提交确认
                        connector.ack(batchId);
                        break;
                    } catch (Throwable e) {
                        if (i != (retry - INT_ONE)) {
                            // 处理失败, 回滚数据
                            connector.rollback(batchId);
                            log.error(e.getMessage() + " Error sync and rollback, execute times: " + (i + INT_ONE));
                        } else {
                            connector.rollback(batchId);
                            log.error(e.getMessage() + " Error sync but ACK!");
                        }
                        Thread.sleep(canalConfig.getInterval());
                    }
                }
            }
        } catch (InterruptedException e) {
            log.error("while thread sleeping has been interrupted!", e);
        } catch (Throwable e) {
            log.error("process error!", e);
        } finally {
            connector.disconnect();
            log.info("=============> Disconnect instance: {} <=============", canalConfig.getInstance());
        }

    }

    private void writeOut(final Message message) {

        // 根据数据各类分类适配类
        List<CanalData> dataList = CanalMessageUtil.parseCanalData(canalConfig.getInstance(), message);
        if (CollUtil.isEmpty(dataList)) {
            if (log.isDebugEnabled()) {
                log.debug("empty canal data list skip adapter process.");
            }
            return;
        }

        if (log.isDebugEnabled()) {
            log.debug("received canal data list: {}", JSONUtil.toJsonStr(dataList));
        }

        dataList.stream().forEach(data -> {
            String schema = data.getDatabase() + STRING_DOT + data.getTable();
            List<String> destList = canalConfig.getDestinations().get(schema);
            if (CollUtil.isNotEmpty(destList)) {
                String [] dest = destList.stream().toArray(String[]::new);
                String target = dest[INT_ZERO].toLowerCase();
                adapters.get(target).addCanalData(String.join(",",dest), schema, data);
            } else {
                log.warn("Received schema {} did not config, ignored it.", schema);
            }
        });

        List<Future<Boolean>> futures = new ArrayList<>();
        adapters.values().forEach(adapter -> {
            futures.add(canalClientAdapterExecutorService.submit(() -> {
                try {
                    long begin = System.currentTimeMillis();
                    adapter.sync();
                    if (log.isDebugEnabled()) {
                        log.debug("{} elapsed time: {}",
                                adapter.getClass().getName(),
                                (System.currentTimeMillis() - begin));
                    }
                    return true;
                } catch (Exception e) {
                    adapter.reset();
                    log.error(e.getMessage(), e);
                    return false;
                }
            }));
        });

        // 等待所有适配器写入完成
        // 由于是并发操作，所以将阻塞直到耗时最久的工作组操作完成
        RuntimeException exception = null;
        for (Future<Boolean> future : futures) {
            try {
                if (!future.get()) {
                    exception = new GlobalException(ResultCodeEnum.UNKNOWN_ERROR);
                }
            } catch (Exception e) {
                exception = new GlobalException(ResultCodeEnum.UNKNOWN_ERROR.getCode(), e.getMessage(), e);
            }
        }
        if (exception != null) {
            throw exception;
        }
    }
}
