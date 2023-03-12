package com.gzw.kd.canal.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import java.io.IOException;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import com.gzw.kd.canal.service.ElasticsearchSyncService;
import com.gzw.kd.common.Constants;
import com.gzw.kd.common.exception.GlobalException;
import com.gzw.kd.config.CanalConfig;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import static com.gzw.kd.common.Constants.*;
import static com.gzw.kd.common.enums.ResultCodeEnum.ERR_ES_DEL;
import static com.gzw.kd.common.enums.ResultCodeEnum.ERR_ES_UPS;

/**
 * @author gaozhiwei
 * @since 2020/5/12
 */
@Slf4j
@Service
@SuppressWarnings("all")
public class ElasticsearchSyncServiceImpl implements ElasticsearchSyncService {

    @Autowired
    private CanalConfig canalConfig;

    @Resource
    private RestHighLevelClient client;

    private List<EsSyncUnit> deleteList = CollUtil.newArrayList();
    private List<EsSyncUnit> upsertList = CollUtil.newArrayList();

    private String esIndexClueData = null;
    private String esTypeClueData = null;

    @PostConstruct
    public void init() {
        // 获取配置文件中表的索引名
        List<String> destList = canalConfig.getDestinations().get(Constants.SCHEMA_TEST);
        if (CollUtil.isNotEmpty(destList)) {
            for (String dest : destList) {
                String[] split = StringUtils.split(dest, STRING_COMMA);
                String target = split[INT_ZERO];
                String index = split[INT_ONE];
                String type = split[INT_TWO];
                if (Constants.TARGET_ELASTICSEARCH.equalsIgnoreCase(target)) {
                    esIndexClueData = index;
                    esTypeClueData = type;
                    break;
                }
            }
        }

    }

    @Override
    public void addToBeDelete(String schema, String index, String type, List<JSONObject> jsons) {
        deleteList.add(new EsSyncUnit().setSchema(schema).setIndex(index).setType(type).setToBeDelete(jsons));
    }

    @Override
    public void addToBeUpsert(String schema, String index, String type, List<JSONObject> jsons) {
        upsertList.add(new EsSyncUnit().setSchema(schema).setIndex(index).setType(type).setToBeUpdate(jsons));
    }

    @Override
    public void fire(){
        try {
            upsert();
            deleteByIds();
            reset();
        } catch (GlobalException e) {
            log.error("fire exception {}",e.getMessage(),e);
        }
    }

    private void deleteByIds() throws GlobalException {
        if (CollUtil.isNotEmpty(deleteList)) {
            boolean isDid = false;
            BulkRequest bulkReq = new BulkRequest();
            for (EsSyncUnit unit : deleteList) {
                for (JSONObject json : unit.getToBeDelete()) {
                    if (!json.containsKey(COL_ID)) {
                        log.warn("delete sync elasticsearch doc not contains field id");
                        return;
                    }
                    String id = json.getStr(COL_ID);
                    if (StrUtil.isBlank(id)) {
                        log.warn("delete sync elasticsearch doc field id {} is blank data", id);
                        return;
                    }
                    bulkReq.add(new DeleteRequest(unit.getIndex(), unit.getType(), id));

                    isDid = true;
                }
            }
            if (isDid) {
                try {
                    BulkResponse bulkResponse = client.bulk(bulkReq, RequestOptions.DEFAULT);
                    log.info(JSONUtil.toJsonStr(bulkResponse));
                    if (bulkResponse.hasFailures()) {
                        throw new GlobalException(bulkResponse.buildFailureMessage(),ERR_ES_DEL.getCode());
                    }
                } catch (IOException e) {
                    throw new GlobalException(ERR_ES_DEL.getCode(), e.getMessage(), e);
                }
            }
        }
    }

    private void upsert() throws GlobalException {
        if (CollUtil.isNotEmpty(upsertList)) {

            boolean isDid = false;
            BulkRequest bulkReq = new BulkRequest();
            bulkReq.timeout(TimeValue.MINUS_ONE);
            for (EsSyncUnit unit : upsertList) {
                for (JSONObject json : unit.getToBeUpdate()) {
                    String id = json.getStr(COL_ID);
                    //因有字段会被更新为null值的场景，直接传入json对象，会出现null值反序列化的异常，所以此处先行反序列为化json字符串
                    String data = json.toString();
                    UpdateRequest indexReq = new UpdateRequest(unit.getIndex(), unit.getType(), id)
                            .doc(data, XContentType.JSON)
                            .upsert(data, XContentType.JSON);
                    bulkReq.add(indexReq);
                    isDid = true;
                }
            }
            if (isDid) {
                try {
                    BulkResponse bulkRes = client.bulk(bulkReq, RequestOptions.DEFAULT);
                    if (bulkRes.hasFailures()) {
                        throw new GlobalException(bulkRes.buildFailureMessage(),ERR_ES_UPS.getCode());
                    }
                } catch (IOException e) {
                    throw new GlobalException(ERR_ES_UPS.getCode(), e.getMessage(), e);
                }
            }
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    @Override
    public void reset() {
        deleteList = null;
        upsertList = null;
        deleteList = CollUtil.newArrayList();
        upsertList = CollUtil.newArrayList();
    }

    @Data
    @Accessors(chain = true)
    private static class EsSyncUnit {
        private String schema;
        private String index;
        private String type;
        private List<JSONObject> toBeDelete = CollUtil.newArrayList();
        private List<JSONObject> toBeUpdate = CollUtil.newArrayList();
    }
}
