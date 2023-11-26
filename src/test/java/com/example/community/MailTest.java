package com.example.community;

import com.example.community.util.MailClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * @program: community
 * @description:
 * @author: LK
 * @create: 2023-10-22 21:15
 **/

@SpringBootTest
public class MailTest {

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Test
    public void sendMailTest(){
        mailClient.sendMail("1157264647@qq.com","TEST","success");
    }

    @Test
    // 利用thymeleaf模板引擎发送HTML邮件
    public void sendHTMLTest(){
        Context context = new Context();
        context.setVariable("username","lkkkk");
        // 调用模板引擎生成HTML格式的邮件
        String process = templateEngine.process("mail/demo", context);
        System.out.println(process);
        mailClient.sendMail("1157264647@qq.com","HTML",process);
    }

}
