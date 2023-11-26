package com.example.community.controller;

import com.example.community.util.CommunityUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @program: community
 * @description:
 * @author: LK
 * @create: 2023-10-26 20:18
 **/

@Controller
@RequestMapping("/test")
public class testController {

    // cookie
    @GetMapping("/cookie/set")
    @ResponseBody
    public String cookieSet(HttpServletResponse response){
        //床架cookie
        Cookie cookie = new Cookie("code", CommunityUtil.generateUUID());
        // 设置有效路径
        cookie.setPath("/community/test");
        // 设置有效时间
        cookie.setMaxAge(60 * 10);
        // 发送cookie
        response.addCookie(cookie);
        return "set cookie";
    }

    @RequestMapping("/cookie/get")
    @ResponseBody
    public String cookieGet(@CookieValue("code")String code){
        System.out.println(code);
        return "get cookie";
    }

    // session
    @GetMapping("/session/set")
    @ResponseBody
    public String sessionSet(HttpSession session){
        session.setAttribute("id","1");
        session.setAttribute("name","test");
        return "set session";
    }

    @GetMapping("/session/get")
    @ResponseBody
    public String sessionGet(HttpSession session){
        System.out.println(session.getAttribute("id"));
        System.out.println(session.getAttribute("name"));
        return "get session";
    }







}
