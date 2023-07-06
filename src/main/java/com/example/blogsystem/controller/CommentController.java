package com.example.blogsystem.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.blogsystem.common.BaseContext;
import com.example.blogsystem.common.Result;
import com.example.blogsystem.dto.CommentQuray;
import com.example.blogsystem.dto.PageCommentDto;
import com.example.blogsystem.entity.Blog;
import com.example.blogsystem.entity.Comment;
import com.example.blogsystem.service.BlogService;
import com.example.blogsystem.service.CommentService;
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
@RequestMapping("/comment")
public class CommentController {
    @Autowired
    private CommentService commentService;
    @Autowired
    private BlogService blogService;
    @GetMapping("/test")
    public Result<String> test(){
        return Result.ok();
    };
    @PostMapping("/page")
    public Result<Page<PageCommentDto>> page(@RequestBody CommentQuray commentQuray){
        return Result.ok(commentService.getPage(commentQuray),"第"+commentQuray.getPage()+"页");
    }
    @PostMapping("/postMianComment")
    public Result<String> postMianComment(@RequestBody Comment comment){
        commentService.postMianComment(comment);
        return Result.ok("评论成功");
    }
    @GetMapping("/updateMianCommentStatus")
    public Result<String> updateMianCommentStatus(@RequestParam("id") Integer id,@RequestParam("status") Integer status){
        if(commentService.updateMianCommentStatus(id,status)){
            return Result.ok("修改成功");
        }
        return Result.fail("修改失败");
    }
    @GetMapping("/blogNonCount")
    public Result<Map<String,Integer>> blogNonCount(@RequestParam("blogId") Integer blogId){
        return Result.ok(commentService.blogNonCount(blogId));
    }

    @GetMapping("/getById")
    public Result<Comment> getById(Integer id){
        Comment comment=new Comment();
        comment.setId(id);
        return Result.ok(commentService.getById(comment));
    }
}
