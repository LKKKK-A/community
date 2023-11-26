package com.example.community.controller.advice;

import com.example.community.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @program: community 啊撒打发
 * @description:
 * @author: LK
 * @create: 2023-11-17 11:58
 **/

//@ControllerAdvice(annotations = Controller.class)
public class ExceptionAdvice {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);

    @ExceptionHandler({Exception.class})
    public void exceptionHandler(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 记录日志
        logger.error("服务器发生异常:", e.getMessage());
        // 记录栈信息
        for(StackTraceElement element : e.getStackTrace()){
            logger.error(element.toString());
        }
        // 如果是异步请求
        String XRequestedWith = request.getHeader("x-requested-with");
        if("XMLHttpRequest".equals(XRequestedWith)){
            // 说明时异步请求，返回一个json格式的字符串】
            response.setContentType("application/plain;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.write(CommunityUtil.getJSONString(1,"服务器异常"));
        }else{
            // 普通请 求
            response.sendRedirect(request.getContextPath() + "/error");
        }
    }


}
