package com.example.blogsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.blogsystem.common.Result;
import com.example.blogsystem.dto.BlogDto;
import com.example.blogsystem.entity.Blog;
import com.example.blogsystem.entity.Category;
import com.example.blogsystem.entity.RelTagBlog;
import com.example.blogsystem.entity.User;
import com.example.blogsystem.mapper.BlogMapper;
import com.example.blogsystem.service.BlogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.blogsystem.service.CategoryService;
import com.example.blogsystem.service.RelTagBlogService;
import com.example.blogsystem.service.UserService;
import com.example.blogsystem.util.CommentUtil;
import com.example.blogsystem.util.RedisUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.example.blogsystem.common.RedisConst.*;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author shj
 * @since 2023-06-28
 */
@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements BlogService {

    @Autowired
    private RelTagBlogService relTagBlogService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private UserService userService;
    @Autowired
    private CommentUtil commentUtil;

    //发布博客
    @Override
    @Transactional
    public Result addBlog(Blog blog,List<Integer> tagList) {
        //保存到blog表
        //存储富文本路径
        String path = new String();
        try {
            path = commentUtil.store(blog.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        blog.setPath(path);
        this.save(blog);
        //添加信息到tb_rel_tag_blog表
        for(int i=0;i<tagList.size();i++){
            RelTagBlog relTagblog = new RelTagBlog();
            relTagblog.setBlogId(blog.getId());
            relTagblog.setTagId(tagList.get(i));
            relTagBlogService.save(relTagblog);
        }
        //删除分页缓存
        redisUtil.cleanCache(BLOGPAGE);
        return Result.ok("发布成功");
    }

    //删除
    @Override
    @Transactional
    public Result deleteBlog(List<Integer> idList) {
        //删blog表信息
        this.removeByIds(idList);
        //删除tb_rel_tag_blog表信息
        QueryWrapper<RelTagBlog> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("blog_id",idList);
        relTagBlogService.remove(queryWrapper);

        //删缓存
        idList.forEach((id)->{
            redisUtil.cleanCache(BLOG+":"+id);
        });

        //删除分页缓存
        redisUtil.cleanCache(BLOGPAGE);

        return Result.ok("删除成功");
    }

    //修改
    @Override
    @Transactional
    public Result updateBlog(Blog blog,List<Integer> tagList) {
        //更新blog表
        //存储富文本路径
        String path = new String();
        try {
            path = commentUtil.store(blog.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        blog.setPath(path);
        this.updateById(blog);

        //更新tb_rel_tag_blog表
        //先删除
        QueryWrapper<RelTagBlog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("blog_id",blog.getId());
        relTagBlogService.remove(queryWrapper);

        //后添加
        for(int i=0;i<tagList.size();i++){
            RelTagBlog relTagblog = new RelTagBlog();
            relTagblog.setBlogId(blog.getId());
            relTagblog.setTagId(tagList.get(i));
            relTagBlogService.save(relTagblog);
        }

        //删缓存
        redisUtil.cleanCache(BLOG+":"+blog.getId());
        //删除分页缓存
        redisUtil.cleanCache(BLOGPAGE);
        return Result.ok("修改成功");
    }

    //查询
    @Override
    public Result pageBlog(String title,Integer userId,Integer categoryId,Integer tagId,Integer pageSize, Integer pageNum) {
        //分页
        Page<Blog> page = new Page<>(pageNum,pageSize);

        //查指定标签的分页数据的blog_id
        //查询tb_rel_tag_blog表
        List<RelTagBlog> relTagBlogList = relTagBlogService.query().select("distinct blog_id")
                .eq(tagId!=null,"tag_id", tagId).list();

        List<Integer> blogIdList = relTagBlogList.stream().map(relTagBlog -> {
            return relTagBlog.getBlogId();
        }).collect(Collectors.toList());
        //拼接key
        String key = BLOGPAGE+":"+(title==null?-1:title)
                +","+(userId==null?-1:userId)+","+(categoryId==null?-1:categoryId)
                +","+(tagId==null?-1:tagId)+","+pageSize+","+pageNum;

        //redis查询是否有缓存
        Page cacheRecords = (Page<BlogDto>)redisUtil.getCache(key);
        //有缓存，直接返回
        if(cacheRecords!=null)return Result.ok(cacheRecords);

        //无缓存，查询数据库
        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(userId!=null,"user_id",userId)
                .eq(categoryId!=null,"category_id",categoryId)
                .in(blogIdList.size()!=0,"id",blogIdList)
                .like(title!=null,"title",title)
                .orderByDesc("create_time");
        this.page(page,queryWrapper);
        //copy属性值
        //封装Dto Page
        Page<BlogDto> page1 = new Page<>();
        BeanUtils.copyProperties(page,page1,"recordes");
        //查询分类名称
        List<Blog> records = page.getRecords();
        List<BlogDto> blogDtoList = records.stream().map(blog -> {
            BlogDto blogDto = new BlogDto();
            BeanUtils.copyProperties(blog,blogDto);
            Category one = categoryService.query().select("category_name").eq("id", blog.getCategoryId()).one();
            blogDto.setCategoryName(one.getCategoryName());

            //富文本文件
            String path = new String();
            try {
              path = commentUtil.read(blog.getPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            blogDto.setPath(path);
            return blogDto;
        }).collect(Collectors.toList());
        page1.setRecords(blogDtoList);

        //加入redis缓存,设置过期时间
        redisUtil.setCache(key,page1,BLOG_TTL,TimeUnit.MINUTES);
        return Result.ok(page1);
    }

    private Blog redisQueryBlog(Blog blog) {
        Integer id = blog.getId();
        //查询缓存
        Blog blog1 = (Blog) redisUtil.getCache(BLOG + ":" + id);
        //命中
        if (blog1 != null) {
            BeanUtils.copyProperties(blog1, blog);
        } else {
            //未命中,查询数据库
            Blog blog2 = query().eq("id", id).one();
            if(blog2==null)return null;
            BeanUtils.copyProperties(blog2, blog);
            //添加缓存
            String key = BLOG+":"+id;
            redisUtil.setCache(key,blog,BLOG_TTL,TimeUnit.MINUTES);
        }
        return blog;
    }

    @Override
    public Result getByBlogId(Integer id) {
        Blog blog = new Blog();
        blog.setId(id);
        blog = redisQueryBlog(blog);
        //富文本
        String path = new String();
        try {
            path = commentUtil.read(blog.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        blog.setPath(path);
        if(blog == null)return Result.fail("博客不存在");
        return Result.ok(blog);
    }

    @Override
    public Result getUserByBlogId(Integer id) {
        Blog one = this.query().select("user_id").eq("id", id).one();
        User user = userService.query().select("user_name").eq("id", one.getUserId()).one();
        return Result.ok(user.getUserName());
    }

    @Override
    public Result getTagIds(Integer id) {
        List<RelTagBlog> list = relTagBlogService.query().select("tag_id").eq("blog_id", id).list();
        List<Integer> tagIds = new ArrayList<>();
        list.forEach(one->{
            tagIds.add(one.getTagId());
        });
        return Result.ok(tagIds);
    }

    @Override
    public Result getCountInfo(Integer id) {
        //blog数量
        List<Blog> blogList = this.query().eq("user_id", id).list();
        Integer blogCount = blogList.size();
        //分类数量
        Integer categoryCount = this.query().select("distinct category_id").eq("user_id", id).count();
        //标签数量
        List<Integer> idList = new ArrayList<>();
        blogList.forEach(one->{
            idList.add(one.getId());
        });
        Integer tagCount = relTagBlogService.query().in("blog_id", idList).count();

        Map<String,Integer> mp = new HashMap<>();
        mp.put("blogCount",blogCount);
        mp.put("categoryCount",categoryCount);
        mp.put("tagCount",tagCount);
        return Result.ok(mp);
    }
}
