package com.example.blogsystem.mapper;

import com.example.blogsystem.entity.System;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.blogsystem.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
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
public interface SystemMapper extends BaseMapper<System> {

    //更新系统配置
    int updateSystem(System system);

    //根据id查询系统配置
    @Select("select * from tb_system where id=#{id}")
    List<System> findSystemById(@Param("id") Integer id );
}
