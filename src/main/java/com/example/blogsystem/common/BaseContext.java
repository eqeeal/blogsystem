package com.example.blogsystem.common;

/**
 * 以线程为作用域，独立存储各个线程的数据
 */
public class BaseContext {
    private static ThreadLocal<Integer> threadLocal=new ThreadLocal<>();

    public static void setCurrentId(Integer id){
        threadLocal.set(id);
    }
    public static Integer getCurrentId(){
        return threadLocal.get();
    }
}
