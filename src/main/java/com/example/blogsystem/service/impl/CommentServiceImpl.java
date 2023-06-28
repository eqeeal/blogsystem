package com.example.blogsystem.service.impl;

import com.example.blogsystem.entity.Comment;
import com.example.blogsystem.mapper.CommentMapper;
import com.example.blogsystem.service.CommentService;
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
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

}
