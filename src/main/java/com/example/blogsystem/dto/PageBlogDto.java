package com.example.blogsystem.dto;

import com.example.blogsystem.entity.Blog;
import lombok.Data;

@Data
public class PageBlogDto extends Blog {
    private Integer nonCount;
    private Integer totalCount;
}
