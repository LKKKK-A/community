package com.example.community.service;

import com.example.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

/**
 * @program: community
 * @description:
 * @author: LK
 * @create: 2023-11-18 14:23
 **/

@Service
public class LikeService {

    @Autowired
    private RedisTemplate redisTemplate;

    public void like(int userId,int entityType,int entityId, int entityUserId){

        // 获取key
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType,entityId);
        String userLikeKey = RedisKeyUtil.getUserLikeKey(entityUserId);
        // 涉及到两次的更新操作，要保证事务
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                // 判断当前用户对该实体有没有点过赞
                // 要在事务之外获取isMember，redis事务之内是不会实际执行的，而是在事务提交之后才统一执行
                Boolean isMember = operations.opsForSet().isMember(entityLikeKey, userId);
                // 开启事务
                operations.multi();
                if(isMember){
                    // 已经点过赞，第二次就取消点赞
                    operations.opsForSet().remove(entityLikeKey,userId);
                    // 被赞用户的点赞数减一
                    operations.opsForValue().decrement(userLikeKey);
                }else{
                    // 第一次点赞
                    operations.opsForSet().add(entityLikeKey,userId);
                    // 被赞用户的点赞数加一
                    operations.opsForValue().increment(userLikeKey);
                }
                return operations.exec();
            }
        });

    }

    // 统计点赞数量
    public long findEntityLikeCount(int entityType ,int entityId){
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType,entityId);
        return redisTemplate.opsForSet().size(entityLikeKey);
    }

    // 判断当前用户对某实体的点赞状态
    public int findEntityLikeStatus(int userId,int entityType,int entityId){
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType,entityId);
        return redisTemplate.opsForSet().isMember(entityLikeKey,userId) ? 1 : 0;
    }

    // 获取某用户的获得的点赞数
    public int findUserLikeCount(int userId){
        String userLikeKey = RedisKeyUtil.getUserLikeKey(userId);
        Integer count = (Integer) redisTemplate.opsForValue().get(userLikeKey);
        return count == null ? 0 : count.intValue();
    }








}
