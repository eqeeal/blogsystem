package com.example.blogsystem.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 *
 * </p>
 *
 * @author shj
 * @since 2023-06-28
 */
@Getter
@Setter
@TableName("tb_user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 用户登录密码
     */
    private String userPass;

    /**
     * 手机号
     */
    private String userPhone;

    /**
     * 用户头像地址
     */
    private String userAvatar;

    /**
     * 用户邮箱
     */
    private String userEmail;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updataTime;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
}
