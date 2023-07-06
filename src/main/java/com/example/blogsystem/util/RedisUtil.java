package com.example.blogsystem.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
@Component
public class RedisUtil {
    @Autowired
    private RedisTemplate redisTemplate;
    public boolean hasKey(String key){
        return redisTemplate.hasKey(key);
    }
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

    public  void  setCache(String key,Object value,int i, TimeUnit timeUnit){
        redisTemplate.opsForValue().set(key,value);
        expire(key,i,timeUnit);
    }

    public List<?> getListCache(String key){
        return redisTemplate.opsForList().range(key,0,-1);
    }
    public List<?> getListCache(String key,Integer start,Integer end){
        return redisTemplate.opsForList().range(key,start,end);
    }
    //设置缓存
    public  void  setListCache(String key,List<?> value){
        redisTemplate.opsForList().rightPush(key,value);
    }
    public  void  setListCache(String key,String value){
        redisTemplate.opsForList().rightPush(key,value);
    }

    public void expire(String key, int i, TimeUnit timeUnit) {
        redisTemplate.expire(key,i,timeUnit);
    }
}