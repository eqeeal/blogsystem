package com.example.blogsystem.entity;

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
@TableName("tb_rel_tag_blog")
public class RelTagBlog implements Serializable {

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
     * 关联标签id
     */
    private Integer tagId;
}
