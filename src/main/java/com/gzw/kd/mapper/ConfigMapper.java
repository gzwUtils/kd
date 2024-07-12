package com.gzw.kd.mapper;



import com.gzw.kd.common.entity.Configs;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@SuppressWarnings("all")
@Repository
@Mapper
public interface ConfigMapper {

    //查询config表
    Configs getConfigs();

    //插入一条记录
    Integer addDoc(Configs configs);

    //更新数据库
    Integer updateConfigsForById(Configs configs);
}
