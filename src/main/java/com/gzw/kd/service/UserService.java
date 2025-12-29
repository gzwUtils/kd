package com.gzw.kd.service;


import com.gzw.kd.common.entity.User;

import java.util.List;

/**
 * @author 高志伟
 */
public interface UserService {

    User getUserByName(String  username);

    User getUserByPhone(String  phone);

    Integer registerUser(User user);

    Integer updatePasswordDocById(User user);

    void updateStatusByName(String name,int status,int errorRetry);

    void updateStatusById(String id,int status);

    void updateErrorByName(String name,int errorRetry);

    List<String> getAllNames();


    List<User> getAllUsers();


    List<User> getAllStopUsers();

}
