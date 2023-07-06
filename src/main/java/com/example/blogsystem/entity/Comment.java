package com.example.blogsystem.entity;

import com.baomidou.mybatisplus.annotation.*;

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
@TableName("tb_comment")
public class Comment implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 关联博客id
     */
    private String blogId;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 评论人id
     */
    private Integer userId;

    /**
     * 	审批状态：0-待审核、1-表示审核通过、2-审核未通过
     */
    private Integer status;

    /**
     * 评论时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 评论头像
     */
    private String photo;
}
