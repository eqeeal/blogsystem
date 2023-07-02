package com.example.blogsystem.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.blogsystem.common.Result;
import com.example.blogsystem.dto.CommentQuray;
import com.example.blogsystem.entity.Comment;
import com.example.blogsystem.entity.Recomment;
import com.example.blogsystem.service.RecommentService;
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
@RequestMapping("/recomment")
public class RecommentController {
    @Autowired
    private RecommentService recommentService;
    @GetMapping("/getPageRecommentFromMainComment")
    public Result<Page<Recomment>> getAllRecommentFromMainComment(@RequestBody CommentQuray commentQuray){
        return Result.ok(recommentService.getAllRecommentFromMainComment(commentQuray));
    }
    @GetMapping("/updateRecommentStatus")
    public Result<String> updateRecommentStatus(Integer id,Integer status){
        recommentService.updateRecommentStatus(id,status);
        return Result.ok("修改成功");
    }
    @PostMapping("/postRecomment")
    public Result<String> postRecomment(@RequestBody Recomment recomment){
        recommentService.postRecomment(recomment);
        return Result.ok("发送成功");
    }
}
