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
@TableName("tb_recomment")
public class Recomment implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 主评论id
     */

    private Integer commentId;

    /**
     * 回复内容
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
     * 父回复id，没有则为0
     */
    private Integer pid;
}
