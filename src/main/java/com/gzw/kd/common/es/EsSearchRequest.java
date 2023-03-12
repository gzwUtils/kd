package com.gzw.kd.common.es;
import lombok.Data;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.sort.FieldSortBuilder;

/**
 * @author gzw
 * @description： es query
 * @since：2023/3/10 16:20
 */

@Data
public class EsSearchRequest {


    private String indexName;


    private QueryBuilder queryBuilder;

    private int from ;

    private int size;

    private FieldSortBuilder sort;


    private Scroll scroll;

    private String preference;
}
