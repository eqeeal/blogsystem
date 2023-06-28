package com.example.blogsystem.service.impl;

import com.example.blogsystem.entity.Blog;
import com.example.blogsystem.mapper.BlogMapper;
import com.example.blogsystem.service.BlogService;
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
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements BlogService {

}
