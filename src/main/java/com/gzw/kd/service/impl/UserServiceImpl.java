package com.gzw.kd.service.impl;


import com.gzw.kd.common.entity.User;
import com.gzw.kd.mapper.UserMapper;
import com.gzw.kd.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


/**
 * @author 高志伟
 */
@Service
public class UserServiceImpl implements UserService {

    @Resource
    UserMapper userMapper;


    @Override
    public User getUserByName(String username) throws Exception {
        return userMapper.getUserByName(username);
    }

    @Override
    public User getUserByNameAndPhone(String username, String phone) throws Exception {
        return userMapper.getUserByNameAndPhone(username,phone);
    }

    @Override
    public User getUserByPhone(String phone){
        return userMapper.getUserByPhone(phone);
    }

    @Override
    public Integer registerUser(User user) throws Exception {
        return userMapper.registerUser(user);
    }

    @Override
    public Integer updatePasswordDocById(User user) throws Exception {
        return userMapper.updatePasswordDocById(user);
    }

    @Override
    public Integer updateStatusByName(String name, int status) throws Exception {
        return userMapper.updateStatusByName(name,status);
    }

    @Override
    public Integer updateStatusById(String id, int status) throws Exception {
        return userMapper.updateStatusById(id,status);
    }

    @Override
    public Integer updateErrorByName(String name, int errorRetry) throws Exception {
        return userMapper.updateErrorByName(name,errorRetry);
    }

    @Override
    public List<String> getAllNames() {
        return userMapper.getAllNames();
    }

    @Override
    public List<User> getAllUsers() {
        return userMapper.getAllUsers();
    }
}
