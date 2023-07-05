package com.example.blogsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.blogsystem.dto.CommentQuray;
import com.example.blogsystem.entity.Recomment;
import com.example.blogsystem.mapper.RecommentMapper;
import com.example.blogsystem.service.RecommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.blogsystem.util.CommentUtil;
import com.example.blogsystem.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author shj
 * @since 2023-06-28
 */
@Service
public class RecommentServiceImpl extends ServiceImpl<RecommentMapper, Recomment> implements RecommentService {
    private final String redisKey = "Recomment";
    @Override
    public void updateRecommentStatus(Integer id, Integer status) {
        redisUtil.cleanCache(redisKey);
        Recomment recomment = getById(id);
        recomment.setStatus(status);
    }
    @Autowired
    private RedisUtil redisUtil;
    @Override
    public Page<Recomment> getAllRecommentFromMainComment(CommentQuray commentQuray) {
        String key = commentQuray.getRedisKey(redisKey);
        Page<Recomment> recommentPage = new Page<>(commentQuray.getPage(),commentQuray.getPageSize());
        if(redisUtil.hasKey(key)){
            recommentPage= (Page<Recomment>) redisUtil.getCache(key);
        }
        else {
            LambdaQueryWrapper<Recomment> queryWrapper=new LambdaQueryWrapper<>();
            queryWrapper.eq(commentQuray.getCommentId()!=null,Recomment::getCommentId,commentQuray.getCommentId());
            queryWrapper.eq(commentQuray.getPid()!=null,Recomment::getPid,commentQuray.getPid());
            queryWrapper.eq(commentQuray.getUserId()!=null,Recomment::getUserId,commentQuray.getUserId());
            queryWrapper.like(commentQuray.getInput()!=null,Recomment::getContent,commentQuray.getInput());
            queryWrapper.orderByDesc(Recomment::getCreateTime);
            page(recommentPage,queryWrapper);
        }
        return recommentPage;
    }
    @Autowired
    private CommentUtil commentUtil;
    @Override
    public void postRecomment(Recomment recomment) {
        recomment.setContent(commentUtil.check(recomment.getContent()));
        save(recomment);
        redisUtil.cleanCache("BLOGPAGE");
        redisUtil.cleanCache(redisKey);
        redisUtil.cleanCache("Comment");
    }

    @Override
    public Integer getRecomentReCount(Integer id) {
        LambdaQueryWrapper<Recomment> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Recomment::getPid,id);
        return count(queryWrapper);
    }
}
