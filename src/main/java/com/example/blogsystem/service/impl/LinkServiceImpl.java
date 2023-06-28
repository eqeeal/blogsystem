package com.example.blogsystem.service.impl;

import com.example.blogsystem.entity.Link;
import com.example.blogsystem.mapper.LinkMapper;
import com.example.blogsystem.service.LinkService;
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
public class LinkServiceImpl extends ServiceImpl<LinkMapper, Link> implements LinkService {

}
