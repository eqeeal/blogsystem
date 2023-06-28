package com.example.blogsystem.entity;

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
@TableName("tb_system")
public class System implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 后台管理标题
     */
    private String sitename;

    /**
     * 后台管理系统的logo地址
     */
    private String logourl;

    /**
     * 主题颜色
     */
    private String themeColor;

    /**
     * 网页图标ico
     */
    private LocalDateTime icourl;
}
