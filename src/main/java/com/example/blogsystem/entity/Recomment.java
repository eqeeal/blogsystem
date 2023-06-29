package com.example.blogsystem.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
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
    private Integer id;

    /**
     * 主评论id
     */
    private String commentId;

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
    private String createTime;

    /**
     * 父回复id，没有则为0
     */
    private Integer pid;
}
