package com.only.mybatis.spring.service;


import com.only.mybatis.spring.entity.User;

public interface UserService {

    User getUser(Integer userId);

    User getUser2(Integer userId);

    User getUser3(Integer userId);

    void addUser(String username);

    void addUser2(String username);

}
