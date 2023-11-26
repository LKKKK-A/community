package com.example.community.controller;

import com.example.community.entity.Comment;
import com.example.community.entity.DiscussPost;
import com.example.community.entity.Page;
import com.example.community.entity.User;
import com.example.community.service.CommentService;
import com.example.community.service.DiscussPostService;
import com.example.community.service.LikeService;
import com.example.community.service.UserService;
import com.example.community.util.CommunityConstant;
import com.example.community.util.CommunityUtil;
import com.example.community.util.HostHolder;
import org.omg.PortableServer.POA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @program: community
 * @description:
 * @author: LK
 * @create: 2023-11-12 16:45
 **/

@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private LikeService likeService;

    @PostMapping("/add")
    @ResponseBody
    public String addDiscussPost(String title, String content) {
        User user = hostHolder.get();
        if (user == null) {
            // 403 : 没有权限
            return CommunityUtil.getJSONString(403, "您还没有登录！");
        }
        DiscussPost discussPost = new DiscussPost();
        discussPost.setTitle(title);
        discussPost.setContent(content);
        discussPost.setUserId(user.getId());
        discussPost.setCreateTime(new Date());
        discussPostService.addDiscussPost(discussPost);
        System.out.println(discussPost);

        // 出现的异常这里不做处理，将来统一处理
        return CommunityUtil.getJSONString(0, "发布成功！");
    }

    @GetMapping("/detail/{discussPostId}")
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model, Page page) {
        // 查询帖子
        DiscussPost post = discussPostService.findDiscussPost(discussPostId);
        model.addAttribute("post", post);
        // 查询user
        User user = userService.findUserById(post.getUserId());
        model.addAttribute("user", user);
        // 查询点赞数量
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST,post.getId());
        model.addAttribute("likeCount",likeCount);
        // 查询当前用户对帖子的点赞状态
        int likeStatus = hostHolder.get() == null ? 0 :
                likeService.findEntityLikeStatus(hostHolder.get().getId(),ENTITY_TYPE_POST, post.getId());
        model.addAttribute("likeStatus",likeStatus);

        page.setPath("/discuss/detail/" + discussPostId);
        page.setRows(post.getCommentCount());
        page.setLimit(5);

        // 查询帖子评论和评论的回复
        // comment:评论，需要分页
        // reply:评论的回复

        List<Comment> commentList = commentService.findCommentsByEntity(
                ENTITY_TYPE_POST, post.getId(), page.getOffset(), page.getLimit());
        // 评论列表
        List<Map<String, Object>> commentVoList = new ArrayList<>();
        for (Comment comment : commentList) {
            HashMap<String, Object> commentVo = new HashMap<>();
            commentVo.put("comment", comment);
            commentVo.put("user", userService.findUserById(comment.getUserId()));
            // 查询评论点赞数量
            likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT,comment.getId());
            commentVo.put("likeCount",likeCount);
            // 点赞状态
            likeStatus = hostHolder.get() == null ? 0 :
                    likeService.findEntityLikeStatus(hostHolder.get().getId(),ENTITY_TYPE_COMMENT,comment.getId());
            commentVo.put("likeStatus",likeStatus);
            // 回复需要分页，一次性全部查出来
            List<Comment> replyList = commentService.findCommentsByEntity(ENTITY_TYPE_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);
            if (replyList != null) {
                // 回复列表
                List<Map<String, Object>> replyVoList = new ArrayList<>();
                for (Comment reply : replyList) {
                    Map<String, Object> replyVo = new HashMap<>();
                    replyVo.put("reply", reply);
                    replyVo.put("user", userService.findUserById(reply.getUserId()));
                    // 查询评论的回复的点赞数量
                    likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT,reply.getId());
                    replyVo.put("likeCount",likeCount);
                    // 点赞状态
                    likeStatus = hostHolder.get() == null ? 0 :
                            likeService.findEntityLikeStatus(hostHolder.get().getId(),ENTITY_TYPE_COMMENT,reply.getId());
                    replyVo.put("likeStatus",likeStatus);
                    // 回复的目标
                    User target = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
                    replyVo.put("target", target);
                    replyVoList.add(replyVo);
                }
                commentVo.put("replys", replyVoList);
            }
            // 每条评论的回复数量
            commentVo.put("replyCount", commentService.selectCountsByEntity(ENTITY_TYPE_COMMENT, comment.getId()));
            commentVoList.add(commentVo);
        }
        model.addAttribute("comments",commentVoList);
        return "/site/discuss-detail";
    }


}
