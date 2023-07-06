package com.example.blogsystem.mapper;

import com.example.blogsystem.dto.usernameCountDto;
import com.example.blogsystem.entity.RelTagBlog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author shj
 * @since 2023-06-28
 */
@Mapper
public interface RelTagBlogMapper extends BaseMapper<RelTagBlog> {
    @Select("select count(*) from tb_rel_tag_blog group by tag_id")
    List<Integer> getTagCountByBlog();

    List<String> gettagname();

    @Select("SELECT count(*) as c,b.user_name  from tb_blog as a,tb_user as b where a.user_id=b.id   group by user_id order by count(*) desc limit 5")
    List<Map<Integer,String>> getblogCount();

    @Select("select a.category_name,count(*) as c from tb_category as a,tb_blog as b where a.id=b.category_id group by b.category_id")
    List<Map<Integer,String>> getCategoryCount();

    @Select("select tb_tag.tag_name as name,ct from tb_tag," +
            "(select tag_id,count(*) as ct from tb_rel_tag_blog group by tag_id) as t1  " +
            "where tb_tag.id=t1.tag_id ")
    List<Map<String,Integer>> tagAndCount();
}
