package com.example.blogsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
public class BlogsystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(BlogsystemApplication.class, args);
    }

}
