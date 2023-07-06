package com.example.blogsystem.controller;

import com.example.blogsystem.common.Result;
import com.example.blogsystem.dto.usernameCountDto;
import com.example.blogsystem.mapper.RelTagBlogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
@RequestMapping("/relTagBlog")
public class RelTagBlogController {
    @Autowired
    private RelTagBlogMapper relTagBlogMapper;

    @GetMapping("/tagcountbyblog")
    public List<Integer> tagCountByBlog(){
        List<Integer> list =relTagBlogMapper.getTagCountByBlog();
        return list;
    }

    @GetMapping("/gettagname")
    public List<String> getTagName(){
        return relTagBlogMapper.gettagname();
    }

    @GetMapping("/getblogCount")
    public Result getBlogCount(){
        List<Map<Integer,String>> list =relTagBlogMapper.getblogCount();
//        System.out.println(list);
        return Result.ok(list,"成功");
    }

    @GetMapping("/getCategoryCount")
    public Result getCategoryCount(){
        List<Map<Integer,String>> list =relTagBlogMapper.getCategoryCount();
        int a= list.size();
        System.out.println(list.size());
        return Result.ok(list,"成功");
    }

    @GetMapping("/tagAndCount")
    public Result tagAndCount(){
        List<Map<String, Integer>> maps = relTagBlogMapper.tagAndCount();
        return Result.ok(maps);
    }
}
