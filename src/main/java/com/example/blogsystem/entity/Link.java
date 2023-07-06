package com.example.blogsystem.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import lombok.Data;
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
@Data
@TableName("tb_link")
public class Link implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private int id;

    /**
     * 链接名称
     */
    private String linkName;

    /**
     * 具体链接
     */
    private String link;

    /**
     * 排序值
     */
    private int sort;

    /**
     * 创建时间
     */
    private String createTime;
}
