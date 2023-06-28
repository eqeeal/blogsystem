package com.example.blogsystem.controller;

import com.example.blogsystem.common.Result;
import com.example.blogsystem.entity.User;
import com.example.blogsystem.mapper.UserMapper;
import com.example.blogsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/user")
//@CrossOrigin(originPatterns = )
public class UserController {
    @Autowired
    private UserMapper userMapper;

    @GetMapping("/findAll")
    public List<User> index(){
        return userMapper.findAll();
    }

    //    @GetMapping
    @GetMapping("/login")
    public Result<String> login(@RequestParam("userPhone") String userPhone, @RequestParam("userPass") String userPass){
        String Message="登录失败";
        List<User> user=userMapper.login(userPhone,userPass);
        if (user.size()>0){
            Message="登录成功";
            return Result.ok(Message);
        }
        return Result.fail(Message);
    }
    @PostMapping("/add")
    public Result<String> register(@RequestBody User user){
//        System.out.println(user.toString());
        List<User> user1=userMapper.findUserByPhone(user);
        if (user1.size()== 0){
            userMapper.res(user.getUserName(),user.getUserPass(),user.getUserPhone());
            return Result.ok("注册成功");
        }
        return Result.fail("注册失败");
    }

    @PostMapping("/delete")
    public Result<String> deleteUserById(@RequestBody User user){
        Integer result=userMapper.deleteById(user);
        if (result>0){
            return Result.ok("删除成功");
        }
        return Result.fail("删除失败");
    }
    @PostMapping("/update")
    public Result<String> updateUser(@RequestBody User user){

        int result= userMapper.updateUser(user);
        if (result>0){
            return Result.ok("更新成功");
        }else {
            return Result.fail("更新失败");
        }

    }
}
