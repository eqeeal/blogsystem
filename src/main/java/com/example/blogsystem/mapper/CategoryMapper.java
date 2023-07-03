package com.example.blogsystem.mapper;

import com.example.blogsystem.common.Result;
import com.example.blogsystem.entity.Category;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.springframework.web.bind.annotation.PostMapping;

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
    @Select("select * from tb_category")
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

    //分页查询
    @Select("select  * from tb_category limit #{pageNum},#{pageSize}")
    List<Category> paginUsers(@Param("pageNum") Integer pageNum,@Param("pageSize") Integer pageSize);

    int delBatch(List<Integer> ids);
}
