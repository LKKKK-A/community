package com.example.community.controller.interceptor;

import com.example.community.annotation.LoginRequire;
import com.example.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * @program: community
 * @description:
 * @author: LK
 * @create: 2023-11-08 21:40
 **/

@Component
public class LoginRequireInterceptor implements HandlerInterceptor {
    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 这个拦截器只拦截指定的方法，不拦截静态资源等
        if(handler instanceof HandlerMethod){
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            // 判断该方法是否加了@LoginRequire注解
            Method method = handlerMethod.getMethod();
            LoginRequire loginRequire = method.getAnnotation(LoginRequire.class);
            if(loginRequire != null && hostHolder.get() == null){
                // 需要拦截判断是否登录，没登陆重定向到登陆页面
                response.sendRedirect(request.getContextPath() + "/login");
                return false;
            }
        }
        return true;
    }
}
