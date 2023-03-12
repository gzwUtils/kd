package com.gzw.kd.canal.adapter;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONObject;
import java.util.List;
import com.gzw.kd.canal.bo.CanalData;
import com.gzw.kd.canal.service.ElasticsearchSyncService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import static com.gzw.kd.common.Constants.*;

/**
 * ES适配器
 * @author gaozhiwei
 */
@Slf4j
@Component
public class ElasticsearchAdapter implements MessageAdapter {

    @Resource
    private final ElasticsearchSyncService elasticsearchSyncService;

    /** 批处理数据 */
    private int batchSize;

    /** 数据集 */
    private List<List<ElasticsearchAdapterUnit>> dataList = CollUtil.newArrayList();

    /** 数据集中的最后一集合的索引位置 */
    private int lastIndex = INT_ZERO;

    /** 数据集中的最后一集合的数量 */
    private int lastSize = INT_ZERO;

    public ElasticsearchAdapter(ElasticsearchSyncService elasticsearchSyncService) {
        this.elasticsearchSyncService = elasticsearchSyncService;
    }

    @Override
    public void addCanalData(String destination, String schema, CanalData canalData) {
        String[] split = destination.split(STRING_COMMA);
        String index = split[INT_ONE];
        String type = split[INT_TWO];

        lastIndex = (lastSize / batchSize);
        if ((lastSize % batchSize) == INT_ZERO) {
            lastSize = INT_ONE;
            dataList.add(CollUtil.newArrayList());
        }
        dataList.get(lastIndex).add(new ElasticsearchAdapterUnit(schema, index, type, canalData));
        lastSize++;
    }

    @Override
    public String getName() {
        return ELASTICSEARCH;
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
                    String index = unit.getIndex();
                    String type = unit.getType();
                    CanalData canalData = unit.getCanalData();
                    List<JSONObject> jsons = MessageAdapter.extractJson(canalData);

                    // 分配删除处理数据列表
                    if (CanalData.Type.DELETE == canalData.getType()) {
                        elasticsearchSyncService.addToBeDelete(schema, index, type, jsons);
                    }
                    // 分配插入更新处理数据列表
                    else {
                        elasticsearchSyncService.addToBeUpsert(schema, index, type, jsons);
                    }
                });
                // 触发处理
                elasticsearchSyncService.fire();
            });
        } finally {
            // 清空数据集
            elasticsearchSyncService.reset();
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
    private static class ElasticsearchAdapterUnit {
        private String schema;
        private String index;
        private String type;
        private CanalData canalData;
    }
}
