package com.gzw.kd.mapper;

import static com.gzw.kd.common.Constants.ELASTIC_DEFAULT_FETCH_SIZE;
import static com.gzw.kd.common.Constants.INT_ONE;
import com.gzw.kd.common.es.EsSearchRequest;
import com.gzw.kd.vo.input.LogSearchInput;
import com.gzw.kd.vo.output.LogExportOutput;
import java.io.IOException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.ClearScrollRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

/**
 * @author gzw
 * @description： log es mapper
 * @since：2023/3/10 16:05
 */

@SuppressWarnings("unused")
@Repository
@Slf4j
public class LogEsMapper extends CommonEsMapper {

    @Value("${operatorLog.index:log_data_all}")
    private String logIndex;

    /**
     * 按照搜索条件bool查询
     *
     * @param searchInput 查询参数
     * @return es查询信息
     */
    public List<LogExportOutput> boolQuery(LogSearchInput searchInput) {
        final int pageNo = searchInput.getPageNum(), pageSize = searchInput.getPageSize();

        EsSearchRequest request = new EsSearchRequest();
        request.setIndexName(logIndex);
        request.setQueryBuilder(genBoolQuery(searchInput));
        request.setFrom((pageNo - INT_ONE) * pageSize);
        request.setSize(pageSize);
        request.setSort(SortBuilders.fieldSort("create_time").order(SortOrder.DESC));
        return searchFromEs(request,LogExportOutput.class);
    }

    /**
     * 根据request查询
     *
     * @param esSearchRequest 查询条件
     * @return es查询结果
     */
    public List<LogExportOutput> query(EsSearchRequest esSearchRequest) {
        return searchFromEs(esSearchRequest, LogExportOutput.class);
    }

    /**
     * 查询符合条件的文档数
     *
     * @param searchInput 查询参数
     * @return 符合查询条件的文档数
     */
    public long count(LogSearchInput searchInput) {
        EsSearchRequest request = new EsSearchRequest();
        request.setIndexName(logIndex);
        request.setQueryBuilder(genBoolQuery(searchInput));
        return queryFromEs(request).getHits().getTotalHits().value;
    }


    /**
     * 开始执行scroll
     *
     * @param searchInput 查询参数
     * @return 初始scroll响应
     */
    public SearchResponse scrollBegin(LogSearchInput searchInput) {
        return scrollBegin(searchInput, ELASTIC_DEFAULT_FETCH_SIZE);
    }

    /**
     * 开始执行scroll
     *
     * @param searchInput 查询参数
     * @param fetchSize   每次scan时获取的文档数
     * @return 初始scroll响应
     */
    public SearchResponse scrollBegin(LogSearchInput searchInput, final int fetchSize) {
        EsSearchRequest request = new EsSearchRequest();
        request.setIndexName(logIndex);
        request.setQueryBuilder(genBoolQuery(searchInput));
        request.setSize(fetchSize);
        request.setScroll(new Scroll(new TimeValue(50000)));
        request.setSort(SortBuilders.fieldSort("id"));
        return queryFromEs(request);
    }

    /**
     * 清除scroll context
     *
     * @param scrollId scrollId
     */
    public void scrollClear(final String scrollId) {
        ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
        clearScrollRequest.addScrollId(scrollId);
        try {
            getClient().clearScroll(clearScrollRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 按照page执行scroll
     *
     * @param scrollId scrollId
     * @return 每次的scroll响应
     */
    public SearchResponse scrollByPage(final String scrollId) {
        SearchScrollRequest request = new SearchScrollRequest(scrollId);
        request.scroll(new TimeValue(50000));
        try {
            return getClient().scroll(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
