package com.example.community.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

/**
 * @program: community
 * @description:
 * @author: LK
 * @create: 2023-10-15 22:54
 **/

@Data
// 该实体和es中discusspost这个索引建立联系
@Document(indexName = "discusspost")
public class DiscussPost {

    @Id
    public int id;

    @Field(type = FieldType.Integer)
    public int userId;

    // analyzer：分词器，存的时候分为更多的词条
    // searchAnalyzer：搜索的时候分的少一点，分的太多的话会远离原本意图
    @Field(type = FieldType.Text,analyzer = "ik_max_word",searchAnalyzer = "ik_smart")
    public String title;

    @Field(type = FieldType.Text,analyzer = "ik_max_word",searchAnalyzer = "ik_smart")
    public String content;

    @Field(type = FieldType.Integer)
    public int type;//'0-普通; 1-置顶;

    @Field(type = FieldType.Integer)
    public int status;//'0-正常; 1-精华; 2-拉黑;'

    @Field(type = FieldType.Date)
    public Date createTime;

    @Field(type = FieldType.Integer)
    public int commentCount;

    @Field(type = FieldType.Double)
    public double score;
}
