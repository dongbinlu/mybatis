package com.only.mybatis.spring.service;


import com.only.mybatis.spring.entity.User;
import com.only.mybatis.spring.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService userService;

    @Override
    public User getUser(Integer userId) {
        return userMapper.getByUserId(userId);
    }

    @Override
    public User getUser2(Integer userId) {
        return userMapper.getByUserId(userId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public User getUser3(Integer userId) {
        return userMapper.getByUserId(userId);
    }


    @Override
    @Transactional
    public void addUser(String username) {

        userMapper.saveUser(new User(username));

        userService.addUser2(username);
    }

    @Override
    @Transactional
    public void addUser2(String username) {
        try {
            userMapper.saveUser(new User(username));
            int i = 1/0;
        } catch (Exception e) {
            System.out.println(111);
        }

    }
}
