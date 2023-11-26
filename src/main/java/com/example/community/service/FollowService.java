package com.example.community.service;

import com.example.community.entity.User;
import com.example.community.mapper.UserMapper;
import com.example.community.util.CommunityConstant;
import com.example.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @program: community
 * @description:
 * @author: LK
 * @create: 2023-11-19 13:59
 **/
@Service
public class FollowService implements CommunityConstant {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserMapper userMapper;

    // 关注
    public void follow(int userId,int entityType,int entityId){
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                // 获取key
                String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
                String followerKey = RedisKeyUtil.getFollowerKey(entityType,entityId);

                operations.multi();
                // 添加用户对某实体的关注
                operations.opsForZSet().add(followeeKey,entityId,System.currentTimeMillis());
                // 添加某实体新增的粉丝
                operations.opsForZSet().add(followerKey,userId,System.currentTimeMillis());
                return operations.exec();
            }
        });
    }

    // 取消关注
    public void unfollow(int userId,int entityType,int entityId){
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {

                // 获取key
                String followeeKey = RedisKeyUtil.getFolloweeKey(userId,entityType);
                String followerKey = RedisKeyUtil.getFollowerKey(entityType,entityId);

                operations.multi();
                operations.opsForZSet().remove(followeeKey,entityId);
                operations.opsForZSet().remove(followerKey,userId);
                return operations.exec();
            }
        });
    }

    // 查询用户关注某是实体的数量
    public long findFolloweeCount(int userId,int entityType){
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId,entityType);
        return redisTemplate.opsForZSet().zCard(followeeKey);
    }

    // 查询某实体的粉丝数量
    public long findFollowerCount(int entityType,int entityId){
        String followerKey = RedisKeyUtil.getFollowerKey(entityType,entityId);
        return redisTemplate.opsForZSet().zCard(followerKey);
    }

    // 查询当前用户是否关注某实体
    public boolean isFollowed(int userId,int entityType,int entityId){
        String followeeKwy = RedisKeyUtil.getFolloweeKey(userId,entityType);
        return redisTemplate.opsForZSet().score(followeeKwy,entityId) != null;
    }

    // 查看某用户的用户关注列表
    public List<Map<String,Object>> findFollowees(int userId,int offset,int limit){
        String followedKey = RedisKeyUtil.getFolloweeKey(userId,ENTITY_TYPE_USER);
        Set<Integer> targetIds = redisTemplate.opsForZSet().reverseRange(followedKey,offset,offset + limit - 1);
        if(targetIds == null){
            return null;
        }
        List<Map<String,Object>> list = new ArrayList<>();
        for (Integer targetId : targetIds) {
            Map<String,Object> map = new HashMap<>();
            // 查user
            User user = userMapper.selectById(targetId);
            map.put("user",user);
            // 查关注时间
            Double score = redisTemplate.opsForZSet().score(followedKey, targetId);
            map.put("followTime",new Date(score.longValue()));

            list.add(map);
        }
        return list;
    }

    // 查询用户的粉丝
    public List<Map<String,Object>> findFollowers(int userId,int offset,int limit){
        String followerKey = RedisKeyUtil.getFollowerKey(ENTITY_TYPE_USER,userId);
        Set<Integer> targetIds = redisTemplate.opsForZSet().reverseRange(followerKey, offset, offset + limit - 1);
        if(targetIds == null){
            return null;
        }

        List<Map<String,Object>> list = new ArrayList<>();
        for (Integer targetId : targetIds) {
            Map<String,Object> map = new HashMap<>();
            User follower = userMapper.selectById(targetId);
            map.put("user",follower);
            Double score = redisTemplate.opsForZSet().score(followerKey, targetId);
            map.put("followTime",new Date(score.longValue()));

            list.add(map);
        }
        return list;
    }









}
