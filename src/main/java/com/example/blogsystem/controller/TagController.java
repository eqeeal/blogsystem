package com.example.blogsystem.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.blogsystem.common.Result;
import com.example.blogsystem.entity.Tag;
import com.example.blogsystem.service.TagService;
import com.example.blogsystem.util.RedisUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author shj
 * @since 2023-06-28
 */
@RestController
@RequestMapping("/tag")
public class TagController {
    @Autowired
    private TagService tagService;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 获取所有的
     * @return
     */
    @GetMapping("/findAll")
    public Result<Page<Tag>> pageTags(@RequestParam Integer page,@RequestParam Integer pageSize,@RequestParam(required = false) String input){
        //分页构造器
        Page<Tag> pageInfo=new Page<>(page,pageSize);
        String key="cacheTags:"+page+"-"+pageSize+"-"+input;
        Page<Tag> cacheTags= (Page<Tag>) redisUtil.getCache(key);
//        Long cacheCount= (Long) redisUtil.getCache("cacheCount");
//        if (cacheCount!=null && cacheTags!=null){
        if (cacheTags!=null){
            //说明redis的cache中有缓存数据，
//            HashMap<String, Object> map = new HashMap<>();
//            map.put("count",cacheCount);
//            map.put("list",cacheTags);
            return  Result.ok(cacheTags);
        } else {
            //没有该缓存，先去数据库拿数据，拿到之后再保存到redis中
            //拿到总条数
//            cacheCount=tagService.countTags(input);
//            cacheTags=tagService.findTags(input);
            //分页构造器
//            Page<Tag> pageInfo=new Page<>(page,pageSize);

            //条件构造器
            LambdaQueryWrapper<Tag> queryWrapper=new LambdaQueryWrapper<>();
            queryWrapper.like(input!=null,Tag::getTagName,input);

            //查询分页
            tagService.page(pageInfo,queryWrapper);
            cacheTags=pageInfo;
//            cacheCount=pageInfo.getTotal();
            //缓存
            redisUtil.setCache(key,pageInfo);
//            redisUtil.setCache("cacheCount",pageInfo.getTotal());

            return Result.ok(pageInfo,"获取成功");
        }
    }

    @PostMapping("/regist")
    public void registTag(@RequestBody Tag tag){
    }
    @GetMapping("/findAllTags")
    public Result<List<Tag>> findAllTags(){
        List<Tag> tagList= tagService.findAll();
        System.out.println(tagList.toString());
        if (tagList.size()>0){
            return Result.ok(tagList,"所有标签");
        }else {
            return Result.fail("没有标签");
        }

    }
}
