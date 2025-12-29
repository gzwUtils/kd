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
    public User getUserByName(String username) {
        return userMapper.getUserByName(username);
    }

    @Override
    public User getUserByPhone(String phone){
        return userMapper.getUserByPhone(phone);
    }

    @Override
    public Integer registerUser(User user) {
        return userMapper.registerUser(user);
    }

    @Override
    public Integer updatePasswordDocById(User user) {
        return userMapper.updatePasswordDocById(user);
    }

    @Override
    public void updateStatusByName(String name, int status,int errorRetry) {
         userMapper.updateStatusByName(name,status,errorRetry);
    }

    @Override
    public void updateStatusById(String id, int status)  {
         userMapper.updateStatusById(id,status);
    }

    @Override
    public void updateErrorByName(String name, int errorRetry) {
        userMapper.updateErrorByName(name,errorRetry);
    }

    @Override
    public List<String> getAllNames() {
        return userMapper.getAllNames();
    }

    @Override
    public List<User> getAllUsers() {
        return userMapper.getAllUsers();
    }

    @Override
    public List<User> getAllStopUsers() {
        return userMapper.getAllStopUsers();
    }
}
