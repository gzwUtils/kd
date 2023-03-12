package com.gzw.kd.mapper;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import static com.gzw.kd.common.Constants.*;
import com.gzw.kd.common.annotation.EsSearchCondition;
import com.gzw.kd.common.utils.EsNullValueQueryWrapper;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.map.MultiValueMap;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.ExistsQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

/**
 * @author gzw
 * @description： 根据 search 构建 queryBuilder
 * @since：2023/3/10 15:51
 */
@SuppressWarnings("all")
@Slf4j
public class CommonEsMapper  extends EsMapper{

    /**
     * 根据搜索数据生成es查询条件，支持null空数据查询
     * @param searchInput 查询条件
     * @return es 查询条件
     */
    protected BoolQueryBuilder genBoolQuery(Object searchInput) {
        final BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        final MultiValueMap rangeQueryMap = new MultiValueMap();

        for (Field field : searchInput.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            final EsSearchCondition condition = field.getAnnotation(EsSearchCondition.class);
            if (null != condition) {
                try {
                    Object value = field.get(searchInput);
                    if (!condition.isRangeQuery()) {
                        if (null != value) {
                            if (value instanceof Collection) {
                                if (CollectionUtil.isNotEmpty((Collection<?>) value)) {
                                    if (((Collection<?>) value).stream().anyMatch(Objects::nonNull)) {
                                        queryBuilder.must(QueryBuilders.termsQuery(condition.fieldName(), (Collection<?>) value));
                                    }
                                }
                            } else if (value instanceof String) {
                                if (StrUtil.isNotBlank((CharSequence) value)) {
                                    String fieldName = condition.fieldName() + condition.fieldsConfig(),
                                            finalValue = condition.toLowerCase() ? ((String) value).toLowerCase() : (String) value;
                                    queryBuilder.must(QueryBuilders.termQuery(fieldName, finalValue));
                                }
                            } else if (value instanceof EsNullValueQueryWrapper) {
                                final EsNullValueQueryWrapper<?> wrapper = (EsNullValueQueryWrapper<?>) value;
                                if (wrapper.isQueryNullValue()) {
                                    final ExistsQueryBuilder existsQueryBuilder = QueryBuilders.existsQuery(condition.fieldName());
                                    final BoolQueryBuilder boolQueryBuilder =
                                            QueryBuilders.boolQuery().should(QueryBuilders.boolQuery().mustNot(existsQueryBuilder));
                                    if (CollectionUtil.isNotEmpty((Collection<?>) wrapper.getOriginParameter())) {
                                        boolQueryBuilder.should(
                                                QueryBuilders.termsQuery(condition.fieldName(), (Collection<?>) wrapper.getOriginParameter()));
                                    }
                                    queryBuilder.must(boolQueryBuilder);
                                } else {
                                    queryBuilder.must(
                                            QueryBuilders.termsQuery(
                                                    condition.fieldName(), (Collection<?>) wrapper.getOriginParameter()));
                                }
                            } else {
                                queryBuilder.must(QueryBuilders.termQuery(condition.fieldName(), value));
                            }
                        }
                    } else {
                        rangeQueryMap.put(condition.fieldName(), value);
                    }
                } catch (IllegalAccessException e) {
                    log.error("根据 search 构建 queryBuilder field {} error {}",field,e.getMessage(),e);
                }
            }
        }

        for (Object fieldName : rangeQueryMap.keySet()) {
            Object[] rangeArray = rangeQueryMap.getCollection(fieldName).toArray(new Object[INT_ZERO]);
            if (rangeArray.length == INT_TWO) {
                Object startPos = rangeArray[INT_ZERO], endPos = rangeArray[INT_ONE];
                if (null != startPos && null != endPos) {
                    queryBuilder.must(QueryBuilders.rangeQuery((String) fieldName).from(startPos).to(endPos));
                }
            }
        }

        return queryBuilder;
    }
}
