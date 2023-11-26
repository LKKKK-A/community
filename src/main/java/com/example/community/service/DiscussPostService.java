package com.example.community.service;

import com.example.community.entity.DiscussPost;
import com.example.community.mapper.DiscussPostMapper;
import com.example.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * @program: community
 * @description:
 * @author: LK
 * @create: 2023-10-17 20:37
 **/

@Service
public class DiscussPostService {

    @Autowired
    DiscussPostMapper discussPostMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    public List<DiscussPost> findDiscussPosts(int id,int offset,int limit){
        List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPosts(id, offset, limit);
        return discussPosts;
    }

    public int findDiscussPostRows(int userId){
        int rows = discussPostMapper.selectDiscussPostRows(userId);
        return rows;
    }

    public int addDiscussPost(DiscussPost post){
        if(post == null){
            throw new IllegalArgumentException("参数不能为空！");
        }
        // 防止帖子中的数据中有HTML标签，对HTML标签进行转义
        post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
        post.setContent(HtmlUtils.htmlEscape(post.getContent()));
        // 过滤敏感词
        post.setTitle(sensitiveFilter.filter(post.getTitle()));
        post.setContent(sensitiveFilter.filter(post.getContent()));

        return discussPostMapper.insertDiscussPost(post);
    }

    public DiscussPost findDiscussPost(int id){
        return discussPostMapper.selectDiscussPostById(id);
    }

    public int updateCommentCount(int id,int commentCount){
        return discussPostMapper.updateCommentCount(id,commentCount);
    }



}
























