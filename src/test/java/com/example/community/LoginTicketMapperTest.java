package com.example.community;

import com.example.community.entity.LoginTicket;
import com.example.community.mapper.LoginTicketMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

/**
 * @program: community
 * @description:
 * @author: LK
 * @create: 2023-10-31 22:02
 **/

@SpringBootTest
public class LoginTicketMapperTest {


    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Test
    public void testInsert(){
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setTicket("abc");
        loginTicket.setStatus(1);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + 1000 * 60 * 10));
        loginTicket.setUserId(1);

        System.out.println(loginTicketMapper.insertLoginTicket(loginTicket));
    }
    @Test
    public void testUpdate(){
        System.out.println(loginTicketMapper.updateStatus("abc", 0));
    }

    @Test
    public void testSelect(){
        System.out.println(loginTicketMapper.selectByTicket("abc"));
    }
}
