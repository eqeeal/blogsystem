package com.example.blogsystem.service;

import com.example.blogsystem.common.Result;
import com.example.blogsystem.entity.Blog;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author shj
 * @since 2023-06-28
 */
public interface BlogService extends IService<Blog> {

    //发布博客
    Result addBlog(Blog blog, List<Integer> tagList);

    //删除博客
    Result deleteBlog(List<Integer> idList);

    //修改博客
    Result updateBlog(Blog blog,List<Integer> tagList);

    //分页博客
    Result pageBlog(String title,Integer userId,Integer categoryId,Integer tagId,Integer pageSize,Integer pageNum);

    Result getByBlogId(Integer id);

    Map<String,List<String>> getEchartsData(Integer userId);

    Map<String,List<String>> getEchartsDataByBlogId(Integer blogId);
    Result getUserByBlogId(Integer id);

    Result getTagIds(Integer id);

    Result getCountInfo(Integer id);
}
