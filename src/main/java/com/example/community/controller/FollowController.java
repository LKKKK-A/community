package com.example.community.controller;

import com.example.community.annotation.LoginRequire;
import com.example.community.entity.Event;
import com.example.community.entity.User;
import com.example.community.entity.Page;
import com.example.community.event.EventProducer;
import com.example.community.service.FollowService;
import com.example.community.service.UserService;
import com.example.community.util.CommunityConstant;
import com.example.community.util.CommunityUtil;
import com.example.community.util.HostHolder;
import com.example.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.beans.EventSetDescriptor;
import java.util.List;
import java.util.Map;

/**
 * @program: community
 * @description:
 * @author: LK
 * @create: 2023-11-19 14:25
 **/

@Controller
public class FollowController implements CommunityConstant {

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private FollowService followService;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private UserService userService;

    @PostMapping("/follow")
    @ResponseBody
    @LoginRequire
    public String follow(int entityType,int entityId){
        User user = hostHolder.get();
        // 关注
        followService.follow(user.getId(),entityType,entityId);

        // 触发关注事件
        Event event = new Event()
                .setTopic(TOPIC_FOLLOW)
                .setEntityType(entityType)
                .setEntityId(entityId)
                .setEntityUserId(entityId)
                .setUserId(hostHolder.get().getId());
        eventProducer.fireEvent(event);

        return CommunityUtil.getJSONString(0,"关注成功");
    }

    @PostMapping("/unfollow")
    @ResponseBody
    @LoginRequire
    public String unfollow(int entityType,int entityId){
        User user = hostHolder.get();
        // 取消关注
        followService.unfollow(user.getId(),entityType,entityId);

        return CommunityUtil.getJSONString(0,"取消关注成功!");
    }

    @GetMapping("/followees/{userId}")
    public String getFollowee(@PathVariable("userId") int userId, Page page, Model model){
        User user = userService.findUserById(userId);
        if(user == null){
            throw new RuntimeException("该用户不存在！");
        }
        model.addAttribute("user",user);

        page.setLimit(5);
        page.setPath("/followees/" + userId);
        page.setRows((int) followService.findFolloweeCount(userId,ENTITY_TYPE_USER));
        List<Map<String, Object>> followees = followService.findFollowees(userId, page.getOffset(), page.getLimit());

        // 将自己是否关注followees添加到map中
        if(followees != null) {
            for (Map<String, Object> map : followees) {
                User u = (User) map.get("user");
                map.put("hasFollowed", hasFollowed(u.getId()));
            }
        }

        model.addAttribute("users",followees);
        return "/site/followee";

    }

    @GetMapping("/followers/{userId}")
    public String getFollower(@PathVariable("userId") int userId, Page page, Model model){
        User user = userService.findUserById(userId);
        if(user == null){
            throw new RuntimeException("该用户不存在！");
        }
        model.addAttribute("user",user);

        page.setLimit(5);
        page.setPath("/followers/" + userId);
        page.setRows((int) followService.findFollowerCount(ENTITY_TYPE_USER,userId));
        List<Map<String, Object>> followers = followService.findFollowers(userId, page.getOffset(), page.getLimit());

        // 将自己是否关注followees添加到map中
        if(followers != null) {
            for (Map<String, Object> map : followers) {
                User u = (User) map.get("user");
                map.put("hasFollowed", hasFollowed(u.getId()));
            }
        }
        model.addAttribute("users",followers);
        return "/site/follower";

    }

    public boolean hasFollowed( int userId){
        User user = hostHolder.get();
        if(user != null){
            return followService.isFollowed(user.getId(),ENTITY_TYPE_USER,userId);
        }
        return false;
    }








}
