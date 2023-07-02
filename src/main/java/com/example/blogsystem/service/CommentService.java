package com.example.blogsystem.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.blogsystem.dto.CommentQuray;
import com.example.blogsystem.dto.PageCommentDto;
import com.example.blogsystem.entity.Comment;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author shj
 * @since 2023-06-28
 */
public interface CommentService extends IService<Comment> {

    void postMianComment(Comment comment);

    boolean updateMianCommentStatus(Integer id,Integer status);

    Page<PageCommentDto> getPage(CommentQuray commentQuray);
}
