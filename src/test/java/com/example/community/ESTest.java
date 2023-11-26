package com.example.community;

import com.example.community.mapper.DiscussPostMapper;
import com.example.community.mapper.elasticsearch.DiscussPostRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;

/**
 * @program: community
 * @description:
 * @author: LK
 * @create: 2023-11-23 16:57
 **/

@SpringBootTest
public class ESTest {

    @Autowired
    public DiscussPostMapper discussPostMapper;

    @Autowired
    private DiscussPostRepository discussPostRepository;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Test
    public void test(){
//        discussPostRepository.save(discussPostMapper.selectDiscussPostById(277));
//        discussPostRepository.save(discussPostMapper.selectDiscussPostById(281));
//        discussPostRepository.save(discussPostMapper.selectDiscussPostById(280));
    }




}
