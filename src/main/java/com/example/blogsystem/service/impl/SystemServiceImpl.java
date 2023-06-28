package com.example.blogsystem.service.impl;

import com.example.blogsystem.entity.System;
import com.example.blogsystem.mapper.SystemMapper;
import com.example.blogsystem.service.SystemService;
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
public class SystemServiceImpl extends ServiceImpl<SystemMapper, System> implements SystemService {

}
