package com.example.community;

import com.example.community.entity.DiscussPost;
import com.example.community.mapper.DiscussPostMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @program: community
 * @description:
 * @author: LK
 * @create: 2023-10-16 22:39
 **/

@SpringBootTest
public class DiscussPostTest {

    @Autowired
    DiscussPostMapper discussPostMapper;

    @Test
    void selectTest(){
        System.out.println(discussPostMapper.selectDiscussPostRows(103));
        for (DiscussPost discussPost : discussPostMapper.selectDiscussPosts(103, 0, 10)) {
            System.out.println(discussPost);
        }
    }






}
