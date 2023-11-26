package com.example.community.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.community.entity.Message;
import com.example.community.entity.Page;
import com.example.community.entity.User;
import com.example.community.service.MessageService;
import com.example.community.service.UserService;
import com.example.community.util.CommunityConstant;
import com.example.community.util.CommunityUtil;
import com.example.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import javax.jws.WebParam;
import java.lang.reflect.MalformedParameterizedTypeException;
import java.util.*;

/**
 * @program: community
 * @description:
 * @author: LK
 * @create: 2023-11-16 13:36
 **/

@Controller
public class MessageController implements CommunityConstant {

    @Autowired
    private MessageService messageServie;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    public UserService userService;

    //私信列表
    @GetMapping("/letter/list")
    public String getLetterList(Model model, Page page){

        User user = hostHolder.get();

        page.setLimit(5);
        page.setPath("/letter/list");
        page.setRows(messageServie.findConversationCount(user.getId()));

        // 获取会话列表
        List<Message> conversationList = messageServie.findConversations(user.getId(), page.getOffset(), page.getLimit());
        List<Map<String,Object>> conversations = new ArrayList<>();
        for (Message message : conversationList) {
            Map<String, Object> map = new HashMap<>();
            map.put("conversation",message);
            map.put("letterCount",messageServie.findLetterCount(message.getConversationId()));
            map.put("unreadCount",messageServie.findUnreadLetterCount(user.getId(),message.getConversationId()));
            int targetId = user.getId() == message.getFromId() ? message.getToId() : message.getFromId();
            map.put("target",userService.findUserById(targetId));

            conversations.add(map);
        }
        model.addAttribute("conversations",conversations);
        // 全部会话的未读信息
        model.addAttribute("letterUnreadCount",messageServie.findUnreadLetterCount(user.getId(),null));
        //查询全部未读通知数量
        model.addAttribute("noticeUnreadCount",messageServie.findUnreadNoticeCount(user.getId(),null));
        return "/site/letter";
    }

    @GetMapping("/letter/detail/{conversationId}")
    public String getLetterDetail(@PathVariable("conversationId") String conversationId,Model model,Page page){
        User user = hostHolder.get();
        page.setLimit(5);
        page.setRows(messageServie.findLetterCount(conversationId));
        page.setPath("/letter/detail/" + conversationId);

        // 私信列表
        List<Message> letterList = messageServie.findLetters(conversationId, page.getOffset(), page.getLimit());
        List<Map<String,Object>> letters = new ArrayList<>();
        for (Message letter : letterList) {
            Map<String, Object> map = new HashMap<>();
            map.put("letter",letter);
            map.put("fromUser",userService.findUserById(letter.getFromId()));
            letters.add(map);
        }
        model.addAttribute("letters",letters);
        // 私信是谁发的
        User target = getLetterTarget(conversationId);
        model.addAttribute("target",target);

        // 设为已读
        List<Integer> ids = getLetterIds(letterList);
        if(!ids.isEmpty()){
            messageServie.readMessage(ids,1);
        }

        return "/site/letter-detail";

    }

    // 获取未读消息的id
    private List<Integer> getLetterIds(List<Message> letterList){
        List<Integer> list = new ArrayList<>();

        if(letterList != null){
            for (Message letter : letterList) {
                // 判断当前用户是否为letter的收件人，而不是发出者
                if(hostHolder.get().getId() == letter.getToId() && letter.getStatus() == 0){
                    list.add(letter.getId());
                }
            }
        }
        return list;
    }

    private User getLetterTarget(String conversationId){
        User user = hostHolder.get();
        String[] ids = conversationId.split("_");
        int id0 = Integer.parseInt(ids[0]);
        int id1 = Integer.parseInt(ids[1]);
        if(user.getId() == id0){
            return userService.findUserById(id1);
        }else{
            return userService.findUserById(id0);
        }
    }

    @PostMapping("/letter/send")
    @ResponseBody
    public String sendLetter(String content,String toName){
//        Integer.valueOf("abc");
        System.out.println("letter send....");
        User target = userService.findUserByName(toName);
        if(target == null){
            return CommunityUtil.getJSONString(1,"目标用户不存在！");
        }
        Message message = new Message();
        message.setContent(content);
        message.setCreateTime(new Date());
        message.setFromId(hostHolder.get().getId());
        message.setToId(target.getId());
        if(message.getFromId() < message.getToId()){
            // 小的id拼在前边
            message.setConversationId(message.getFromId() + "_" + message.getToId());
        }else{
            message.setConversationId(message.getToId() + "_" + message.getFromId());
        }
        messageServie.addMessage(message);
        return CommunityUtil.getJSONString(0);
    }


    @GetMapping("/notice/list")
    public String getNoticeList(Model model){
        User user = hostHolder.get();

        // 查询评论类通知
        Message message = messageServie.findLatestNotice(user.getId(),TOPIC_COMMENT);
        System.out.println("message:"+message);
        Map<String,Object> commentNoticeVo = new HashMap<>();
        if(message != null){
            commentNoticeVo.put("message",message);
            String content = message.getContent();
            // 把JSONString格式的content转成Map
            content = HtmlUtils.htmlUnescape(content);
            Map map = JSONObject.parseObject(content, HashMap.class);
            commentNoticeVo.put("user",userService.findUserById((Integer) map.get("userId")));
            commentNoticeVo.put("postId",map.get("postId"));
            commentNoticeVo.put("entityType",map.get("entityType"));
            commentNoticeVo.put("entityId",map.get("entityId"));

            int noticeCount = messageServie.findNoticeCount(user.getId(), TOPIC_COMMENT);
            commentNoticeVo.put("count",noticeCount);
            int unreadNoticeCount = messageServie.findUnreadNoticeCount(user.getId(), TOPIC_COMMENT);
            commentNoticeVo.put("unread",unreadNoticeCount);
        }
        model.addAttribute("commentNotice",commentNoticeVo);

        // 查询点赞类通知
        message = messageServie.findLatestNotice(user.getId(),TOPIC_LIKE);
        Map<String,Object> likeNoticeVo = new HashMap<>();
        if(message != null){
            likeNoticeVo.put("message",message);
            String content = message.getContent();
            // 把JSONString格式的content转成Map
            content = HtmlUtils.htmlUnescape(content);
            Map map = JSONObject.parseObject(content, HashMap.class);
            likeNoticeVo.put("user",userService.findUserById((Integer) map.get("userId")));
            likeNoticeVo.put("postId",map.get("postId"));
            likeNoticeVo.put("entityType",map.get("entityType"));
            likeNoticeVo.put("entityId",map.get("entityId"));

            int noticeCount = messageServie.findNoticeCount(user.getId(), TOPIC_LIKE);
            likeNoticeVo.put("count",noticeCount);
            int unreadNoticeCount = messageServie.findUnreadNoticeCount(user.getId(), TOPIC_LIKE);
            System.out.println("unreadNoticeCount="+unreadNoticeCount);
            likeNoticeVo.put("unread",unreadNoticeCount);
        }
        model.addAttribute("likeNotice",likeNoticeVo);


        // 查询关注类通知
        message = messageServie.findLatestNotice(user.getId(),TOPIC_FOLLOW);
        Map<String,Object> followNoticeVo = new HashMap<>();
        if(message != null){
            followNoticeVo.put("message",message);
            String content = message.getContent();
            // 把JSONString格式的content转成Map
            content = HtmlUtils.htmlUnescape(content);
            Map map = JSONObject.parseObject(content, HashMap.class);
            followNoticeVo.put("user",userService.findUserById((Integer) map.get("userId")));
            followNoticeVo.put("entityType",map.get("entityType"));
            followNoticeVo.put("entityId",map.get("entityId"));

            int noticeCount = messageServie.findNoticeCount(user.getId(), TOPIC_FOLLOW);
            followNoticeVo.put("count",noticeCount);
            int unreadNoticeCount = messageServie.findUnreadNoticeCount(user.getId(), TOPIC_FOLLOW);
            followNoticeVo.put("unread",unreadNoticeCount);
        }
        model.addAttribute("followNotice",followNoticeVo);

        // 查询全部未读通知数量
        int unreadNoticeCount = messageServie.findUnreadNoticeCount(user.getId(), null);
        model.addAttribute("noticeUnreadCount",unreadNoticeCount);
        // 查询全部未读私信数量
        int unreadLetterCount = messageServie.findUnreadLetterCount(user.getId(),null);
        model.addAttribute("letterUnreadCount",unreadLetterCount);

        return "/site/notice";
    }

    @GetMapping("/notice/detail/{topic}")
    public String getNoticesDetail(Model model,Page page,@PathVariable("topic") String topic){
        User user = hostHolder.get();

        page.setRows(messageServie.findNoticeCount(user.getId(),topic));
        page.setPath("/notice/detail/" + topic);
        page.setLimit(5);


        List<Message> notices = messageServie.findNotices(user.getId(), topic,page.getOffset(),page.getLimit());
        List<Map<String,Object>> noticeVoList = new ArrayList<>();
        if(notices != null){
            for(Message notice : notices){
                Map<String, Object> map = new HashMap<>();
                // 通知
                map.put("notice",notice);

                String content = notice.getContent();
                content = HtmlUtils.htmlUnescape(content);
                Map<String,Object> data = JSONObject.parseObject(content, HashMap.class);
                // 内容
                map.put("entityType",data.get("entityType"));
                map.put("entityId",data.get("entityId"));
                map.put("user",userService.findUserById((Integer)data.get("userId")));
                map.put("postId",data.get("postId"));
                // 通知作者,系统
                map.put("fromUser",userService.findUserById(notice.getFromId()));

                noticeVoList.add(map);
            }
            model.addAttribute("notices",noticeVoList);
            //设置已读

            List<Integer> ids = getLetterIds(notices);
            if(!ids.isEmpty()){
                messageServie.readMessage(ids,1);
            }
        }
        return "/site/notice-detail";


    }



}
