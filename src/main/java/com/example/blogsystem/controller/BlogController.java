package com.example.blogsystem.controller;

import com.example.blogsystem.common.Result;
import com.example.blogsystem.entity.Blog;
import com.example.blogsystem.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
@RequestMapping("/blog")
public class BlogController {
    @Autowired
    private BlogService blogService;


    @PostMapping("/add")
    public Result addBlog(@RequestBody Blog blog,@RequestParam List<Integer> tagList){
        return blogService.addBlog(blog,tagList);
    }

    @PutMapping("/update")
    public Result updateBlog(@RequestBody Blog blog,@RequestParam List<Integer> tagList){
        return blogService.updateBlog(blog,tagList);
    }

    @DeleteMapping("/delete/{id}")
    public Result deleteBlog(@PathVariable Integer id){
        return blogService.deleteBlog(id);
    }

    @GetMapping("/page")
    public Result pageBlog(@RequestParam(value = "title",required = false) String title,
                           @RequestParam(value = "userId",required = false)Integer userId,
                           @RequestParam(value = "categoryId",required = false)Integer categoryId,
                           @RequestParam(value = "tagId",required = false)Integer tagId,
                           @RequestParam("pageSize") Integer pageSize,@RequestParam("pageNum") Integer pageNum){
        return blogService.pageBlog(title,userId,categoryId,tagId,pageSize,pageNum);
    }

    @GetMapping("/getByBlogId/{id}")
    public Result getByBlogId(@PathVariable Integer id){
        return blogService.getByBlogId(id);
    }

}
