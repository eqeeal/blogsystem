package com.example.blogsystem.dto;

import com.example.blogsystem.entity.Blog;
import lombok.Data;

import java.util.List;


@Data
public class BlogDto extends Blog {
    private String categoryName;
}
