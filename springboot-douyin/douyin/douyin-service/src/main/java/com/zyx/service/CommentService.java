package com.zyx.service;

import com.zyx.bo.CommentBO;
import com.zyx.pojo.Comment;
import com.zyx.utils.PagedGridResult;
import com.zyx.vo.CommentVO;

/**
 * @author 张宇森
 * @version 1.0
 */
public interface CommentService {

    //发表评论
    CommentVO addComment(CommentBO commentBO);

    //查询评论的列表
    PagedGridResult queryVlogComments(String vlogId, String userId,Integer page,Integer pageSize);

    //删除视频下的评论
    void deleteComment(String commentUserId,String commentId,String vlogId);

    //根据评论id查询Comment
    Comment getCommentById(String id);
}
