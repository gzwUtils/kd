package com.gzw.kd.service;


import com.gzw.kd.common.entity.User;

import java.util.List;

/**
 * @author 高志伟
 */
@SuppressWarnings("all")
public interface UserService {

    User getUserByName(String  username)throws  Exception;

    User getUserByNameAndPhone(String  username,String phone)throws  Exception;

    User getUserByPhone(String  phone);

    Integer registerUser(User user)throws  Exception;

    Integer updatePasswordDocById(User user)throws  Exception;

    Integer updateStatusByName(String name,int status,int errorRetry)throws  Exception;

    Integer updateStatusById(String id,int status)throws  Exception;

    Integer updateErrorByName(String name,int errorRetry)throws  Exception;

    List<String> getAllNames();


    List<User> getAllUsers();


    List<User> getAllStopUsers();

}
