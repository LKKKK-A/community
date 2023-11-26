package com.example.community.controller;

import com.example.community.entity.DiscussPost;
import com.example.community.entity.Page;
import com.example.community.entity.User;
import com.example.community.mapper.DiscussPostMapper;
import com.example.community.service.DiscussPostService;
import com.example.community.service.LikeService;
import com.example.community.service.UserService;
import com.example.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: community
 * @description:
 * @author: LK
 * @create: 2023-10-17 21:12
 **/

@Controller
public class HomeController implements CommunityConstant {

    @Autowired
    UserService userService;

    @Autowired
    DiscussPostService discussPostService;

    @Autowired
    LikeService likeService;

    @GetMapping("/index")
    public String getIndexPage(Model model, Page page){
        // 方法调用之前，SpringMVC 会初始化model,page,并把 page 注入到model中
        page.setRows(discussPostService.findDiscussPostRows(0));
        // 用于前端分页路径链接的拼接
        page.setPath("/index");
        List<DiscussPost> list = discussPostService.findDiscussPosts(0, page.getOffset(), page.getLimit());
        List<Map<String,Object>> discussPosts = new ArrayList<>();
        if(list != null) {
            for (DiscussPost discussPost : list) {
                Map<String, Object> map = new HashMap<>();
                map.put("post", discussPost);
                User user = userService.findUserById(discussPost.getUserId());
                map.put("user", user);
                // 帖子点赞数量
                long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, discussPost.getId());
                map.put("likeCount",likeCount);

                discussPosts.add(map);
            }
        }
//        for (Map<String, Object> discussPost : discussPosts) {
//            System.out.println(discussPost);
//
//        }
//        System.out.println(page.getRows());
        model.addAttribute("discussPosts",discussPosts);
        return "/index";
    }
    @GetMapping("/error")
    public String getErrorPage(){
        return "/error/500";
    }






}
