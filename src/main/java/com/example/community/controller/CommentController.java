package com.example.community.controller;

import com.example.community.entity.Comment;
import com.example.community.entity.DiscussPost;
import com.example.community.entity.Event;
import com.example.community.entity.User;
import com.example.community.event.EventProducer;
import com.example.community.service.CommentService;
import com.example.community.service.DiscussPostService;
import com.example.community.service.UserService;
import com.example.community.util.CommunityConstant;
import com.example.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;

/**
 * @program: community
 * @description:
 * @author: LK
 * @create: 2023-11-15 19:32
 **/

@Controller
@RequestMapping("/comment")
public class CommentController implements CommunityConstant {

    @Autowired
    private CommentService commentService;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @PostMapping("/add/{discussPostId}")
    public String addComment(@PathVariable("discussPostId") int discussPostId, Comment comment){
        comment.setUserId(hostHolder.get().getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        commentService.addComment(comment);

        // 触发评论事件
        Event event = new Event();
        event.setUserId(comment.getUserId())
                .setEntityType(comment.getEntityType())
                .setEntityId(comment.getEntityId())
                .setTopic(TOPIC_COMMENT)
                .setData("postId",discussPostId);
        //由于此次评论事件可能是针对帖子和评论的，所以需要判断来setEntityUserId
        if(comment.getEntityType() == ENTITY_TYPE_POST){
            // 针对帖子
            DiscussPost discussPost = discussPostService.findDiscussPost(discussPostId);
            event.setEntityUserId(discussPost.getUserId());
        }else if(comment.getEntityType() == ENTITY_TYPE_COMMENT){
            //针对评论
            User user = userService.findUserById(comment.getTargetId());
            event.setEntityUserId(user.getId());
        }
        // 发布事件
        eventProducer.fireEvent(event);

        return "redirect:/discuss/detail/" + discussPostId;
    }





}
