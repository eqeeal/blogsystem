package com.example.blogsystem.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
@AllArgsConstructor
@Data
public class Result<T> {
    private int code;//当前请求处理的结果状态码，一般200为成功，-1为失败
    private String Message;//请求操作 返回描述文本，比如登录成功，修改失败、删除成功
    private T data;//当前请求处理后要返回的数据，根据实际情况来处理

//    public Result<T> success() {
//        this.code=200;
//
//    }
}
