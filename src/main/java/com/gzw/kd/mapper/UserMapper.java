package com.gzw.kd.mapper;


import com.gzw.kd.common.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author 高志伟
 */
@SuppressWarnings("all")
@Repository
@Mapper
public interface UserMapper {

    User getUserByName(String  account)throws  Exception;

    User getUserByNameAndPhone(String  account,String phone)throws  Exception;

    User getUserByPhone(String  phone);

    Integer registerUser(User user)throws  Exception;

    Integer updatePasswordDocById(User user)throws  Exception;

    List<String> getAllNames();

    Integer updateStatusByName(String account,int status,int errorRetry)throws  Exception;

    Integer updateStatusById(String id,int status)throws  Exception;

    Integer updateErrorByName(String account,int errorRetry)throws  Exception;

    List<User> getAllUsers();

    List<User> getAllStopUsers();



}
