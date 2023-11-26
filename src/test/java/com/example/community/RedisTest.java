package com.example.community;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.*;

/**
 * @program: community
 * @description:
 * @author: LK
 * @create: 2023-11-18 12:21
 **/



@SpringBootTest
public class RedisTest {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testStrings(){
        String redisKey = "test:count";

        redisTemplate.opsForValue().set(redisKey,1);

        System.out.println(redisTemplate.opsForValue().get(redisKey));
        System.out.println(redisTemplate.opsForValue().increment(redisKey));
        System.out.println(redisTemplate.opsForValue().decrement(redisKey));

        System.out.println("-------------------------------------");
        BoundValueOperations operations = redisTemplate.boundValueOps(redisKey);

        operations.increment();
        operations.increment();
        operations.increment();
        operations.increment();
        System.out.println(operations.get());
    }

    @Test
    public void testTransactional(){
        String redisKey = "test:name";

        Object res = redisTemplate.execute(new SessionCallback() {
            Object o;
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                // 开启事务
                operations.multi();
                operations.opsForValue().set(redisKey, "lisi");
                operations.opsForValue().set(redisKey, "zhangsan");
                operations.opsForValue().set(redisKey, "wangwu");

//                operations.opsForValue().get(redisKey);

                return operations.exec();

            }

        });
        System.out.println(res);
    }



}
