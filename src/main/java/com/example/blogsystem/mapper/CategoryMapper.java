package com.example.blogsystem.mapper;

import com.example.blogsystem.common.Result;
import com.example.blogsystem.entity.Category;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.HashMap;
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
public interface CategoryMapper extends BaseMapper<Category> {

    //获取所有分类信息
    @Select("select * from tb_category limit 0,5")
    List<Category> getAllCate();

    //新增分类
    @Insert("insert into tb_category (category_name,category_avatar,category_detail) value (#{categoryName},#{categoryAvatar},#{categoryDetail})")
    Integer addCate(@Param("categoryName") String categoryName,@Param("categoryAvatar") String categoryAvatar,@Param("categoryDetail") String categoryDetail);

    //判断是否存在分类名
    @Select("select category_name from tb_category where category_name=#{categoryName}")
    List<Category> getByName(@Param("categoryName") String categoryName);

    //修改分类
    @Update("update tb_category set category_name=#{categoryName},category_detail=#{categoryDetail},category_avatar=#{categoryAvatar} where id=#{id}")
    Integer updateCate(Category category);

    //删除分类
    @Delete("delete from tb_category where id=#{id}")
    Integer delCate(@Param("id") Integer id);

    //分页查询(带模糊搜索分类名)
    @Select("select  * from tb_category where category_name like concat('%',#{categoryName},'%') limit #{pageNum},#{pageSize}")
    List<Category> paginUsers(@Param("categoryName") String categoryName,@Param("pageNum") Integer pageNum,@Param("pageSize") Integer pageSize);

    //批量删除分类
    int delBatch(List<Integer> ids);

    //获取数据总条数
    @Select("select count(*) from tb_category where category_name like concat('%',#{categoryName},'%')")
    Integer getCateCount(@Param("categoryName") String categoryName);

    //获取指定id的分类下绑定的博客数量
    @Select("select count(*) from tb_blog where tb_blog.category_id=#{id}")
    Integer getCateBlog(@Param("id") Integer id);

    //获取前十热门分类
    @Select("select category_name,category_id,count(*) as count from tb_blog,tb_category where tb_blog.category_id=tb_category.id " +
            "GROUP BY tb_blog.category_id ORDER BY count(*) desc LIMIT 10 ")
    List<HashMap> getHotCate();

    //获取前十热门分类
    @Select("select category_name as name,count(*) as count from tb_blog,tb_category where tb_blog.category_id=tb_category.id " +
            "GROUP BY tb_blog.category_id ORDER BY count(*) desc")
    List<HashMap> getHotCategory();
    @Select("select category_name as name,count(*) as count from tb_blog,tb_category where tb_blog.user_id = #{id} and tb_blog.category_id=tb_category.id " +
            "GROUP BY tb_blog.category_id ORDER BY count(*) desc")
    List<HashMap> getHotCategoryByUserId(Integer id);
}
