package com.example.community;

import com.example.community.entity.Message;
import com.example.community.entity.User;
import com.example.community.mapper.MessageMapper;
import com.example.community.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;
import java.util.List;

/**
 * @program: community
 * @description:
 * @author: LK
 * @create: 2023-10-16 21:40
 **/

@ContextConfiguration(classes = CommunityApplication.class)
@SpringBootTest
public class MapperTest {
    @Autowired
    UserMapper userMapper;

    @Autowired
    MessageMapper messageMapper;

    @Test
    void selectTest(){
        System.out.println(userMapper.selectById(1));
        System.out.println(userMapper.selectByEmail("nowcoder101@sina.com"));
        System.out.println(userMapper.selectByName("mmm"));
    }

    @Test
    void insetTest(){
        User user = new User();
        user.setEmail("1157264647@qq.com");
        user.setUsername("likai");
        user.setPassword("123456");
        user.setHeaderUrl("http://images.nowcoder.com/head/316t.png");
        user.setCreateTime(new Date());

        int i = userMapper.insertUser(user);
        System.out.println(i);
        System.out.println("id=" + user.getId());

    }

    @Test
    void updateTest(){
        System.out.println(userMapper.updateStatus(153, 1));
        System.out.println(userMapper.updateHeader(153, "http://images.nowcoder.com/head/199t.png"));
        System.out.println(userMapper.updatePassword(153, "1234"));
    }

    @Test
    public void MessageMapperTest(){
        List<Message> list = messageMapper.selectConversations(111, 0, 20);
        for (Message message : list) {
            System.out.println(message);
        }
        int count = messageMapper.selectConversationCount(111);
        System.out.println(count);
        list = messageMapper.selectLetters("111_112",0,10);
        for (Message message : list) {
            System.out.println(message);
        }
        count = messageMapper.selectLettersCount("111_112");
        System.out.println(count);
        count = messageMapper.selectLettersUnreadCount(131,"111_131");
        System.out.println(count);
    }
}
