package com.example.blogsystem;

import com.example.blogsystem.entity.Tag;
import com.example.blogsystem.service.TagService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BlogsystemApplicationTests {
    @Autowired
    private TagService tagService;

    @Test
    void contextLoads() {

    }
    @Test
    void func(){
        for (int i = 0; i < 100; i++) {
            Tag tag=new Tag();
            tag.setTagName("tag"+i);
            tagService.save(tag);
        }
    }


}
