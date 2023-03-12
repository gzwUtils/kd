package com.gzw.kd.canal.adapter;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONObject;
import com.gzw.kd.canal.bo.CanalData;
import com.gzw.kd.canal.service.RedisSyncService;
import java.util.List;
import javax.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import static com.gzw.kd.common.Constants.INT_ONE;
import static com.gzw.kd.common.Constants.INT_ZERO;

/**
 * redis 适配器
 * @author gaozhiwei
 */
@Slf4j
@Component
public class RedisAdapter implements MessageAdapter {

    @Resource
    private final RedisSyncService redisSyncService;

    /** 批处理数据 */
    private int batchSize;

    /** 数据集 */
    private List<List<RedisAdapterUnit>> dataList = CollUtil.newArrayList();

    /** 数据集中的最后一集合的索引位置 */
    private int lastIndex = INT_ZERO;

    /** 数据集中的最后一集合的数量 */
    private int lastSize = INT_ZERO;

    public RedisAdapter(RedisSyncService redisSyncService) {
        this.redisSyncService = redisSyncService;
    }

    @Override
    public void addCanalData(String destination, String schema, CanalData canalData) {
        lastIndex = (lastSize / batchSize);
        if ((lastSize % batchSize) == INT_ZERO) {
            lastSize = INT_ONE;
            dataList.add(CollUtil.newArrayList());
        }
        dataList.get(lastIndex).add(new RedisAdapterUnit(schema, canalData));
        lastSize++;
    }

    @Override
    public String getName() {
        return REDIS;
    }

    @Override
    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    @Override
    public void sync() {
        try {
            // 按分批同步
            dataList.forEach(list -> {
                list.forEach(unit -> {
                    String schema = unit.getSchema();
                    CanalData canalData = unit.getCanalData();
                    List<JSONObject> jsons = MessageAdapter.extractJson(canalData);

                    // 分配删除处理数据列表
                    if (CanalData.Type.DELETE == canalData.getType()) {
                        redisSyncService.addToBeDelete(schema, jsons);
                    }
                    // 分配插入更新处理数据列表
                    else {
                        redisSyncService.addToUpsert(schema, jsons);
                    }
                });
                // 触发处理
                redisSyncService.fire();
            });
        } finally {
            // 清空数据集
            redisSyncService.reset();
            reset();
        }
    }

    @Override
    public void destroy() {
        // NOTHING
    }

    @Override
    public void reset() {
        dataList = null;
        dataList = CollUtil.newArrayList();
        lastIndex = INT_ZERO;
        lastSize = INT_ZERO;
    }

    @Data
    @AllArgsConstructor
    private static class RedisAdapterUnit {
        private String schema;
        private CanalData canalData;
    }
}
