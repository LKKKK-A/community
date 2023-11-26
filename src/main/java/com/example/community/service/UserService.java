package com.example.community.service;

import com.example.community.entity.LoginTicket;
import com.example.community.entity.User;
import com.example.community.mapper.LoginTicketMapper;
import com.example.community.mapper.UserMapper;
import com.example.community.util.CommunityConstant;
import com.example.community.util.CommunityUtil;
import com.example.community.util.MailClient;
import com.example.community.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @program: community
 * @description:
 * @author: LK
 * @create: 2023-10-15 22:55
 **/

@Service
public class UserService implements CommunityConstant {




    @Autowired
    UserMapper userMapper;

//    @Autowired
//    LoginTicketMapper loginTicketMapper;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private MailClient mailClient;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Value("${community.path.domain}")
    private String domain;

    @Autowired
    private RedisTemplate redisTemplate;

    public User findUserById(int userId){
        User user = getCache(userId);
        if(user == null){
            user = initCache(userId);
        }
        return user;
//        return userMapper.selectById(userId);
    }




    public Map<String,Object> register(User user){
        HashMap<String, Object> map = new HashMap<>();
        // 空值判断
        if(user == null) {
            throw new IllegalArgumentException("参数不能为空！");
        }
        if(StringUtils.isBlank(user.getUsername())){
            map.put("usernameMsg","用户名不能为空");
            return map;
        }
        if(StringUtils.isBlank(user.getPassword())){
            map.put("passwordMsg","密码不能为空");
            return map;
        }
        if(StringUtils.isBlank(user.getEmail())){
            map.put("emailMsg","邮箱不能为空");
            return map;
        }

        // 验证是否已经注册
        User u = userMapper.selectByName(user.getUsername());
        if(u != null){
            map.put("usernameMsg","该账号已存在！");
            return map;
        }
        u = userMapper.selectByEmail(user.getEmail());
        if(u != null){
            map.put("emailMsg","该邮箱已被注册！");
            return map;
        }


        // 使用MD5对密码加密
        //生成随机slat
        String salt = CommunityUtil.generateUUID().substring(0, 5);
        user.setSalt(salt);
        // md5加密
        String md5Password = CommunityUtil.md5(user.getPassword() + user.getSalt());
        user.setPassword(md5Password);

        // 设置其他属性并注册
        user.setStatus(0);
        user.setType(0);
        //设置激活码
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setCreateTime(new Date());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png",new Random().nextInt(1000)));
        userMapper.insertUser(user);

        // 发送激活邮件
        Context context = new Context();
        context.setVariable("email",user.getEmail());
        // http://localhost:8081/community/activation/101/code
        String url = domain + contextPath + "/activation/" + user.getId() +"/"+ user.getActivationCode();
        context.setVariable("url",url);
        System.out.println(url);
        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(),"激活账号",content);
        return map;
    }




    public int activation(int userId,String activationCode){
        User user = userMapper.selectById(userId);
        if(user.getStatus() == 1) {
            // 重复激活
            return ACTIVATION_REPEAT;
        }else if(user.getActivationCode().equals(activationCode)){
            userMapper.updateStatus(userId,1);
            clearCache(userId);
            //激活成功
            return ACTIVATION_SUCCESS;
        }else{
            // 激活失败
            return ACTIVATION_FAILURE;
        }
    }




    public Map<String,Object> login(String username,String password,int expiredSeconds){
        Map<String,Object> map = new HashMap<>();
        // 空值判断
        if(StringUtils.isBlank(username)){
            map.put("usernameMsg","用户名不能为空！");
            return map;
        }
        if(StringUtils.isBlank(password)){
            map.put("passwordMsg","密码不能为空！");
            return map;
        }
        // 验证登录信息
        User user = userMapper.selectByName(username);
        if(user == null){
            map.put("usernameMsg","该用户未注册！");
            return map;
        }
        if(user.getStatus() == 0){
            map.put("usernameMsg","该账号未激活！");
            return map;
        }

        password = CommunityUtil.md5(password + user.getSalt());
        if(!user.getPassword().equals(password)){
            map.put("passwordMsg","密码不正确！");
            return map;
        }

        // 生成登陆凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));
        loginTicket.setUserId(user.getId());
        loginTicket.setStatus(0);

//        loginTicketMapper.insertLoginTicket(loginTicket);
        // 将登录凭证存到redis中
        String ticketKey = RedisKeyUtil.getTicketKey(loginTicket.getTicket());
        redisTemplate.opsForValue().set(ticketKey,loginTicket);

        map.put("ticket",loginTicket.getTicket());
        return map;
    }




    public void logout(String ticket){
//        loginTicketMapper.updateStatus(ticket,1);
        String ticketKey = RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(ticketKey);
        loginTicket.setStatus(1);
        redisTemplate.opsForValue().set(ticketKey,loginTicket);
    }




    public LoginTicket findLoginTicket(String ticket){
        String ticketKey = RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(ticketKey);
        return loginTicket;
//        return loginTicketMapper.selectByTicket(ticket);
    }




    public int updateHeader(int userId,String headerUrl){
        int rows = userMapper.updateHeader(userId, headerUrl);
        clearCache(userId);
        return rows;
//        return userMapper.updateHeader(userId,headerUrl);
    }




    public Map<String,Object> updatePassword(int id ,String oldPassword,String newPassword){
        Map<String, Object> map = new HashMap<>();
        if(StringUtils.isBlank(oldPassword)){
            map.put("oldPasswordMsg","原始密码不能为空！");
            return map;
        }
        if(StringUtils.isBlank(newPassword)){
            map.put("newPasswordMsg","新密码不能为空！");
            return map;
        }
        // 判断原始密码是否正确
        User user = userMapper.selectById(id);
        String salt = user.getSalt();
        oldPassword = oldPassword + salt;
        oldPassword = CommunityUtil.md5(oldPassword);
        if(!user.getPassword().equals(oldPassword)){
            map.put("oldPasswordMsg","原始密码不正确！");
            return map;
        }
        // 修改密码
        // MD5加密
        String newSalt = CommunityUtil.generateUUID().substring(0, 5);
        newPassword = newPassword + newSalt;
        newPassword = CommunityUtil.md5(newPassword);
        userMapper.updatePassword(id,newPassword);
        clearCache(user.getId());
        return map;
    }

    public User findUserByName(String username){
        return userMapper.selectByName(username);
    }

    // 1.优先从缓存里面取
    private User getCache(int userId){
        String userKey = RedisKeyUtil.getUserKey(userId);
        User user = (User) redisTemplate.opsForValue().get(userKey);
        return user;
    }

    //2.缓存里面没有的话，从数据库里面取，并初始化缓存
    private User initCache(int userId){
        User user = userMapper.selectById(userId);
        String userKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.opsForValue().set(userKey,user,3600, TimeUnit.SECONDS);
        return user;
    }

    // 3.数据变更时清除缓存数据
    private void clearCache(int userId){
        String userKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.delete(userKey);
    }
















}
