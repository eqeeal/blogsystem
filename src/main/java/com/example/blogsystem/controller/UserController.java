package com.example.blogsystem.controller;

import com.example.blogsystem.entity.Result;
import com.example.blogsystem.entity.User;
import com.example.blogsystem.mapper.UserMapper;
import org.apache.ibatis.annotations.Insert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
//@CrossOrigin(originPatterns = )
public class UserController {
    @Autowired
    private UserMapper userMapper;

    @GetMapping("/user/findAll")
    public List<User> index(){
        return userMapper.findAll();
    }

    //    @GetMapping
    @GetMapping("/login")
    public Result<User> login(@RequestParam("userPhone") String userphone, @RequestParam("userPass") String userPass){
        String Message="登录失败";
        int code=-1;
        List<User> user=userMapper.login(userphone,userPass);
        if (user.size()>0){
            Message="登录成功";
            code=200;
        }
        return new Result(code,Message,"");
    }
    @PostMapping("/user/add")
    public Result<User> register(@RequestBody User user){
//        System.out.println(user.toString());
        int code=-1;
        List<User> user1=userMapper.findUserByPhone(user);
        if (user1.size()== 0){
            code=200;
            userMapper.res(user.getUserName(),user.getUserPass(),user.getUserPhone());
            return new Result(code,"注册成功",user);
        }
        return new Result(code,"注册失败",user);
    }
}