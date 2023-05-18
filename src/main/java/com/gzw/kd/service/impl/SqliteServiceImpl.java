package com.gzw.kd.service.impl;
import com.gzw.kd.mapper.SqliteMapper;
import com.gzw.kd.service.SqliteService;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * @author gzw
 * @description：
 * @since：2023/5/18 14:29
 */

@Service
public class SqliteServiceImpl implements SqliteService {

    @Resource
    private SqliteMapper sqliteMapper;

    @Override
    public List<Map<String,Object>> select(String sql) {
        return sqliteMapper.select(sql);
    }

    @Override
    public Integer insert(String sql) {
        return sqliteMapper.insert(sql);
    }

    @Override
    public Integer createSql(String sql) {
        return sqliteMapper.createSql(sql);
    }
}
