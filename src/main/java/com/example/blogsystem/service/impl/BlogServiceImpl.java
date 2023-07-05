package com.example.blogsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.blogsystem.common.Result;
import com.example.blogsystem.dto.PageBlogDto;
import com.example.blogsystem.entity.Blog;
import com.example.blogsystem.entity.Comment;
import com.example.blogsystem.entity.Recomment;
import com.example.blogsystem.entity.RelTagBlog;
import com.example.blogsystem.mapper.BlogMapper;
import com.example.blogsystem.service.BlogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.blogsystem.service.CommentService;
import com.example.blogsystem.service.RecommentService;
import com.example.blogsystem.service.RelTagBlogService;
import com.example.blogsystem.util.RedisUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
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
    private CommentService commentService;
    @Autowired
    private RecommentService recommentService;

    //发布博客
    @Override
    @Transactional
    public Result addBlog(Blog blog,List<Integer> tagList) {
        //保存到blog表
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
    public Result deleteBlog(Integer id) {
        //删数据库
        this.removeById(id);
        //删除tb_rel_tag_blog表信息
        QueryWrapper<RelTagBlog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("blog_id",id);
        relTagBlogService.remove(queryWrapper);

        //删缓存
        redisUtil.cleanCache(BLOG+":"+id);
        //删除分页缓存
        redisUtil.cleanCache(BLOGPAGE);

        return Result.ok("删除成功");
    }

    //修改
    @Override
    @Transactional
    public Result updateBlog(Blog blog,List<Integer> tagList) {
        //更新blog表
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
        List<Blog> cacheRecords = (List<Blog>)redisUtil.getCache(key);
        //有缓存，直接返回
        if(cacheRecords!=null)return Result.ok(page.setRecords(cacheRecords));

        //无缓存，查询数据库
        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(userId!=null,"user_id",userId)
                .eq(categoryId!=null,"category_id",categoryId)
                .in(blogIdList.size()!=0,"id",blogIdList)
                .like(title!=null,"title",title);
        this.page(page,queryWrapper);
        //加入redis缓存,设置过期时间
        Page<PageBlogDto> blogDtoPage=new Page<>(pageNum,pageSize);
        BeanUtils.copyProperties(page,blogDtoPage,"records");
        List<PageBlogDto> list=page.getRecords().stream().map((x)->{
            PageBlogDto pageBlogDto = new PageBlogDto();
            BeanUtils.copyProperties(x,pageBlogDto);
            Map<String, Integer> map = commentService.blogNonCount(x.getId());
            pageBlogDto.setNonCount(map.get("nonCount"));
            pageBlogDto.setTotalCount(map.get("total"));
            return pageBlogDto;
        }).collect(Collectors.toList());
        blogDtoPage.setRecords(list);
        redisUtil.setCache(key,blogDtoPage.getRecords(),BLOG_TTL,TimeUnit.MINUTES);
        return Result.ok(blogDtoPage);
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
        if(blog == null)return Result.fail("博客不存在");
        return Result.ok(blog);
    }

    @Override
    public Map<String, List<String>> getEchartsData(Integer userId) {
        LambdaQueryWrapper<Blog> blogLambdaQueryWrapper=new LambdaQueryWrapper<>();
        blogLambdaQueryWrapper.eq(Blog::getUserId,userId);
        List<Blog> blogList = list(blogLambdaQueryWrapper);
        List<String> da=new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            da.add(String.format("前%d天",30-i*5));
        }
        int[] arr=new int[6];
        blogList.forEach((blog)->{
            LambdaQueryWrapper<Comment> commentLambdaQueryWrapper=new LambdaQueryWrapper<>();
            commentLambdaQueryWrapper.eq(Comment::getBlogId,blog.getId());
            List<Comment> commentList = commentService.list(commentLambdaQueryWrapper);
            commentList.forEach((comment)->{
                LambdaQueryWrapper<Recomment> recommentLambdaQueryWrapper=new LambdaQueryWrapper<>();
                recommentLambdaQueryWrapper.eq(Recomment::getCommentId,comment.getId());
                List<Recomment> recommentList = recommentService.list(recommentLambdaQueryWrapper);
                recommentList.forEach((recomment)->{
                    long until = recomment.getCreateTime().until(LocalDateTime.now(), ChronoUnit.DAYS);
                    if(until<31)
                        arr[(int) (until/5)]+=1;
                });
                long until = comment.getCreateTime().until(LocalDateTime.now(), ChronoUnit.DAYS);
                if(until<31)
                    arr[(int) (until/5)]+=1;
            });
        });
        List<String> stringList=new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            stringList.add(arr[5-i]+"");
        }
        Map<String, List<String>> map=new HashMap<>();
        map.put("dateData",da);
        map.put("dataData",stringList);
        return map;
    }

    @Override
    public Map<String, List<String>> getEchartsDataByBlogId(Integer blogId) {
        LambdaQueryWrapper<Blog> blogLambdaQueryWrapper=new LambdaQueryWrapper<>();
        blogLambdaQueryWrapper.eq(Blog::getId,blogId);
        Blog blog = getOne(blogLambdaQueryWrapper);
        LambdaQueryWrapper<Comment> commentLambdaQueryWrapper=new LambdaQueryWrapper<>();
        commentLambdaQueryWrapper.eq(Comment::getBlogId,blog.getId());
        List<Comment> commentList = commentService.list(commentLambdaQueryWrapper);
        List<String> da=new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            da.add(String.format("前%d天",30-i*5));
        };
        int[] arr=new int[6];
        commentList.forEach((comment)->{
            LambdaQueryWrapper<Recomment> recommentLambdaQueryWrapper=new LambdaQueryWrapper<>();
            recommentLambdaQueryWrapper.eq(Recomment::getCommentId,comment.getId());
            List<Recomment> recommentList = recommentService.list(recommentLambdaQueryWrapper);
            recommentList.forEach((recomment)->{
                long until = recomment.getCreateTime().until(LocalDateTime.now(), ChronoUnit.DAYS);
                if(until<31)
                    arr[(int) (until/5)]+=1;
            });
            long until = comment.getCreateTime().until(LocalDateTime.now(), ChronoUnit.DAYS);
            if(until<31)
                arr[(int) (until/5)]+=1;
        });
        List<String> stringList=new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            stringList.add(arr[5-i]+"");
        }
        Map<String, List<String>> map=new HashMap<>();
        map.put("dateData",da);
        map.put("dataData",stringList);
        return map;
    }
}
