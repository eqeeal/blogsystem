package com.example.blogsystem.controller;
import com.example.blogsystem.common.My;
import com.example.blogsystem.entity.User;
import com.example.blogsystem.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.api.R;
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

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
//@CrossOrigin(originPatterns = )
public class UserController {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private UserService userService;

    //获取所有用户信息
    @GetMapping("findAll")
    public My<User> index() {
        List<User> list =userMapper.findAll();
        return  new My(200,"查询成功",list);
    }


    @PostMapping("getbyPhone")
    public My<User> getbyPhone(@RequestParam("userphone")String userPhone){
        int code = -1;
        List<User> list = userMapper.findUserByPhone(userPhone);
        if (list.size() > 0) {
            code = 200;
        }
        return new My(code, "查询成功", list);
    }


    //删除某个用户
    @PostMapping("delete")
    public My<Integer> deleteUserById(@RequestBody User user) {
        Integer result = userMapper.deleteById(user);
        redisUtil.cleanCache("tb_user");
        if (result > 0) {
            return new My(200, "删除成功", "");
        }

        return new My(-1, "删除失败", "");
    }

    //更新
    @PostMapping("update")
    public My<String> updateUser(@RequestBody User user) {
        redisUtil.cleanCache("tb_user");
        List<User> list = userMapper.findUserById(user.getId());
        if (list.size() > 0) {
            int count = userMapper.updateUser(user);
            if (count > 0) {
                return new My(200, "更新成功", user);
            } else {
                return new My(-1, "更新失败", user);
            }
        } else {
            return new My(-1, "无该用户", user);
        }
    }

    //    @GetMapping
    @GetMapping("/login")
    public Result<Integer> login(@RequestParam("userPhone") String userPhone, @RequestParam("userPass") String userPass, HttpSession session){
        String Message="登录失败";
        List<User> user=userMapper.login(userPhone,userPass);
        if (user.size()>0){
            Message="登录成功";
            BaseContext.setCurrentId(user.get(0).getId());
            session.setAttribute("userId",user.get(0).getId());
            return Result.ok(user.get(0).getId(),Message);
        }
        return Result.fail(Message);
    }
    @PostMapping("/add")
//    public Result<Integer> register(@RequestBody User user) {
//        user.setUserName(user.getUserPhone());
//    }
    public Result<User> register(@RequestBody User user){
//        System.out.println(user.toString());
        List<User> user1=userMapper.findUserByPhone(user.getUserPhone());
        if (user1.size()== 0){
//            userMapper.res(user.getUserName(),user.getUserPass(),user.getUserPhone());
            userService.save(user);
//            userMapper.res(user.getUserName(),user.getUserPass(),user.getUserPhone());
            BaseContext.setCurrentId(user.getId());
            return Result.ok(user,"注册成功");
        }
        return Result.fail("注册失败");
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

//获取当前用户信息（头像、名字）
    @GetMapping("/getNowUserInfo")
        public Result getNowUserInfo(@RequestParam String phone){
        User user = userService.query().eq("user_phone", phone).one();
        Map<String,String> mp = new HashMap<String,String>(){{
            put("userName",user.getUserName());
            put("userAvatar",user.getUserAvatar());
            put("userId",user.getId().toString());
        }};
        return Result.ok(mp);
    }

    //获取当前用户信息（id）
    @GetMapping("/getNowUserId")
    public Result getNowUserId(@RequestParam String phone){
        User user = userService.query().eq("user_phone", phone).one();
        return Result.ok(user.getId());
    }

    //分页查询
    @GetMapping("page")
    public My<HashMap> paginUsers(@RequestParam("username") String username, @RequestParam("pageNum") Integer pageNum, @RequestParam("pageSize") Integer pageSize) {
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
        return new My(200, "查询成功", map);
    }

    //批量删除
    @PostMapping("delUsers")
    public My<Integer> delUsers(@RequestBody Map<String,List<Integer>> data){
        List<Integer> ids =data.get("ids");
        Integer count = userMapper.delUsers(ids);
        if(count >0){
        return new My(200,"批量删除成功",count);
        }else{
            return new My(-1,"批量删除失败",-1);
        }
    }

}
