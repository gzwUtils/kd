package com.gzw.kd.mapper;
import com.alibaba.fastjson2.JSONObject;
import com.gzw.kd.common.enums.ResultCodeEnum;
import com.gzw.kd.common.es.EsSearchRequest;
import com.gzw.kd.common.exception.GlobalException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;

/**
 * @author gzw
 * @description：
 * @since：2023/3/10 16:57
 */

@SuppressWarnings("all")
@Slf4j
public class EsMapper {

    @Resource
    private RestHighLevelClient restHighLevelClient;


    public <T> List<T>   searchFromEs(EsSearchRequest request, Class<T> clazz) {
        List<T> arrayList = new ArrayList<>();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(request.getQueryBuilder());
        if(request.getFrom()!=0){
            searchSourceBuilder.from(request.getFrom());
        }
        if(request.getSize()!=0){
            searchSourceBuilder.size(request.getSize());
        }
        searchSourceBuilder.sort(request.getSort());
        SearchRequest searchRequest = new SearchRequest(request.getIndexName()).source(searchSourceBuilder);
        SearchResponse response;
        try {
            response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHit[] searchHitArr = response.getHits().getHits();
            for (SearchHit searchHit : searchHitArr) {
                String source = searchHit.getSourceAsString();
                T object = JSONObject.parseObject(source, clazz);
                arrayList.add(object);
            }
        } catch (Throwable e) {
            log.error("es查询操作日志信息异常:[{}]",e.getMessage(),e);
            throw new GlobalException("es查询操作日志信息异常["+e.getMessage()+"]", ResultCodeEnum.UNKNOWN_ERROR.getCode());
        }
        return arrayList;
    }


    public SearchResponse queryFromEs(EsSearchRequest request){
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(request.getQueryBuilder());
        searchSourceBuilder.sort(request.getSort());
        searchSourceBuilder.size(request.getSize());
        SearchRequest searchRequest = new SearchRequest().indices(request.getIndexName()).source(searchSourceBuilder).scroll(request.getScroll());
        SearchResponse response = null;
        try {
            response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("index {} query error {}",request.getIndexName(),e.getMessage(),e);
        }
        return response;
    }



    public RestHighLevelClient getClient(){
        return restHighLevelClient;
    }
}
