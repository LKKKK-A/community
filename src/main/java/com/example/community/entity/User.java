package com.example.community.entity;

import lombok.Data;

import java.util.Date;

/**
 * @program: community
 * @description:
 * @author: LK
 * @create: 2023-10-15 22:53
 **/

@Data
public class User {
    public int id;
    public String username;
    public String password;
    public String salt;
    public String email;
    public int type;
    public int status;
    public String activationCode;
    public String headerUrl;
    public Date createTime;

}
