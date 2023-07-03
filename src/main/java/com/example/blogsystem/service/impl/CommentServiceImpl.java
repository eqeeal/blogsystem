package com.example.blogsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.blogsystem.dto.CommentQuray;
import com.example.blogsystem.dto.PageCommentDto;
import com.example.blogsystem.entity.Comment;
import com.example.blogsystem.entity.User;
import com.example.blogsystem.mapper.CommentMapper;
import com.example.blogsystem.service.CommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.blogsystem.service.UserService;
import com.example.blogsystem.util.CommentUtil;
import com.example.blogsystem.util.RedisUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
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
    private UserService userService;
    @Autowired
    private CommentUtil commentUtil;
    @Override
    public void postMianComment(Comment comment) {
        comment.setContent(commentUtil.check(comment.getContent()));
        save(comment);
    }
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public boolean updateMianCommentStatus(Integer id,Integer status) {
        Comment comment = getById(id);
        comment.setStatus(status);
        redisUtil.cleanCache(redisKey);
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
                tmp.setUserAvatar(user.getUserAvatar());
                return tmp;
            }).collect(Collectors.toList());
            pageCommentDtoPage.setRecords(list);
            if (commentPage.getTotal()!=0)
                redisUtil.setCache(key,pageCommentDtoPage,5,TimeUnit.MINUTES);
        }
        return pageCommentDtoPage;
    }
}
