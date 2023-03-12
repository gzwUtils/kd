package com.gzw.kd.service;
import java.util.List;
import java.util.Map;

/**
 * @author 高志伟
 */
public interface ElasticService {

    /**
     * 获取文档
     * @param index index
     * @param id    id
     * @return json
     * @throws Exception glob
     */
    String getDocument(String index, String id) throws Exception;


    /**
     * 搜索请求
     * @param index index
     * @param keyword keyword
     * @return json
     * @throws Exception glob
     */
    List<Map<String, Object>> searchRequest(String index, String keyword) throws Exception;

    /**
     * 搜索所有id
     * @param index index
     * @return json
     * @throws Exception glob
     */
    List<Integer> searchAllRequest(String index) throws Exception;

}
