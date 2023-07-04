package com.example.blogsystem.mapper;

import com.example.blogsystem.entity.Blog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author shj
 * @since 2023-06-28
 */
@Mapper
public interface BlogMapper extends BaseMapper<Blog> {
    @Select("select count(*) from tb_blog where user_id =#{id} ")
    Integer countByUserId(@RequestParam("id")Integer id);


}
