package com.gzw.kd.canal.adapter;

import cn.hutool.json.JSONObject;
import com.gzw.kd.canal.bo.CanalData;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 数据适配器接口
 * @author gaozhiwei
 */
public interface MessageAdapter {

    String ELASTICSEARCH = "elasticsearch";
    String REDIS = "redis";

    /**
     * 返回当前适配器名
     * @return 当前适配器名
     */
    String getName();

    /**
     * 设置批处理数量
     * @param batchSize 批处理数量
     */
    void setBatchSize(int batchSize);

    /** 触发数据同步处理 */
    void sync();

    /** 重置数据集 */
    void reset();

    /** 销毁处理 */
    void destroy();

    /**
     * 传递canal 数据，内部数据集为列表嵌套格式，以批数量为单位作为一个子列表
     * @param destination 数据目标
     * @param schema 数据表名
     * @param canalData canal数据
     */
    void addCanalData(String destination, String schema, CanalData canalData);

    /**
     * 转换canal数据集为json列表
     * @param canalData canal数据集
     * @return json列表
     */
    static List<JSONObject> extractJson(CanalData canalData) {
        return canalData.getData().stream()
                .map(row -> new JSONObject(row, false))
                .collect(Collectors.toList());
    }
}
