package com.example.community.service;

import com.example.community.entity.Message;
import com.example.community.mapper.MessageMapper;
import com.example.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * @program: community
 * @description:
 * @author: LK
 * @create: 2023-11-16 13:28
 **/

@Service
public class MessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    SensitiveFilter sensitiveFilter;

    public List<Message> findConversations(int userId,int offset,int limit){
        return messageMapper.selectConversations(userId,offset,limit);
    }

    public int findConversationCount(int userId){
        return messageMapper.selectConversationCount(userId);
    }

    public List<Message> findLetters(String conversationId,int offset,int limit){
        return messageMapper.selectLetters(conversationId,offset,limit);
    }

    public int findLetterCount(String conversationId){
        return messageMapper.selectLettersCount(conversationId);
    }

    public int findUnreadLetterCount(int userId,String conversationId){
        return messageMapper.selectLettersUnreadCount(userId,conversationId);
    }

    public int addMessage(Message message){
        // 过滤敏感词
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        message.setContent(sensitiveFilter.filter(message.getContent()));
        return messageMapper.insertMessage(message);
    }

    public int readMessage(List<Integer> ids,int status){
        return messageMapper.updateStatus(ids,status);
    }


    public Message findLatestNotice(int userId,String topic){
        return messageMapper.selectLatestNotice(userId,topic);
    }

    public int findNoticeCount(int userId,String topic){
        return messageMapper.selectNoticeCount(userId,topic);
    }

    public int findUnreadNoticeCount(int userId,String topic){
        return messageMapper.selectUnreadNoticeCount(userId,topic);
    }


    public List<Message> findNotices(int userId,String topic,int offset,int limit){
        return messageMapper.selectNotices(userId,topic,offset,limit);
    }






}
