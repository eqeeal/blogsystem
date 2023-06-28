package com.example.blogsystem.service.impl;

import com.example.blogsystem.entity.Tag;
import com.example.blogsystem.mapper.TagMapper;
import com.example.blogsystem.service.TagService;
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
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {

}
