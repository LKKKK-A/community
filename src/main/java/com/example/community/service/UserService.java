package com.example.community.service;

import com.example.community.entity.User;
import com.example.community.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @program: community
 * @description:
 * @author: LK
 * @create: 2023-10-15 22:55
 **/

@Service
public class UserService {


    @Autowired
    UserMapper userMapper;
    public User findUserById(int userId){
        User user = userMapper.selectById(userId);
        return user;
    }



}
