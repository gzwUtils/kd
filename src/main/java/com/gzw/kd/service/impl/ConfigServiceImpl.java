package com.gzw.kd.service.impl;

import com.gzw.kd.common.entity.Configs;
import com.gzw.kd.mapper.ConfigMapper;
import com.gzw.kd.service.ConfigService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author 高志伟
 */

@Service
public class ConfigServiceImpl implements ConfigService {

    @Resource
    ConfigMapper configMapper;

    @Override
    public Configs getConfigs() throws Exception {
        return configMapper.getConfigs();
    }

    @Override
    public Integer addDoc(Configs configs) throws Exception {
        return configMapper.addDoc(configs);
    }

    @Override
    public Integer updateConfigsForById(Configs configs) throws Exception {
        return configMapper.updateConfigsForById(configs);
    }
}
