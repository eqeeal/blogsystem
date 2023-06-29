package com.example.blogsystem.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.TimeUnit;
@Component
public class RedisUtil {
    @Autowired
    private RedisTemplate redisTemplate;
    //清理指定的缓存
    public   void  cleanCache(String key){
        Set<String> keys = redisTemplate.keys("*");
        for(String i : keys){
            if(i.indexOf(key)!=-1){
                System.out.println("清理缓存");
                redisTemplate.delete(i);
                //标记清除状态
                redisTemplate.expire(i, -1, TimeUnit.SECONDS);
            }
        }
    }
    //清理所有的的缓存
    public   void   cleanAll(){
        Set<String> keys = redisTemplate.keys("*");
        for(String i : keys){
            redisTemplate.delete(i);
        }
    }
    //获取缓存
    public  Object  getCache(String key){
        return redisTemplate.opsForValue().get(key);
    }
    //设置缓存
    public  void  setCache(String key,Object value){
        redisTemplate.opsForValue().set(key,value);
    }

}