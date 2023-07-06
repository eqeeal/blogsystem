package com.example.blogsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.blogsystem.dto.CommentQuray;
import com.example.blogsystem.dto.PageCommentDto;
import com.example.blogsystem.entity.Comment;
import com.example.blogsystem.entity.Recomment;
import com.example.blogsystem.entity.User;
import com.example.blogsystem.mapper.CommentMapper;
import com.example.blogsystem.service.BlogService;
import com.example.blogsystem.service.CommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.blogsystem.service.RecommentService;
import com.example.blogsystem.service.UserService;
import com.example.blogsystem.util.CommentUtil;
import com.example.blogsystem.util.RedisUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author shj
 * @since 2023-06-28
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {
    private final String redisKey="Comment";
    @Autowired
    private RecommentService recommentService;
    @Autowired
    private UserService userService;
    @Autowired
    private CommentUtil commentUtil;
    @Override
    public void postMianComment(Comment comment) {
        comment.setContent(commentUtil.check(comment.getContent()));
        save(comment);
        redisUtil.cleanCache("BLOGPAGE");
        redisUtil.cleanCache(redisKey);
    }
    @Autowired
    private RedisUtil redisUtil;
    @Override
    public boolean updateMianCommentStatus(Integer id,Integer status) {
        Comment comment = getById(id);
        comment.setStatus(status);
        redisUtil.cleanCache(redisKey);
        redisUtil.cleanCache("BLOGPAGE");
        return updateById(comment);
    }
    @Override
    public Page<PageCommentDto> getPage(CommentQuray commentQuray) {
        String key = commentQuray.getRedisKey(redisKey);
        Page<Comment> commentPage=new Page<>(commentQuray.getPage(),commentQuray.getPageSize());
        Page<PageCommentDto> pageCommentDtoPage=new Page<>(commentQuray.getPage(),commentQuray.getPageSize());
        if(redisUtil.hasKey(key)){
            pageCommentDtoPage = (Page<PageCommentDto>) redisUtil.getCache(key);
        }
        else {
            LambdaQueryWrapper<Comment> queryWrapper=new LambdaQueryWrapper<>();
            queryWrapper.eq(commentQuray.getStatus()!=null,Comment::getStatus,commentQuray.getStatus());
            queryWrapper.eq(commentQuray.getBlogId()!=null,Comment::getBlogId,commentQuray.getBlogId());
            queryWrapper.eq(commentQuray.getUserId()!=null,Comment::getUserId,commentQuray.getUserId());
            queryWrapper.like(commentQuray.getTime()!=null,Comment::getCreateTime,commentQuray.getTime());
            queryWrapper.like(commentQuray.getInput()!=null,Comment::getContent,commentQuray.getInput());
            queryWrapper.orderByDesc(Comment::getCreateTime);
            page(commentPage,queryWrapper);
            BeanUtils.copyProperties(commentPage,pageCommentDtoPage,"records");
            List<PageCommentDto> list=commentPage.getRecords().stream().map((x)->{
                PageCommentDto tmp=new PageCommentDto();
                BeanUtils.copyProperties(x,tmp);
                User user = userService.getById(x.getUserId());
                tmp.setName(user.getUserName());
                LambdaQueryWrapper<Recomment> recommentLambdaQueryWrapper=new LambdaQueryWrapper<>();
                recommentLambdaQueryWrapper.eq(Recomment::getCommentId,x.getId());
                tmp.setRecommentCount(recommentService.count(recommentLambdaQueryWrapper));
                return tmp;
            }).collect(Collectors.toList());
            pageCommentDtoPage.setRecords(list);
            if (commentPage.getTotal()!=0&&(commentQuray.getStatus()==null||commentQuray.getStatus()!=2))
                redisUtil.setCache(key,pageCommentDtoPage,5,TimeUnit.MINUTES);
        }
        return pageCommentDtoPage;
    }

    @Override
    public Map<String,Integer> blogNonCount(Integer blogId) {
        Integer ans1=0;
        Integer ans2=0;
        LambdaQueryWrapper<Comment> commentLambdaQueryWrapper=new LambdaQueryWrapper<>();
        commentLambdaQueryWrapper.eq(Comment::getBlogId,blogId);
        LambdaQueryWrapper<Recomment> recommentLambdaQueryWrapper=new LambdaQueryWrapper<>();
        LambdaQueryWrapper<Recomment> recommentLambdaQueryWrapper1=new LambdaQueryWrapper<>();
        List<Comment> comments = this.list(commentLambdaQueryWrapper);
        for (Comment comment:comments
             ) {
            recommentLambdaQueryWrapper1.eq(Recomment::getCommentId,comment.getId());
            ans2+=recommentService.count(recommentLambdaQueryWrapper1)+1;
            recommentLambdaQueryWrapper.eq(Recomment::getCommentId,comment.getId());
            recommentLambdaQueryWrapper.eq(Recomment::getStatus,2);
            ans1+=recommentService.count(recommentLambdaQueryWrapper)+comment.getStatus()==2?1:0;
        }
        if(ans1!=0)redisUtil.cleanCache("BLOGPAGE");
        Map<String,Integer> map=new HashMap<>();
        map.put("nonCount",ans1);
        map.put("total",ans2);
        return map;
    }
}
