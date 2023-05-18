package com.gzw.kd.mapper;
import com.gzw.kd.config.datasource.annotation.SpecDataSource;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author 高志伟
 */
@SpecDataSource("SLAVE_4")
@SuppressWarnings("all")
@Repository
@Mapper
public interface SqliteMapper {

    List<Map<String,Object>> select(String sql);

    Integer  insert(String sql);

    Integer createSql(String sql);



}
