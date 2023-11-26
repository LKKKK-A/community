package com.example.community.controller;

import com.example.community.annotation.LoginRequire;
import com.example.community.entity.User;
import com.example.community.mapper.UserMapper;
import com.example.community.service.FollowService;
import com.example.community.service.LikeService;
import com.example.community.service.UserService;
import com.example.community.util.CommunityConstant;
import com.example.community.util.CommunityUtil;
import com.example.community.util.HostHolder;
import org.apache.ibatis.annotations.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

/**
 * @program: community
 * @description:
 * @author: LK
 * @create: 2023-10-15 22:55
 **/

@Controller
@RequestMapping("/user")
public class UserController implements CommunityConstant {

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${community.path.fileNamePath}")
    private String fileNamePath;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private FollowService followService;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);


    @LoginRequire
    @GetMapping("/setting")
    public String getSettingPath(){
        return "/site/setting";
    }



    @LoginRequire
    @PostMapping("/upload")
    public String uploadHeader(MultipartFile multipartFile, Model model){
        if(multipartFile == null){
            model.addAttribute("error","头像不能为空！");
            return "/site/setting";
        }
        // 获取头像
        String filename = multipartFile.getOriginalFilename();
        String suffix = filename.substring(filename.lastIndexOf("."));
        // 生成随机文件名
        filename = CommunityUtil.generateUUID() + suffix;

        // 确定文件存放路径
        // 创建空文件
        File file = new File(fileNamePath+"/"+filename);
        //存储文件
        try {
            multipartFile.transferTo(file);
        } catch (IOException e) {
            logger.error("上传文件失败:",e.getMessage());
            throw new RuntimeException("上传文件失败，服务器发生异常！",e);
        }

        // 更新user的headUrl（web访问路径）
        // http://localhost:8081/community/user/header/xxx.png
        User user = hostHolder.get();
        String headUrl = domain + contextPath + "/user/header/"+ filename;
        userService.updateHeader(user.getId(),headUrl);

        return "redirect:/index";
    }

    @GetMapping("/header/{fileName}")
    public void getHeader(HttpServletResponse response, @PathVariable("fileName")String fileName){
        // 服务器的存放路径
        fileName = fileNamePath + "/" + fileName;
        // 文件后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        // 响应图片
        response.setContentType("image/" + suffix);
            // 把文件读入输入流
        try (
                FileInputStream fis = new FileInputStream(fileName);
                // 通过response获取输出流
                ServletOutputStream os = response.getOutputStream();
                ){
            byte[] buffer = new byte[1024];
            int b = 0;
            while((b = fis.read(buffer)) != -1){
                os.write(buffer,0,b);
            }
        } catch (IOException e) {
            logger.error("读取头像失败:" + e.getMessage());
        }
    }

    @PostMapping("/resetPassword")
    public String updatePassword(Model model,String oldPassword,String newPassword){
        System.out.println("fsadfas");
        // 获取当前用户
        User user = hostHolder.get();
        if(user == null){
            model.addAttribute("settingError","您还未登录！");
            return "/site/setting";
        }
        Map<String, Object> map = userService.updatePassword(user.getId(), oldPassword, newPassword);
        if(!map.isEmpty()){
            //登陆失败
            model.addAttribute("settingError",map.get("oldPasswordMsg"));
            return "/site/setting";
        }
        // 修改成功，重新登录
        System.out.println("fasfas");
        return "/site/index";
    }

    @GetMapping("/profile/{userId}")
    public String getProfilePage(@PathVariable("userId") int userId,Model model){
        // 查询user
        User user = userService.findUserById(userId);
        if(user == null){
            throw new RuntimeException("该用户不存在！");
        }
        model.addAttribute("user",user);

        // 查询用户点赞数量
        int likeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount",likeCount);

        // 查询user的关注数量
        long followeeCount = followService.findFolloweeCount(userId, ENTITY_TYPE_USER);
        model.addAttribute("followeeCount",followeeCount);

        // 查询user的粉丝数量
        long followerCount = followService.findFollowerCount(ENTITY_TYPE_USER, userId);
        model.addAttribute("followerCount",followerCount);
        // 判断当前用户是否已经关注该实体
        boolean hasFollowed = false;
        if(hostHolder.get() != null){
            hasFollowed = followService.isFollowed(hostHolder.get().getId(),ENTITY_TYPE_USER,userId);
        }
        model.addAttribute("hasFollowed",hasFollowed);

        return "/site/profile";


    }

}
