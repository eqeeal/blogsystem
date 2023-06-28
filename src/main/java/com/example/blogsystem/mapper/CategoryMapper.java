package com.example.blogsystem.mapper;

import com.example.blogsystem.entity.Category;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author shj
 * @since 2023-06-28
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {

}
