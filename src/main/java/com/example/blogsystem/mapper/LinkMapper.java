package com.example.blogsystem.mapper;

import com.example.blogsystem.entity.Link;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;

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
public interface LinkMapper extends BaseMapper<Link> {
    @Select("select * from tb_link")
    List<Link> findAll();
    //添加链接
    @Insert("insert into tb_link (link_name,link,create_time,sort) values(#{linkName},#{link},#{createTime},#{sort})")
    Integer addLink(Link link);
    //根据链接名查找链接
    @Select("select * from tb_link where link_name = #{linkName}")
    List<Link> findLinkByName(@Param("linkName") String linkName);
    //删除链接
    @Delete("delete from tb_link where id = #{id}")
    Integer delLink(Integer id);
    //修改链接，使用动态的sql,注释@update注解
    //@Update("update tb_link set link_name=#{linkName},link=#{link},create_time=#{create_time} where id = #{id}")
    Integer updateLink(Link link);
    //获取总条数
    @Select("select count(*) from tb_link where link_name like concat('%',#{linkName},'%')")
    Integer linkTotalCount(@Param("linkName") String linkName);
    //分页查询
    @Select("select  * from tb_link where link_name like concat('%',#{linkName},'%') limit #{pageNum},#{pageSize} ")
    List<Link> paginLink(@Param("linkName") String linkName,@Param("pageNum") Integer pageNum,@Param("pageSize") Integer pageSize);
    //批量删除
    int delLinks(List<Integer> ids);
}
