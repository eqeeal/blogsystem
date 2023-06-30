package com.example.blogsystem;

import com.example.blogsystem.common.Result;
import com.example.blogsystem.service.BlogService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class BlogsystemApplicationTests {
    @Autowired
    private BlogService blogService;

    @Test
    void contextLoads() {

    }

    @Test
    void queryBlog(){
        Result result = blogService.pageBlog("1", 1,1,1,5, 1);
        Object data = result.getData();
        System.out.println(data);
    }
}
