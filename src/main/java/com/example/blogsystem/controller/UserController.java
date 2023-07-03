package com.example.blogsystem.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.blogsystem.common.BaseContext;
import com.example.blogsystem.common.Result;
import com.example.blogsystem.entity.Comment;
import com.example.blogsystem.entity.User;
import com.example.blogsystem.mapper.UserMapper;
import com.example.blogsystem.service.UserService;
import com.example.blogsystem.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
@RestController
@RequestMapping("/user")
//@CrossOrigin(originPatterns = )
public class UserController {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserService userService;


    @Autowired
    private RedisUtil redisUtil;
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
            BaseContext.setCurrentId(user.get(0).getId().longValue());
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

    @PostMapping("/updatePass")
    public Result<String> updatePass(@RequestBody User user){
        Integer count = userMapper.updatePass(user);
        if(count >0){
            return Result.ok("修改密码成功");
        }else {
            return Result.fail("修改密码失败");
        }
    }

//    @GetMapping
//    public Result<HashMap> paginUsers(@RequestParam("username")String username, @RequestParam("pageNum") Integer pageNum, @RequestParam("pageSize") Integer pageSize){
//        List<User> cacheUsers =(List<User>) redisTemplate.opsForValue().get("paginCache");
//        Integer cacheCount = (Integer) redisTemplate.opsForValue().get("countCache");
//        if(cacheUsers==null || cacheCount ==null){
//            //没有该缓存，先去数据库拿数据，拿到之后再保存到redis中
//            //拿到总条数
//            cacheCount = userMapper.countUsers();
//            //拿到分页查询数据
//            //将pageNum进行转换：pageNum：第几页，但是数据库limit需要的是开始查询的位置
//            pageNum = (pageNum - 1) * pageSize;//开始查询的位置
//            cacheUsers = userMapper.paginUsers(username,pageNum,pageSize);
//            //将查询的数据放入Redis中
//            redisTemplate.opsForValue().set("paginCache",cacheUsers);
//            redisTemplate.opsForValue().set("countCache",cacheCount);
//            System.out.println("分页数据：加入缓存");
//        }else{
//            System.out.println("分页数据：使用缓存");
//        }
//        //可以使用HashMap来保存总条数以及查询结果一起发送给前端
//        HashMap<String,Object> map = new HashMap();
//        map.put("count",cacheCount);
//        map.put("list",cacheUsers);
//        return  new Result(200,"查询成功",map);
//    }
//
@GetMapping("/page1")
public Result<Page<User>> pageResult(@RequestParam("page") Integer page, @RequestParam("pageSize") Integer pageSize){
    Page<User> commentPage=new Page<>(page,pageSize);
    LambdaQueryWrapper<User> queryWrapper=new LambdaQueryWrapper<>();
//    queryWrapper.like(input!=null,Comment::getContent,input);
    userService.page(commentPage,queryWrapper);
    List<User> records = commentPage.getRecords();

    redisUtil.setCache("test",records);
    return Result.ok(commentPage,"第"+page+"页");
}
}
