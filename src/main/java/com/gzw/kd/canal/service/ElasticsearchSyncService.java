package com.gzw.kd.canal.service;

import cn.hutool.json.JSONObject;

import java.util.List;

/**
 *
 * @author gaozhiwei
 */
@SuppressWarnings("all")
public interface ElasticsearchSyncService {

    void addToBeDelete(String schema, String index, String type, List<JSONObject> jsons);

    void addToBeUpsert(String schema, String index, String type, List<JSONObject> jsons);

    void fire();

    void reset();
}
