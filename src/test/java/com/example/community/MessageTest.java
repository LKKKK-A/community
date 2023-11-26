package com.example.community;

import com.example.community.mapper.MessageMapper;
import com.example.community.util.CommunityConstant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @program: community
 * @description:
 * @author: LK
 * @create: 2023-11-22 13:11
 **/

@SpringBootTest
public class MessageTest implements CommunityConstant {

    @Autowired
    MessageMapper messageMapper;

    @Test
    public void test(){
        System.out.println(messageMapper.selectUnreadNoticeCount(168, TOPIC_LIKE));
    }



}
