package com.gzw.kd.service;


import com.gzw.kd.common.entity.Configs;

@SuppressWarnings("all")
public interface ConfigService {

    //根据id查询config表
    Configs getConfigs();

    //插入一条记录
    Integer addDoc(Configs configs);

    //更新数据库
    Integer updateConfigsForById(Configs configs);


}
