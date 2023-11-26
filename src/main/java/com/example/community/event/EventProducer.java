package com.example.community.event;

import com.alibaba.fastjson.JSONObject;
import com.example.community.entity.Event;
import com.example.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * @program: community
 * @description:
 * @author: LK
 * @create: 2023-11-21 15:17
 **/

@Component
public class EventProducer implements CommunityConstant {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    //处理事件
    public void fireEvent(Event event){
        // 将时间发送到指定的主题
        kafkaTemplate.send(event.getTopic(), JSONObject.toJSONString(event));
    }


}
