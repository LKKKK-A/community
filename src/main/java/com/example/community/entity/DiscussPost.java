package com.example.community.entity;

import lombok.Data;

import java.util.Date;

/**
 * @program: community
 * @description:
 * @author: LK
 * @create: 2023-10-15 22:54
 **/

@Data
public class DiscussPost {
    public int id;
    public int userId;

    public String title;
    public String content;
    public int type;//'0-普通; 1-置顶;',
    public int status;//'0-正常; 1-精华; 2-拉黑;'

    public Date createTime;
    public int commentCount;
    public double score;
}
