package com.example.blogsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.blogsystem.dto.CommentQuray;
import com.example.blogsystem.entity.Comment;
import com.example.blogsystem.mapper.CommentMapper;
import com.example.blogsystem.service.CommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.blogsystem.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.*;
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
    private RedisUtil redisUtil;
    @Override
    public String check(String input) {
        String key="limitText";
        StringBuilder sb;
        if(redisUtil.hasKey(key)){
            List<String> range = (List<String>) redisUtil.getListCache(key);
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
                    redisUtil.setListCache(key,str);
                    sb=new StringBuilder();
                    sb.append(str.charAt(0));
                    int len=str.length();
                    for (int i = 1; i < len; i++) {
                        sb.append("*");
                    }
                    input=input.replaceAll(str,sb.toString());
                }
                redisUtil.expire(key,30,TimeUnit.MINUTES);
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
    @Override
    public Page<Comment> getPage(CommentQuray commentQuray) {
        String key = commentQuray.getRedisKey("Comment");
        Page<Comment> commentPage=new Page<>(commentQuray.getPage(),commentQuray.getPageSize());
        if(redisUtil.hasKey(key)){
            commentPage = (Page<Comment>) redisUtil.getCache(key);
        }
        else {
            LambdaQueryWrapper<Comment> queryWrapper=new LambdaQueryWrapper<>();
            queryWrapper.eq(commentQuray.getBlogId()!=null,Comment::getBlogId,commentQuray.getBlogId());
            queryWrapper.eq(commentQuray.getUserId()!=null,Comment::getUserId,commentQuray.getUserId());
            queryWrapper.like(commentQuray.getInput()!=null,Comment::getContent,commentQuray.getInput());
            queryWrapper.orderByDesc(Comment::getCreateTime);
            page(commentPage,queryWrapper);
            if (commentPage.getTotal()!=0)
                redisUtil.setCache(key,commentPage,5,TimeUnit.MINUTES);
        }
        return commentPage;
    }
}
