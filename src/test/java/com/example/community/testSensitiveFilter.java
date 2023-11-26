package com.example.community;

import com.example.community.util.SensitiveFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @program: community
 * @description:
 * @author: LK
 * @create: 2023-11-12 13:45
 **/

@SpringBootTest
public class testSensitiveFilter {
    @Autowired
    SensitiveFilter sensitiveFilter;

    @Test
    public void testSensitiveFilter(){
        String text = "这里可以嫖娼，可以吸毒，可以赌博，我爱赌博";
        text = sensitiveFilter.filter(text);
        System.out.println(text);

        text = "这里可以□嫖□娼□，可以□吸□毒□，可以□赌□博□，可以□开□票□，哈哈哈！";
        text = sensitiveFilter.filter(text);
        System.out.println(text);
    }


}
