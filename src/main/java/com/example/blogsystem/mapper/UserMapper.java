package com.example.blogsystem.mapper;

import com.example.blogsystem.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.springframework.web.bind.annotation.RequestMapping;

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
        //查询所有用户
        @Select("select * from tb_user;")
        ArrayList<User> findAll();

        //登录
        @Select("select * from tb_user where user_phone=#{userPhone} and user_pass=#{userPass}")
        List<User> login(@Param("userPhone") String userphone, @Param("userPass") String userpass);

        //通过手机号查询用户
        @Select("select * from tb_user where user_phone=#{userPhone}")
//        List<User> findUserByPhone(User user);
        List<User> findUserByPhone(@Param("userPhone") String userphone);
        @Select("select COUNT(*) from tb_user")
        Integer countUsers();


        @Insert("insert into tb_user(user_name,user_pass,user_phone) values(#{userName},#{userPass},#{userPhone})")
        void res(@Param("userName") String username,@Param("userPass") String userpass,@Param("userPhone") String userphone);

//        @Select("select * from tb_user where user_phone=#{userPhone}")
//        List<User> findUserByPhone(User user);
//    List<User> findUserByPhone(@Param("userPhone") String userphone);


        //通过id查询用户
        @Select("select * from tb_user where id=#{id}")
        List<User> findUserById(@Param("id")Integer id);


        @Delete("delete from tb_user where id=#{id}")
        int deleteById(User user);//删除用户账号

//   更新   @Update("update tb_user set userName=#{userName},userAvatar=#{userAvatar},userEmail=#{userEmail} where id=#{id}")
        //int updateUser(User user);

    //获取总数
        @Select("select count(*) from tb_user where  user_name like concat('%',#{username},'%')")
        Integer userTotalCount(@Param("username") String username);

    //分页查询
        @Select("select * from tb_user where user_name like concat('%',#{username},'%')  limit #{pageNum},#{pageSize}")
        List<User> paginUsers(@Param("username")String username,@Param("pageNum")Integer pageNum,@Param("pageSize")Integer pageSize);

        //批量删除
        int delUsers(List<Integer> ids);
       @Update("update tb_user set userName=#{userName},userAvatar=#{userAvatar},userEmail=#{userEmail} where id=#{id}")
    int updateUser(User user);


       @Update("update tb_user set user_pass=#{userPass} where user_phone=#{userPhone}")
    Integer updatePass(User user);

}
