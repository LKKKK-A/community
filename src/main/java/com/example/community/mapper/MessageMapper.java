package com.example.community.mapper;

import com.example.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageMapper{

    // 查询当前用户的会话列表，针对每个会话只返回一条最新的数据
    List<Message> selectConversations(int userId,int offset,int limit);

    // 查询当前用户的会话数量
    int selectConversationCount(int userId);

    // 查询某个会话所包含的私信列表
    List<Message> selectLetters(String conversationId,int offset,int limit);

    // 查询某个会话所包含的私信数量
    int selectLettersCount(String conversationId);

    // 查询未读私信的数量(查询所有会话的未读信息或者某个会话的，复用)
    int selectLettersUnreadCount(int userId,String conversationId);

    //添加信息
    int insertMessage(Message message);

    // 更新状态
    int updateStatus(List<Integer> ids,int status);

    // 查询某个主题的最新通知
    Message selectLatestNotice(int userId,String topic);

    // 查询通知数量
    int selectNoticeCount(int userId,String topic);

    // 查询某个主题未读通知
    int selectUnreadNoticeCount(int userId,String topic);

    // 查询某个主题的通知
    List<Message> selectNotices(int userId,String topic,int offset,int limit);





}
