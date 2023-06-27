package com.example.blogsystem.entity;

import lombok.Data;

@Data
public class User {
    private int id;//用户唯一标识
    private String userName;//用户名
    private String userPass;//用户密码
    private String userPhone;//手机号
    private String userAvatar;//用户头像地址
    private String userEmail;//用户邮箱
    private String create_time;
    private String updata_time;
}
