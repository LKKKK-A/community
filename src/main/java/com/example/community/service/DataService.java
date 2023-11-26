package com.example.community.service;

import com.example.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @program: community
 * @description:
 * @author: LK
 * @create: 2023-11-25 15:17
 **/


@Service
public class DataService {

    @Autowired
    private RedisTemplate redisTemplate;

    private SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");

    // 将指定ip计入uv
    public void recordUV(String ip){
        String uvKey = RedisKeyUtil.getUVKey(df.format(new Date()));
        redisTemplate.opsForHyperLogLog().add(uvKey,ip);
    }

    // 统计区间独立访客uv
    public long calendarUV(Date startDate,Date endDate){
        if(startDate == null || endDate == null){
            throw new IllegalArgumentException("参数不能为空！");
        }
        // 整理该日期范围内的key
        List<String> keylist = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        while(!calendar.getTime().after(endDate)){
            String uvKey = RedisKeyUtil.getUVKey(df.format(calendar.getTime()));
            keylist.add(uvKey);
            // 循环，日期加一
            calendar.add(Calendar.DATE,1);
        }
        // 合并这些数据
        String redisKey = RedisKeyUtil.getUVKey(df.format(startDate), df.format(endDate));
        redisTemplate.opsForHyperLogLog().union(redisKey,keylist.toArray());

        //返回统计结果
        return redisTemplate.opsForHyperLogLog().size(redisKey);
    }

    // 将指定用户计入dau(每日活跃用户)
    public void recordDAU(int userId){
        // 获取key
        String dauKey = RedisKeyUtil.getDAUKey(df.format(new Date()));
        // 将userId存入到当天的dau中
        redisTemplate.opsForValue().setBit(dauKey,userId,true);
    }

    // 统计指定区间的dau
    public long calendarDAU(Date startDate,Date endDate){
        if(startDate == null || endDate == null){
            throw new IllegalArgumentException("参数不能为空!");
        }
        // 整理指定区间的daukey
        List<byte[]> keyList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        while(!calendar.getTime().after(endDate)){
            String key = RedisKeyUtil.getDAUKey(df.format(calendar.getTime()));
            keyList.add(key.getBytes());
            calendar.add(Calendar.DATE,1);
        }

        // 将区间的key左or运算，因为dau只需要user在指定区间内的一天中为true就行
        long obj = (long) redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                // 合并key
                String unionDAUKey = RedisKeyUtil.getDAUKey(df.format(startDate), df.format(endDate));
                // 对keyList中的bitmap进行或运算
                connection.bitOp(RedisStringCommands.BitOperation.OR, unionDAUKey.getBytes(), keyList.toArray(new byte[0][0]));

                // 返回统计后的结果
                return connection.bitCount(unionDAUKey.getBytes());
            }
        });
        return obj;


    }








}
