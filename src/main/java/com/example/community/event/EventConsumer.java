package com.example.community.event;

import com.alibaba.fastjson.JSONObject;
import com.example.community.entity.Event;
import com.example.community.entity.Message;
import com.example.community.service.MessageService;
import com.example.community.util.CommunityConstant;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.HashMap;

/**
 * @program: community
 * @description:
 * @author: LK
 * @create: 2023-11-21 15:22
 **/

@Component
public class EventConsumer implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Autowired
    private MessageService messageService;

    //监听消息队列中的事件
    @KafkaListener(topics = {TOPIC_COMMENT,TOPIC_FOLLOW,TOPIC_LIKE})
    public void handleCommentMessage(ConsumerRecord record){
        if(record == null || record.value() == null){
            logger.error("消息内容为空！");
            return;
        }
        Event event = JSONObject.parseObject(record.value().toString(),Event.class);
        if(event == null){
            logger.error("消息格式错误！");
        }
        // 根据消息队列中事件的信息，构建message，发送站内通知
        Message message = new Message();
        message.setConversationId(event.getTopic());
        message.setFromId(SYSTEM_ID);
        message.setToId(event.getEntityUserId());
        message.setCreateTime(new Date());

        //设置message的基本content
        Map<String,Object> content = new HashMap<>();
        content.put("userId",event.getUserId());
        content.put("entityType",event.getEntityType());
        content.put("entityId",event.getEntityId());

        //将event中的一些额外数据也封装到content中
        if(!event.getData().isEmpty()){
            for(Map.Entry<String,Object> entry : event.getData().entrySet()){
                content.put(entry.getKey(),entry.getValue());
            }
        }
        message.setContent(JSONObject.toJSONString(content));
        //将message存到数据库
        messageService.addMessage(message);
    }



}
