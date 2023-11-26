package com.example.community.util;

import com.example.community.entity.User;
import org.springframework.stereotype.Component;

/**
 * @program: community
 * @description:
 * @author: LK
 * @create: 2023-11-05 21:53
 **/

@Component
public class HostHolder {

     ThreadLocal<User> threadLocal = new ThreadLocal<User>();

    public  void set(User user){
        threadLocal.set(user);
    }
    public  User get(){
        return threadLocal.get();
    }

    public  void clear(){
        threadLocal.remove();
    }
}
