package com.example.blogsystem.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.blogsystem.dto.CommentQuray;
import com.example.blogsystem.entity.Recomment;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author shj
 * @since 2023-06-28
 */
public interface RecommentService extends IService<Recomment> {

    void updateRecommentStatus(Integer id, Integer status);

    Page<Recomment> getAllRecommentFromMainComment(CommentQuray commentQuray);

    void postRecomment(Recomment recomment);

    Integer getRecomentReCount(Integer id);
}
