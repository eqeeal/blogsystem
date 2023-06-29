package com.example.blogsystem.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.blogsystem.common.Result;
import com.example.blogsystem.dto.CommentQuray;
import com.example.blogsystem.entity.Comment;
import com.example.blogsystem.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
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
    @GetMapping("/test")
    public Result<String> test(){
        return Result.ok();
    };
    @GetMapping("/page")
    public Result<Page<Comment>> page(@RequestBody CommentQuray commentQuray){
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
}
