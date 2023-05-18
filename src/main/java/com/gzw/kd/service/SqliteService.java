package com.gzw.kd.service;
import java.util.List;
import java.util.Map;

/**
 * @author 高志伟
 */
@SuppressWarnings("all")
public interface SqliteService {

    List<Map<String,Object>> select(String sql);

    Integer  insert(String sql);

    Integer createSql(String sql);

}
