package com.gzw.kd.mapper;


import com.gzw.kd.common.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author 高志伟
 */
@Repository
@Mapper
public interface UserMapper {

    User getUserByName(String  account);


    User getUserById(int  id);


    List<User> getUsersByIds(@Param("ids") List<String> ids);

    User getUserByNameAndPhone(String  account,String phone);

    User getUserByPhone(String  phone);

    Integer registerUser(User user);

    Integer updatePasswordDocById(User user);

    List<String> getAllNames();

    Integer updateStatusByName(String account,int status,int errorRetry);

    Integer updateStatusById(String id,int status);

    Integer updateErrorByName(String account,int errorRetry);

    List<User> getAllUsers();

    List<User> getAllStopUsers();



}
