package com.example.blogsystem.service.impl;

import com.example.blogsystem.entity.Tag;
import com.example.blogsystem.mapper.TagMapper;
import com.example.blogsystem.service.TagService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

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
    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public List<Tag> findAll() {
        List<Tag> tags = redisTemplate.opsForList().range("tags", 0, -1);
        if(tags!=null && tags.size()!=0){
            return tags;
        }
        else {
            List<Tag> list =this.list();//获取tag表全部数据
            redisTemplate.opsForList().rightPush("tags",list);
            return list;
        }

    }

    @Override
    public Integer countTags(String input) {
        int sizeTags=query().like(input!=null,"tag_name",input).list().size();
        return sizeTags;
    }

    @Override
    public List<Tag> findTags(String input) {
        return query().like(input!=null,"tag_name",input).list();
    }
}
