package com.gzw.kd.service;


import com.gzw.kd.common.entity.Configs;

@SuppressWarnings("all")
public interface ConfigService {

    //根据id查询config表
    Configs getConfigs() throws  Exception;

    //插入一条记录
    Integer addDoc(Configs configs) throws  Exception;

    //更新数据库
    Integer updateConfigsForById(Configs configs) throws  Exception;


}
