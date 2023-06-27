package com.example.blogsystem.mapper;

import com.example.blogsystem.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Mapper
public interface UserMapper {

    @Select("select * from tb_user;")
    List<User> findAll();

    @Select("select * from tb_user where userPhone=#{userPhone} and userPass=#{userPass}")
    List<User> login(@Param("userPhone") String userphone,@Param("userPass") String userpass);

    @Insert("insert into tb_user(userName,userPass,userPhone) values(#{userName},#{userPass},#{userPhone})")
    void res(@Param("userName") String username,@Param("userPass") String userpass,@Param("userPhone") String userphone);

    @Select("select * from tb_user where userPhone=#{userPhone}")
    List<User> findUserByPhone(User user);
//    List<User> findUserByPhone(@Param("userPhone") String userphone);
}