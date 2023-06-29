package com.example.blogsystem.dto;

import lombok.Data;

@Data
public class CommentQuray {
    private Integer blogId;
    private Integer userId;
    private Integer commentId;
    private String input;

    private Integer page=1;
    private Integer pageSize=5;

    private Integer pid;

    public String getRedisKey(String name){
        return String.format("%s:%s-%s-%s-%s-%s-%s-%s",name,blogId,userId,commentId,input,page,pageSize,pid);
    };

}
