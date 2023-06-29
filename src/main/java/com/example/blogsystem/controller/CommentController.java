package com.example.blogsystem.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.blogsystem.common.Result;
import com.example.blogsystem.entity.Comment;
import com.example.blogsystem.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    @GetMapping("/page")
    public Result<Page<Comment>> pageResult(@RequestParam Integer page,@RequestParam Integer pageSize,@RequestParam String input){
        Page<Comment> commentPage=new Page<>(page,pageSize);
        LambdaQueryWrapper<Comment> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.like(input!=null,Comment::getContent,input);
        commentService.page(commentPage,queryWrapper);
        return Result.ok(commentPage,"第"+page+"页");
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
}
