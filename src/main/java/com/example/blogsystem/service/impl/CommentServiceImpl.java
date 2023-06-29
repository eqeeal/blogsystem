package com.example.blogsystem.service.impl;

import com.example.blogsystem.entity.Comment;
import com.example.blogsystem.mapper.CommentMapper;
import com.example.blogsystem.service.CommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author shj
 * @since 2023-06-28
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {
    @Value("${limit-text-file.path}")
    private String limitTextFilePath;

    @Override
    public void postMianComment(Comment comment) {
        comment.setContent(check(comment.getContent()));
        save(comment);
    }
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Override
    public String check(String input) {
        String key="limitText";
        StringBuilder sb=new StringBuilder();
        List<String> range = redisTemplate.opsForList().range(key, 0, -1);
        if(range!=null&&range.size()!=0){
            for (String str:range
                 ) {
                sb=new StringBuilder();
                sb.append(str.charAt(0));
                int len=str.length();
                for (int i = 1; i < len; i++) {
                    sb.append("*");
                }
                input=input.replaceAll(str,sb.toString());
            }
        }
        else {
            try {
                FileReader fileReader=new FileReader(new File(limitTextFilePath));
                BufferedReader bufferedReader=new BufferedReader(fileReader);
                String str;
                while ((str=bufferedReader.readLine())!=null){
                    redisTemplate.opsForList().rightPush(key,str);
                    sb=new StringBuilder();
                    sb.append(str.charAt(0));
                    int len=str.length();
                    for (int i = 1; i < len; i++) {
                        sb.append("*");
                    }
                    input=input.replaceAll(str,sb.toString());
                }
                redisTemplate.expire(key,30, TimeUnit.MINUTES);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return input;
    }

    @Override
    public boolean updateMianCommentStatus(Integer id,Integer status) {
        Comment comment = getById(id);
        comment.setStatus(status);
        return updateById(comment);
    }
}
