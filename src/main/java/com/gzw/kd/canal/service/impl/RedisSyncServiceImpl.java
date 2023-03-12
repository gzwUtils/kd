package com.gzw.kd.canal.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import java.util.List;
import java.util.concurrent.TimeUnit;
import com.gzw.kd.canal.service.RedisSyncService;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import static com.gzw.kd.common.Constants.*;

/**
 * gaozhiwei
 * @since  2022-06-27
 */
@Slf4j
@Service
@SuppressWarnings("all")
public class RedisSyncServiceImpl implements RedisSyncService {

    private static final String LOG_KEY_UPSERT = "upsert";
    private static final String LOG_KEY_DELETE = "delete";

    @Autowired
    private RedisTemplate<String, String> redisTemplate;


    private List<RedisSyncUnit> redisSyncUnits = CollUtil.newArrayList();

    @Override
    public void addToBeDelete(String schema, List<JSONObject> jsons) {
        redisSyncUnits.add(new RedisSyncUnit().setSchema(schema).setToBeDelete(jsons));
    }

    @Override
    public void addToUpsert(String schema, List<JSONObject> jsons) {
        redisSyncUnits.add(new RedisSyncUnit().setSchema(schema).setToBeUpdate(jsons));
    }

    @Override
    public void fire(){
        try {
            redisSyncUnits.stream().forEach(unit -> {
                String schema = unit.getSchema();
                unit.getToBeUpdate().stream().forEach(json -> {
                    //表处理
                     if (LEARN_SCHEMA_TEST_LEARN.equalsIgnoreCase(schema)) {
                        if (!json.containsKey(COL_ID)) {
                            log.warn("upsert sync schema {} not contains field  id", schema);
                            return;
                        }
                        String idStr = json.getStr(COL_ID);
                        if (StrUtil.isBlank(idStr)) {
                            log.warn("upsert sync schema {} field id {} is blank data", schema, idStr);
                            return;
                        }
                         String key = getRedisLearnKey(idStr);
                         log.info("upsert sync schema {} field,json{}", schema, json.toString());
                        redisTemplate.opsForValue().set(key, json.toString());
                    } else {
                        log.warn("upsert sync ignored data {}", json.toString());
                    }
                });
                // 删除处理
                unit.toBeDelete.stream().forEach(json -> {
                    long random = RandomUtil.randomLong(LONG_ZERO, LONG_THOUSAND);
                    //表处理
                     if (LEARN_SCHEMA_TEST_LEARN.equalsIgnoreCase(schema)) {
                        if (!json.containsKey(COL_ID) || !json.containsKey(COL_ID)) {
                            log.warn("delete sync schema {} not contains field activities_id or id", schema);
                            return;
                        }
                        String idStr = json.getStr(COL_ID);
                        if (StrUtil.isBlank(idStr)) {
                            log.warn("delete sync schema {} field id {} is blank data", schema, idStr);
                            return;
                        }
                        String key = getRedisLearnKey(idStr);
                        redisTemplate.expire(key, random, TimeUnit.MILLISECONDS);
                    }
                });
            });
        } catch (Exception e) {
            log.error("Redis执行同步失败{}",e.getMessage(),e);
        }

        reset();
    }

    @Override
    public void reset() {
        redisSyncUnits = null;
        redisSyncUnits = CollUtil.newArrayList();
    }

    @Data
    @Accessors(chain = true)
    private static class RedisSyncUnit {
        private String schema;
        private List<JSONObject> toBeDelete = CollUtil.newArrayList();
        private List<JSONObject> toBeUpdate = CollUtil.newArrayList();
    }
}
