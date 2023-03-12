package com.gzw.kd.service.impl;
import com.gzw.kd.service.ElasticService;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author 高志伟
 */
@SuppressWarnings("all")
@Service
public class ElasticServiceImpl implements ElasticService {

    @Resource
    private RestHighLevelClient restHighLevelClient;


    @Override
    public String getDocument(String index, String id) throws Exception {
        GetRequest getRequest = new GetRequest(index, id);
        GetResponse getResponse = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
        return getResponse.getSourceAsString();
    }

    @Override
    public List<Map<String, Object>> searchRequest(String index, String keyword) throws Exception {
        // 搜索请求
        SearchRequest searchRequest;
        if (StringUtils.isEmpty(index)) {
            searchRequest = new SearchRequest();
        } else {
            searchRequest = new SearchRequest(index);
        }
        // 条件构造
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // 第几页
        searchSourceBuilder.from(0);
        // 每页多少条数据
        searchSourceBuilder.size(1000);
        // 配置高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("consumer_Name").field("status");
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.postTags("</span>");
        searchSourceBuilder.highlighter(highlightBuilder);
        // 精确查询
        //QueryBuilders.termQuery();
        // 匹配所有
        //QueryBuilders.matchAllQuery();
        // 最细粒度划分：ik_max_word，最粗粒度划分：ik_smart
        //multiMatchQuery多字段匹配查询
        searchSourceBuilder.query(QueryBuilders.multiMatchQuery(keyword, "status", "consumer_Name", "id"));
        // matchQuery 单字段匹配一个值
        // searchSourceBuilder.query(QueryBuilders.matchQuery("content", keyWord));
        searchSourceBuilder.timeout(TimeValue.timeValueSeconds(10));

        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        List<Map<String, Object>> results = new ArrayList<>();
        for (SearchHit searchHit : searchResponse.getHits().getHits()) {
            Map<String, HighlightField> highlightFieldMap = searchHit.getHighlightFields();
            HighlightField title = highlightFieldMap.get("consumer_Name");
            HighlightField description = highlightFieldMap.get("status");
            // 原来的结果
            Map<String, Object> sourceMap = searchHit.getSourceAsMap();
            // 解析高亮字段，替换掉原来的字段
            if (title != null) {
                Text[] fragments = title.getFragments();
                StringBuilder n_title = new StringBuilder();
                for (Text text : fragments) {
                    n_title.append(text);
                }
                sourceMap.put("consumer_Name", n_title.toString());
            }
            if (description != null) {
                Text[] fragments = description.getFragments();
                StringBuilder n_description = new StringBuilder();
                for (Text text : fragments) {
                    n_description.append(text);
                }
                sourceMap.put("status", n_description.toString());
            }
            results.add(sourceMap);
        }
        return results;
    }

    @Override
    public List<Integer> searchAllRequest(String index) throws Exception {
        // 搜索请求
        SearchRequest searchRequest;
        if (StringUtils.isEmpty(index)) {
            searchRequest = new SearchRequest();
        } else {
            searchRequest = new SearchRequest(index);
        }
        // 条件构造
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // 第几页
        searchSourceBuilder.from(0);
        // 每页多少条数据
        searchSourceBuilder.size(1000);
        // 匹配所有
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchSourceBuilder.timeout(TimeValue.timeValueSeconds(10));

        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        List<Integer> results = new ArrayList<>();
        for (SearchHit searchHit : searchResponse.getHits().getHits()) {
            results.add(Integer.valueOf(searchHit.getId()));
        }
        return results;
    }
}

