package com.example.community.service;

import com.example.community.entity.DiscussPost;
import com.example.community.mapper.DiscussPostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public List<DiscussPost> findDiscussPosts(int id,int offset,int limit){
        List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPosts(id, offset, limit);
        return discussPosts;
    }

    public int findDiscussPostRows(int userId){
        int rows = discussPostMapper.selectDiscussPostRows(userId);
        return rows;
    }

}
























