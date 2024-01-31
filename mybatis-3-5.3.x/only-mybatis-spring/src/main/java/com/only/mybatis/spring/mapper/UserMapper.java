package com.only.mybatis.spring.mapper;

import com.only.mybatis.spring.entity.User;
import org.apache.ibatis.annotations.Param;


public interface UserMapper {

    User getByUserId(@Param("userId") Integer userId);

    void updateByUserId(@Param("username") String username, @Param("userId") Integer userId);

    User getByUsername(@Param("username") String username);

    void saveUser(User user);


}
