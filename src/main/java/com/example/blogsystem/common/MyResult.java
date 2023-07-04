package com.example.blogsystem.common;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MyResult<T> {
    private Integer code;
    private String message;
    private T data;

}