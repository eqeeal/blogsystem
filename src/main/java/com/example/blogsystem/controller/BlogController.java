package com.example.blogsystem.controller;

import com.example.blogsystem.common.My;
import com.example.blogsystem.mapper.BlogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.blogsystem.common.Result;
import com.example.blogsystem.entity.Blog;
import com.example.blogsystem.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    private BlogMapper blogMapper;
    @Autowired
    private BlogService blogService;


    //所有博客数量
    @GetMapping("countblog")
        public Integer countBlog(){
            Integer count =blogMapper.countBlog();
            return count;
        }

    //某个用户的总文章数
    @GetMapping("countByUserid")
    public My<String> countByuserid(@RequestParam("id") Integer id){
        Integer count=blogMapper.countByUserId(id);
        Integer code=-1;
        if (count > 0) {
            code = 200;
            return new My(code, "查询成功", count);
        }
        return new My(code, "查询失败", null);

    }

    @PostMapping("/add")
    public Result addBlog(@RequestBody Blog blog,@RequestParam("tagList[]") List<Integer> tagList){
        return blogService.addBlog(blog,tagList);
    }

    @PutMapping("/update")
    public Result updateBlog(@RequestBody Blog blog,@RequestParam("tagList[]") List<Integer> tagList){
        return blogService.updateBlog(blog,tagList);
    }

    @DeleteMapping("/delete")
    public Result deleteBlog(@RequestBody List<Integer> idList){
        return blogService.deleteBlog(idList);
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

    @GetMapping("/getUserByBlogId/{id}")
    public Result getUserByBlogId(@PathVariable Integer id){
        return blogService.getUserByBlogId(id);
    }

    @GetMapping("/getTagIds/{id}")
    public Result getTagIds(@PathVariable()Integer id){
        return blogService.getTagIds(id);
    }
    @GetMapping("/getCountInfo/{id}")
    public Result getCountInfo(@PathVariable Integer id){
        return blogService.getCountInfo(id);
    }
}
