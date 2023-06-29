package com.example.blogsystem.service;

import com.example.blogsystem.entity.Tag;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author shj
 * @since 2023-06-28
 */
public interface TagService extends IService<Tag> {

    List<Tag> findAll();
}
