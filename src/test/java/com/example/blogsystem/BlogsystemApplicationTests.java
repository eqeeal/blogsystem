package com.example.blogsystem;

import com.example.blogsystem.service.CommentService;
import com.example.blogsystem.service.TagService;
import com.example.blogsystem.util.RedisUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class BlogsystemApplicationTests {
    @Autowired
    private RedisUtil redisUtil;

    @Test
    void contextLoads() {

    }

    @Test
    void func(){
    }

}
