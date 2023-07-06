package com.example.blogsystem;

import com.example.blogsystem.entity.Tag;
import com.example.blogsystem.service.CommentService;
import com.example.blogsystem.service.TagService;
import com.example.blogsystem.util.RedisUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Random;


@SpringBootTest
class BlogsystemApplicationTests {
    @Autowired
    private RedisUtil redisUtil;

    @Test
    void contextLoads() {

    }
    @Autowired
    private TagService tagService;
    @Test
    void func(){
        String str="abcdefghijklmnopq123456";
        Random random=new Random();
        for (int i = 0; i < 100; i++) {
            Tag tag=new Tag();
            StringBuilder sb=new StringBuilder();
            for (int j = 0; j < 6; j++) {
                sb.append(str.charAt(random.nextInt(str.length())));
            }
            tag.setTagName(sb.toString());
            tagService.save(tag);
        }
    }

}
