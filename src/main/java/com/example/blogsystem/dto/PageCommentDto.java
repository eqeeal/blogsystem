package com.example.blogsystem.dto;

import com.example.blogsystem.entity.Comment;
import lombok.Data;

@Data
public class PageCommentDto extends Comment {
    private String name;
    private Integer recommentCount;
}
