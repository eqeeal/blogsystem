package com.example.blogsystem.service.impl;

import com.example.blogsystem.entity.Category;
import com.example.blogsystem.mapper.CategoryMapper;
import com.example.blogsystem.service.CategoryService;
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
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

}
