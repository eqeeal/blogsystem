package com.example.blogsystem.mapper;

import com.example.blogsystem.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;

import java.util.ArrayList;
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
public interface UserMapper extends BaseMapper<User> {
        @Select("select * from tb_user;")
        ArrayList<User> findAll();

        @Select("select * from tb_user where userPhone=#{userPhone} and userPass=#{userPass}")
        List<User> login(@Param("userPhone") String userphone, @Param("userPass") String userpass);

        @Insert("insert into tb_user(userName,userPass,userPhone) values(#{userName},#{userPass},#{userPhone})")
    void res(@Param("userName") String username,@Param("userPass") String userpass,@Param("userPhone") String userphone);

        @Select("select * from tb_user where userPhone=#{userPhone}")
    List<User> findUserByPhone(User user);
//    List<User> findUserByPhone(@Param("userPhone") String userphone);


        @Delete("delete from tb_user where id=#{id}")
    int deleteById(User user);//删除用户账号

       @Update("update tb_user set userName=#{userName},userAvatar=#{userAvatar},userEmail=#{userEmail} where id=#{id}")
    int updateUser(User user);
}
