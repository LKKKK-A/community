package com.example.community.mapper;

import com.example.community.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Mapper
public interface UserMapper {

    List<User> selectUsers();

    User selectById(int id);

    User selectByName(String username);

    User selectByEmail(String email);

    int updateHeader(int id,String headerUrl);

    int updateStatus( int id,int status);

    int updatePassword(int id, String password);

    int insertUser(User user);

}
