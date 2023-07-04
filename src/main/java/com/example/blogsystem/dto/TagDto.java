package com.example.blogsystem.dto;

import com.example.blogsystem.entity.Tag;
import lombok.Data;

@Data
public class TagDto extends Tag {
    private Integer hot;
    private Integer blogCount;
}
