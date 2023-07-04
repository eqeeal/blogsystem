package com.example.blogsystem.controller;

import com.example.blogsystem.common.MyResult;
import com.example.blogsystem.entity.User;
import com.example.blogsystem.mapper.UserMapper;
import com.example.blogsystem.service.UserService;
import com.example.blogsystem.util.RedisUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
//@CrossOrigin(originPatterns = )
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisUtil redisUtil;

    //获取所有用户信息
    @GetMapping("findAll")
    public MyResult<User> index() {
        List<User> list =userMapper.findAll();
        return  new MyResult(200,"查询成功",list);
    }

    //登录
    @GetMapping("login")
    public MyResult<String> login(@RequestParam("userPhone") String userPhone, @RequestParam("userPass") String userPass) {
        String Message = "登录失败";
        int code = -1;
        List<User> user = userMapper.login(userPhone, userPass);
        if (user.size() > 0) {
            Message = "登录成功";
            code = 200;
        }
        return new MyResult(code, Message, user);
    }

    @PostMapping("getbyPhone")
    public  MyResult<User> getbyPhone(@RequestParam("userphone")String userPhone){
        int code = -1;
        List<User> list = userMapper.findUserByPhone(userPhone);
        if (list.size() > 0) {
            code = 200;
        }
        return new MyResult(code, "查询成功", list);
    }

    @PostMapping("add")
    public MyResult<User> register(@RequestBody User user) {
        redisUtil.cleanCache("tb_user");
        System.out.println(user.toString());
        int code = -1;
        List<User> user1 = userMapper.findUserByPhone(user.getUserPhone());
        if (user1.size() == 0) {
            code = 200;
            userMapper.res(user.getUserName(), user.getUserPass(), user.getUserPhone());
            return new MyResult(code, "注册成功", null);
        }
        return new MyResult(code, "注册失败", null);
    }

    //删除某个用户
    @PostMapping("delete")
    public MyResult<Integer> deleteUserById(@RequestBody User user) {
        Integer result = userMapper.deleteById(user);
        redisUtil.cleanCache("tb_user");
        if (result > 0) {
            return new MyResult(200, "删除成功", "");
        }

        return new MyResult(-1, "删除失败", "");
    }

    //更新
    @PostMapping("update")
    public MyResult<String> updateUser(@RequestBody User user) {
        redisUtil.cleanCache("tb_user");
        List<User> list = userMapper.findUserById(user.getId());
        if (list.size() > 0) {
            int count = userMapper.updateUser(user);
            if (count > 0) {
                return new MyResult(200, "更新成功", user);
            } else {
                return new MyResult(-1, "更新失败", user);
            }
        } else {
            return new MyResult(-1, "无该用户", user);
        }

    }

    //分页查询
    @GetMapping("page")
    public MyResult<HashMap> paginUsers(@RequestParam("username") String username, @RequestParam("pageNum") Integer pageNum, @RequestParam("pageSize") Integer pageSize) {
        Integer countCache = (Integer) redisUtil.getCache("tb_user:count:"+pageNum+"-"+pageSize+":"+"username"+":"+username);//获取值
        List<User> cacheUsers = (List<User>) redisUtil.getCache("tb_user:"+pageNum+"-"+pageSize+":"+"username"+":"+username);
        if (cacheUsers == null || countCache == null) {//判断是否有值
            //没有该缓存，先去数据库拿数据，拿到之后再保存到redis中
            //拿到总条数
            countCache = userMapper.userTotalCount(username);
            //分页查询
            //将pageNum进行转换，pageNum,第几页，但数据库limit是要的开始查询的位置
            Integer start = (pageNum - 1) * pageSize;
            cacheUsers = userMapper.paginUsers(username, start, pageSize);
            redisUtil.setCache("tb_user:"+pageNum+"-"+pageSize+":"+"username"+":"+username,cacheUsers);//新增一个值，set(K key,V value),key是键，value是值
            redisUtil.setCache("tb_user:count:"+pageNum+"-"+pageSize+":"+"username"+":"+username,countCache);
            System.out.println("分页数据：加入缓存");
        } else {
            System.out.println("分页数据：使用缓存");
        }
        //用HashMap保存总条数以及查询结果
        HashMap<String, Object> map = new HashMap();
        map.put("count", countCache);
        map.put("list", cacheUsers);
        return new MyResult(200, "查询成功", map);
    }

    //批量删除
    @PostMapping("delUsers")
    public MyResult<Integer> delUsers(@RequestBody Map<String,List<Integer>> data){
        List<Integer> ids =data.get("ids");
        Integer count = userMapper.delUsers(ids);
        if(count >0){
        return new MyResult(200,"批量删除成功",count);
        }else{
            return new MyResult(-1,"批量删除失败",-1);
        }
    }

}
