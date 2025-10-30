package com.gzw.kd.cache;

import com.gzw.kd.common.utils.ApplicationContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CacheRefreshService implements CacheRefreshApi {

    /** 全局锁，防止同时触发多次全量刷新 */
    private final Lock globalLock = new ReentrantLock();

    @Override
    public RefreshResult refreshOne(String beanName) {
        AbstractCache cache = ApplicationContextUtils.getApplicationContext().getBean(beanName, AbstractCache.class);
        return doReload(cache);
    }

    @Override
    public List<RefreshResult> refreshAll() {
        globalLock.lock();
        try {
            Map<String, AbstractCache> map =  ApplicationContextUtils.getApplicationContext().getBeansOfType(AbstractCache.class);
            List<CompletableFuture<RefreshResult>> futures = new ArrayList<>();
            for (AbstractCache cache : map.values()) {
                futures.add(CompletableFuture.supplyAsync(() -> doReload(cache)));
            }
            return futures.stream()
                    .map(CompletableFuture::join)
                    .collect(Collectors.toList());
        } finally {
            globalLock.unlock();
        }
    }

    /* ============== 私有 ============== */

    private RefreshResult doReload(AbstractCache cache) {
        long start = System.currentTimeMillis();
        String name = cache.getClass().getSimpleName();
        try {
            cache.reload();
            long cost = System.currentTimeMillis() - start;
            log.info("【CacheRefresh】{} 刷新成功，耗时 {} ms", name, cost);
            return RefreshResult.builder()
                    .beanName(name)
                    .success(true)
                    .msg("ok")
                    .cost(cost)
                    .build();
        } catch (Exception e) {
            long cost = System.currentTimeMillis() - start;
            log.error("【CacheRefresh】{} 刷新失败，耗时 {} ms", name, cost, e);
            return RefreshResult.builder()
                    .beanName(name)
                    .success(false)
                    .msg(e.getMessage())
                    .cost(cost)
                    .build();
        }
    }
}