package com.example.blogsystem.service.impl;

import com.example.blogsystem.entity.User;
import com.example.blogsystem.mapper.UserMapper;
import com.example.blogsystem.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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

}
