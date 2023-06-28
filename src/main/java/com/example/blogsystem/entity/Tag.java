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
@TableName("tb_tag")
public class Tag implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 分类名称
     */
    private String tagName;
}
