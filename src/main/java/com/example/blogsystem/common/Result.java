package com.example.blogsystem.common;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {
    private Boolean success;
    private String Msg;
    private T data;
    private Long total;

    public static <T> Result<T> ok(){
        return new Result<>(true, null, null, null);
    }
    public static <T> Result<T> ok(T data){
        return new Result<>(true, null, data, null);
    }

    public static <T> Result<T> ok(T data,String Msg){
        return new Result<>(true, Msg, data, null);
    }

    public static <T> Result<T> ok(T data, Long total){
        return new Result<>(true, null, data, total);
    }
    public static <T> Result<T> fail(String Msg){
        return new Result<>(false, Msg, null, null);
    }

}
