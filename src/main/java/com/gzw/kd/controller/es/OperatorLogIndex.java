package com.gzw.kd.controller.es;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.gzw.kd.common.enums.ResultCodeEnum;
import com.gzw.kd.common.exception.GlobalException;
import com.gzw.kd.vo.input.OperatorLogInput;
import com.gzw.kd.vo.output.EsLogSearchIndex;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
/**
 * @author gzw
 * @description： log index
 * @since：2023/3/2 19:59
 */
@Slf4j
@Component
@SuppressWarnings("all")
public class OperatorLogIndex {

    @Resource
    private RestHighLevelClient restHighLevelClient;

    @Value("${operatorLog.index:log_data_all}")
    private String logIndex;


    @Value("${es.query.default.size}")
    private  int defaultSize  ;


    /**
     * 获取操作日志信息
     * @param whereLog  where log
     * @return list
     */
    public List<EsLogSearchIndex> getAllLog(OperatorLogInput whereLog) throws GlobalException {
        List<EsLogSearchIndex> esLogSearchIndexList = new ArrayList<>();
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        if(StringUtils.isNotBlank(whereLog.getUserName())){
            queryBuilder.must(QueryBuilders.termQuery("username", whereLog.getUserName()));
        }
        if(StringUtils.isNotBlank(whereLog.getLocation())){
            queryBuilder.must(QueryBuilders.termQuery("location", whereLog.getLocation()));
        }
        if(StringUtils.isNotBlank(whereLog.getCreateTimeStart()) && StringUtils.isNotBlank(whereLog.getCreateTimeEnd())){
            queryBuilder.must(QueryBuilders.rangeQuery("create_time")
                    .from(whereLog.getCreateTimeStart())
                    .to(whereLog.getCreateTimeEnd())
                    );
        }
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        int size = whereLog.getSize() == 0 ? defaultSize:whereLog.getSize();
        sourceBuilder.query(queryBuilder).size(size).sort("create_time", SortOrder.DESC);
        // 高亮查询
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.postTags("</span>");
        highlightBuilder.requireFieldMatch(false);
        highlightBuilder.field("*");
        sourceBuilder.highlighter(highlightBuilder);
        SearchRequest searchRequest = new SearchRequest(logIndex).source(sourceBuilder);
        SearchResponse response;
        try {
            response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHit[] searchHitArr = response.getHits().getHits();
            for (SearchHit searchHit : searchHitArr) {
                String source = searchHit.getSourceAsString();
                EsLogSearchIndex esLogSearchIndex = JSONObject.parseObject(source, EsLogSearchIndex.class);
                esLogSearchIndexList.add(esLogSearchIndex);
            }
        } catch (Throwable e) {
            log.error("es查询操作日志信息异常:[{}]",e.getMessage(),e);
            throw new GlobalException("es查询操作日志信息异常["+e.getMessage()+"]", ResultCodeEnum.UNKNOWN_ERROR.getCode());
        }
        return esLogSearchIndexList;
    }

    /**
     * save
     * @param saveLog save
     * @return true/false
     */

    public boolean save(OperatorLogInput saveLog) {
        try {
            Map<String, Object> source = getMap(saveLog);
            IndexRequest request = Requests.indexRequest(logIndex).source(source).id(String.valueOf(source.get("id")));
            IndexResponse response;
            response = restHighLevelClient.index(request, RequestOptions.DEFAULT);
            boolean b = response.status().getStatus() != RestStatus.OK.getStatus();
            if (b) {
                log.info("会话数据写入es成功=[{}]", JSON.toJSONString(saveLog));
                return true;
            }
        } catch (Exception e) {
            log.error("es记录日志数据失败日志 id:{},错误:{}", saveLog.getId(), e.getMessage(), e);
            throw new GlobalException("es保存操作日志信息异常 id: [" + saveLog.getId() + "]+ message:[" + e.getMessage() + "]", ResultCodeEnum.UNKNOWN_ERROR.getCode());
        }
        log.info("日志数据写入es失败=[{}]", JSON.toJSONString(saveLog));
        return false;
    }

    /**
     * 批量插入es
     * @param msgList msgList
     */
    public boolean saveBatch(List<OperatorLogInput> msgList) {
        try {
            BulkRequest bulkRequest = new BulkRequest();
            for (OperatorLogInput bo : msgList) {
                Map<String, Object> source = getMap(bo);
                IndexRequest indexRequest = Requests.indexRequest(logIndex).source(source).id(String.valueOf(source.get("id")));
                bulkRequest.add(indexRequest);
            }
            BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
            log.info("会话数据批量写入es成功,data=[{}], 返回状态=[{}]", JSON.toJSONString(msgList), bulk.status());
            return true;
        } catch (Exception e) {
            log.error("es记录日志数据批量写入失败异常,错误:{}", e.getMessage(), e);
            throw new GlobalException("es写入操作日志信息异常[" + e.getMessage() + "]", ResultCodeEnum.UNKNOWN_ERROR.getCode());
        }
    }

    private Map<String, Object> getMap(OperatorLogInput operatorLogInput) {
        Map<String, Object> source = new HashMap<>(8);
        source.put("id", operatorLogInput.getId());
        source.put("username", operatorLogInput.getUserName());
        source.put("desc", operatorLogInput.getDesc());
        source.put("operation", operatorLogInput.getOperation());
        source.put("time", operatorLogInput.getTime());
        source.put("ip", operatorLogInput.getIp());
        source.put("create_time", operatorLogInput.getCreateTime());
        source.put("location", operatorLogInput.getLocation());
        source.put("result", operatorLogInput.getResult());
        return source;
    }
}
