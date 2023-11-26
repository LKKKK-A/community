package com.example.community.controller.interceptor;

import com.example.community.entity.User;
import com.example.community.service.DataService;
import com.example.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @program: community
 * @description:
 * @author: LK
 * @create: 2023-11-25 16:16
 **/

@Component
public class DateInterceptor implements HandlerInterceptor {


    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private DataService dataService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 统计UV
        String ip = request.getRemoteUser();
        dataService.recordUV(ip);

        // 统计DAU
        // 判断是否登录
        User user = hostHolder.get();
        if(user != null){
            dataService.recordDAU(user.getId());
        }
        return true;
    }
}
