package com.gzw.kd.service.impl;


import com.gzw.kd.common.entity.Assign;
import com.gzw.kd.mapper.CustomerMapper;
import com.gzw.kd.service.CustomerService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


/**
 * @author gaozhiwei
 */
@Service
public class CustomerServiceImpl implements CustomerService {

    @Resource
    CustomerMapper customerMapper;


    @Override
    public Assign getUserByOpenId(String openId) throws Exception {
        return customerMapper.getUserByOpenId(openId);
    }


    @Override
    public Assign getUserByName(String name) throws Exception {
        return customerMapper.getUserByName(name);
    }

    @Override
    public Integer registerUser(Assign user) throws Exception {
        return customerMapper.registerUser(user);
    }

    @Override
    public Integer updateAssignByOpenId(Assign user) throws Exception {
        return customerMapper.updateAssignByOpenId(user);
    }

    @Override
    public Integer updatePhone(String openId,String phone,String name) throws Exception {
        return customerMapper.updatePhone(openId,phone,name);
    }

    @Override
    public List<String> getAllNames() {
        return customerMapper.getAllNames();
    }
}
