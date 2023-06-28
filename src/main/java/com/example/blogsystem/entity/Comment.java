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
@TableName("tb_comment")
public class Comment implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
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
    private Byte status;

    /**
     * 评论时间
     */
    @TableField(fill = FieldFill.INSERT)
    private String createTime;

    /**
     * 评论头像
     */
    private String photo;
}
