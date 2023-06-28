package com.example.blogsystem.service.impl;

import com.example.blogsystem.entity.Comment;
import com.example.blogsystem.mapper.CommentMapper;
import com.example.blogsystem.service.CommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Arrays;

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
    @Override
    public String check(String input) {
        try {
            FileReader fileReader=new FileReader(new File(limitTextFilePath));
            BufferedReader bufferedReader=new BufferedReader(fileReader);
            String str;
            while ((str=bufferedReader.readLine())!=null){
                StringBuilder sb=new StringBuilder();
                sb.append(str.charAt(0));
                int len=str.length();
                for (int i = 1; i < len; i++) {
                    sb.append("*");
                }
                input=input.replaceAll(str,sb.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return input;
    }
}
