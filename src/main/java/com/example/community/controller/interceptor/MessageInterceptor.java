package com.example.community.controller.interceptor;

import com.example.community.entity.User;
import com.example.community.service.MessageService;
import com.example.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @program: community
 * @description:
 * @author: LK
 * @create: 2023-11-22 14:57
 **/

@Component
public class MessageInterceptor implements HandlerInterceptor {

    @Autowired
    private MessageService messageService;

    @Autowired
    private HostHolder hostHolder;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.get();
        if(user != null){
            int unreadNoticeCount = messageService.findUnreadNoticeCount(user.getId(), null);
            int unreadLetterCount = messageService.findUnreadLetterCount(user.getId(), null);
            modelAndView.addObject("allUnreadCount",unreadLetterCount + unreadNoticeCount);
        }

    }
}
