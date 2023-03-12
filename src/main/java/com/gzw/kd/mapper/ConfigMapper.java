package com.gzw.kd.mapper;



import com.gzw.kd.common.entity.Configs;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@SuppressWarnings("all")
@Repository
@Mapper
public interface ConfigMapper {

    //查询config表
    Configs getConfigs() throws  Exception;

    //插入一条记录
    Integer addDoc(Configs configs) throws  Exception;

    //更新数据库
    Integer updateConfigsForById(Configs configs) throws  Exception;
}
