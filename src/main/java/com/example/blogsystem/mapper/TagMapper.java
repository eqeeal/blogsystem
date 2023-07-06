package com.example.blogsystem.mapper;

import com.example.blogsystem.entity.Tag;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

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
public interface TagMapper extends BaseMapper<Tag> {

    //查询标签名为tag_name的标签，用于判断是否能添加tag_name标签
    @Select("select * from tb_tag where tag_name=#{tagName}")
    List<Tag> findTagByName(Tag tag);

    @Insert("insert into tb_tag(tag_name) values(tag_name=#{tagName}) ")
    boolean insertTag(Tag tag);


}
