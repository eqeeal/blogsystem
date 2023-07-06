package com.example.blogsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.blogsystem.entity.Comment;
import com.example.blogsystem.entity.Recomment;
import com.example.blogsystem.entity.User;
import com.example.blogsystem.mapper.UserMapper;
import com.example.blogsystem.service.CommentService;
import com.example.blogsystem.service.RecommentService;
import com.example.blogsystem.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private RecommentService recommentService;
    @Override
    public User getByPid(Integer id) {
        if(id==null){
            return null;
        }
        else {
            LambdaQueryWrapper<Recomment> recommentLambdaQueryWrapper=new LambdaQueryWrapper<>();
            recommentLambdaQueryWrapper.eq(Recomment::getId,id);
            Recomment recomment = recommentService.getOne(recommentLambdaQueryWrapper);
            User user=new User();
            user.setId(recomment.getUserId());
            return user=getById(user);
        }
    }

}
