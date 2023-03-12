package com.gzw.kd.service.impl;


import com.gzw.kd.common.entity.WxUserInfo;
import com.gzw.kd.mapper.WxUserMapper;
import com.gzw.kd.service.WxUserService;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * @author gaozhiwei
 */
@Service
public class WxUserServiceImpl implements WxUserService {

    @Resource
    WxUserMapper wxUserMapper;


    @Override
    public WxUserInfo getUserByOpenId(String openId) throws Exception {
        return wxUserMapper.getUserByOpenId(openId);
    }


    @Override
    public WxUserInfo getUserByName(String name) throws Exception {
        return wxUserMapper.getUserByName(name);
    }

    @Override
    public Integer registerUser(WxUserInfo user) throws Exception {
        return wxUserMapper.registerUser(user);
    }

    @Override
    public Integer updatePhoneByOpenId(WxUserInfo user) throws Exception {
        return wxUserMapper.updatePhoneByOpenId(user);
    }

    @Override
    public Integer updatePhone(String openId,String phone,String name) throws Exception {
        return wxUserMapper.updatePhone(openId,phone,name);
    }

    @Override
    public List<String> getAllNames() {
        return wxUserMapper.getAllNames();
    }
}
