package com.example.blogsystem.controller;

import com.example.blogsystem.common.Result;
import com.example.blogsystem.entity.Tag;
import com.example.blogsystem.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
@RequestMapping("/tag")
public class TagController {
    @Autowired
    private TagService tagService;

    /**
     * 获取所有的
     * @return
     */
    @GetMapping("/findAll")
    public Result<List<Tag>> findAll(){
        List<Tag> list=tagService.findAll();
        return Result.ok(list,"获取成功");
    }

}
