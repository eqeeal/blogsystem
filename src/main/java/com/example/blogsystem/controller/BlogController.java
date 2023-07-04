package com.example.blogsystem.controller;

import com.example.blogsystem.common.MyResult;
import com.example.blogsystem.entity.Blog;
import com.example.blogsystem.mapper.BlogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    //某个用户的总文章数
    @GetMapping("countByUserid")
    public MyResult<String> countByuserid(@RequestParam("id") Integer id){
        Integer count=blogMapper.countByUserId(id);
        Integer code=-1;
        if (count > 0) {
            code = 200;
            return new MyResult(code, "查询成功", count);
        }
        return new MyResult(code, "查询失败", null);

    }

}
