package com.gzw.kd.common.utils;

import cn.hutool.core.util.NumberUtil;
import static com.gzw.kd.common.Constants.*;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

/**
 * @author gzw
 * @description： Elastic scroll api 迭代类, 非线程安全
 * @since：2023/3/10 17:44
 */
@Slf4j
public class EsScrollIterable implements Iterable<List<Map<String, Object>>>{

    /**
     * 数据提供者
     */
    private final Function<String, SearchResponse> dataProvider;

    private final SearchHits initialHitList;

    private final long scrollCount;

    private final String initialScrollId;

    private final int sizePerScroll;

    private Iterator<List<Map<String, Object>>> forReuse;

    @Getter
    private long lastDocId;

    public EsScrollIterable(SearchResponse initialSearchResponse, Function<String, SearchResponse> dataProvider) {
        this(initialSearchResponse, dataProvider, ELASTIC_DEFAULT_FETCH_SIZE);
    }

    public EsScrollIterable(SearchResponse initialSearchResponse, Function<String, SearchResponse> dataProvider, int sizePerScroll) {
        if (sizePerScroll > ELASTIC_DEFAULT_FETCH_SIZE) {
            throw new IllegalArgumentException("Page size limit 1 to " + ELASTIC_DEFAULT_FETCH_SIZE);
        }

        this.sizePerScroll = sizePerScroll;
        this.dataProvider = dataProvider;
        this.initialHitList = initialSearchResponse.getHits();
        this.initialScrollId = initialSearchResponse.getScrollId();
        this.scrollCount = scrollCountCalc(initialSearchResponse.getHits().getTotalHits().value);

        if (log.isDebugEnabled()) {
            log.debug("Es scroll page size: {}", this.scrollCount);
        }
    }

    @Override
    public Iterator<List<Map<String, Object>>> iterator() {
        if (null == forReuse) {
            forReuse = new Itr();
        }
        return forReuse;
    }

    public String getLastScrollId() {
        return ((Itr) forReuse).lastScrollId;
    }

    private class Itr implements Iterator<List<Map<String, Object>>> {

        long cursor = INT_ZERO;
        String lastScrollId = initialScrollId;

        @Override
        public boolean hasNext() {
            return cursor <= scrollCount - INT_ONE;
        }

        @Override
        public List<Map<String, Object>> next() {
            if (log.isDebugEnabled()) {
                log.debug("Iterator scroll id : {}", lastScrollId);
            }

            if (INT_ZERO == cursor++) {
                lastDocId = docId(initialHitList);
                return convert(initialHitList);
            }

            SearchResponse response = dataProvider.apply(lastScrollId);
            SearchHits hits = response.getHits();
            lastScrollId = response.getScrollId();
            lastDocId = docId(hits);

            return convert(hits);
        }

        private List<Map<String, Object>> convert(SearchHits s) {
            return Stream.of(s.getHits()).map(SearchHit::getSourceAsMap).collect(Collectors.toList());
        }

        private long docId(SearchHits hits) {
            if (hits.getHits().length > LONG_ZERO) {
                String id = hits.getHits()[hits.getHits().length - INT_ONE].getId();
                if (NumberUtil.isNumber(id)) {
                    return Long.parseLong(id);
                }
            }
            return LONG_ZERO;
        }
    }

    private long scrollCountCalc(long hitsTotalCount) {
        long roughlyScrollCount = hitsTotalCount / sizePerScroll;
        return roughlyScrollCount >= INT_ONE ?
                (hitsTotalCount % sizePerScroll > INT_ZERO ? roughlyScrollCount + INT_ONE : roughlyScrollCount) : INT_ONE;
    }
}
