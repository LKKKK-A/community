package com.example.community.entity;

import lombok.Data;

import java.util.Date;

/**
 * @program: community
 * @description:
 * @author: LK
 * @create: 2023-10-31 21:51
 **/

@Data
public class LoginTicket {
    private int id;
    private int userId;
    private String ticket;
    private int status;
    private Date expired;


}
