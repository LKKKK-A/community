package com.example.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: community
 * @description:
 * @author: LK
 * @create: 2023-11-09 21:33
 **/

@Component
public class SensitiveFilter {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    // 替换符
    private static final String REPLACEMENT = "***";

    //前缀树根节点
    private TireNode rootNode = new TireNode();

    //在项目启动时就初始化前缀树
    @PostConstruct
    public void init(){
        try(
                // 获取敏感词资源
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                //将字节流转换为字符缓冲流方便读取
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                ){
            String keyword;
            while((keyword = reader.readLine()) != null){
                // 添加到前缀树中
                rootNode.addKeyWord(keyword);
            }
        }catch (IOException e){
            logger.error("加载敏感词文件失败:" + e.getMessage());
        }
    }

    public String filter(String text){
        if(StringUtils.isBlank(text)){
            return null;
        }

        // 指针一
        TireNode curNode = rootNode;
        // 指针二
        int begin = 0;
        // 指针三
        int position = 0;
        // 结果
        StringBuilder sb = new StringBuilder();

        while(begin < text.length()){
            char c = text.charAt(position);
            // 跳过特殊字符
            // 判断是否为特殊字符
            if(isSymbol(c)){
                // 判断curNode是不是rootNode，是的话将c加入sb，并跳过
                if(curNode == rootNode){
                    sb.append(c);
                    ++begin;
                }
                // 不管curNode是不是rootNode,position都需要后移
                position++;
                continue;
            }
            // 如果不是特殊字符，继续判断是不是敏感词
            curNode = curNode.getSubNode(c);
            if(curNode == null){
                // 当前字符在前缀树中找不见
                // 说明begin 开头没有敏感词
                sb.append(text.charAt(begin));
                // 重新指向根节点
                curNode = rootNode;
                // begin进入下一个位置,position也退回来
                position = ++begin;
            }else if(curNode.isKeyWord()){
                // 将begin ~ position替换掉
                sb.append(REPLACEMENT);
                // 以position进入下一个位置
                begin = ++position;
                // 重新指向根节点
                curNode = rootNode;
            }else{
                // 检查下一个字符
                if(position < text.length() - 1){
                    // 如果position到最后了，那么position不再后移，下一次循环时在curNode中找position对应的字符，会是null，因为
                    //前缀树的这条分支已经到头了，就会进入curNode == null 的逻辑，将begin加入sb，并后移begin继续判断
                    position++;
                }
            }
        }
//        //将最后一部分字符加进去
//        sb.append(text.substring(begin));
        return sb.toString();
    }


    // 检测是否为特殊符号
    public boolean isSymbol(Character c){
//        0x2E80~0x9FFF 是东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }


    private class TireNode{
        // 该节点是否是敏感词最后一个字符
        private boolean isWordEnd = false;
        // 子节点
        Map<Character,TireNode> tireNodeMap = new HashMap<Character,TireNode>();

        private void setSubNode(Character c,TireNode node){
            this.tireNodeMap.put(c,node);
        }

        private TireNode getSubNode(Character c){
            return tireNodeMap.get(c);
        }

        private void setKeyWordEnd(boolean isEnd){
            this.isWordEnd = isEnd;
        }

        private boolean isKeyWord(){
            return isWordEnd;
        }

        // 将铭感词插入Tire,构建前缀树
        private void addKeyWord(String keyword){
            TireNode curNode = rootNode;
            for (int i = 0; i < keyword.length(); i++) {
                char c = keyword.charAt(i);
                TireNode subNode = curNode.getSubNode(c);
                if (subNode == null) {
                    // 初始化当前节点的子节点
                    subNode = new TireNode();
                    curNode.setSubNode(c,subNode);
                }
                // 更新节点，进行下一次循环
                curNode = subNode;

                //设置结束标记
                if(i == keyword.length() - 1){
                    curNode.setKeyWordEnd(true);
                }
            }
        }
    }

}
