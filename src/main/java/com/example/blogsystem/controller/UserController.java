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
            return new Result(code,"注册成功",null);
        }
        return new Result(code,"注册失败",null);
    }

    @PostMapping("/user/delete")
    public Result<Integer> deleteUserById(@RequestBody User user){
        Integer result=userMapper.deleteById(user);
        if (result>0){
            return new Result(200,"删除成功","");
        }

        return new Result(-1,"删除失败","");
    }

    @PostMapping("/user/update")
    public Result<String> updateUser(@RequestBody User user){
        int result= userMapper.updateUser(user);
        if (result>0){
            return new Result(200,"更新成功",user);
        }else {
            return new Result(-1,"更新失败",user);
        }

    }
}
