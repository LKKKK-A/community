package com.example.community.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;
import java.util.Map;

/**
 * @program: community
 * @description:
 * @author: LK
 * @create: 2023-10-23 21:48
 **/


public class CommunityUtil {

    // 生成随机字符串
    public static String generateUUID(){
        // 把生成的UUID 中的空格去掉
        return UUID.randomUUID().toString().replaceAll("-","");
    }

    // MD5加密
    // hello -> abc123def456
    // hello + a3f6d -> abc123def456rgs
    public static String md5(String key){
        // isBlank（）:如果key是：" " ,null , "-"的话都会认为是null
        if(StringUtils.isBlank(key)){
            return null;
        }
        // 使用MD5加密，就是上述逻辑
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

    public static String getJSONString(int code,String msg,Map<String,Object> map){
        JSONObject json = new JSONObject();
        json.put("code",code);
        json.put("msg",msg);
        if(map != null){
            Set<String> keySet = map.keySet();
            for (String key : keySet) {
                json.put(key,map.get(key));
            }
        }
        return json.toJSONString();
    }
    public static String getJSONString(int code,String msg){
        return getJSONString(code,msg,null);
    }
    public static String getJSONString(int code){
        return getJSONString(code,null);
    }

    public static void main(String[] args) {
        Map<String, Object> map = new HashMap<>();
        map.put("name","likai");
        map.put("age",12);
        String jsonString = CommunityUtil.getJSONString(0, "ok", map);
        System.out.println(jsonString);
    }




}
