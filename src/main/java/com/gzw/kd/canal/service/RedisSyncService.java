package com.gzw.kd.canal.service;

import cn.hutool.json.JSONObject;
import com.gzw.kd.common.exception.GlobalException;

import java.util.List;

/**
 *
 * @author gaozhiwei
 */
@SuppressWarnings("all")
public interface RedisSyncService {

    /**
     * 删除
     * @param schema 模式
     * @param jsons  json
     */

    void addToBeDelete(String schema, List<JSONObject> jsons);

    /**
     * 新增 修改
     * @param schema 模式
     * @param jsons  json
     */

    void addToUpsert(String schema, List<JSONObject> jsons);

    void fire();

    void reset();
}
