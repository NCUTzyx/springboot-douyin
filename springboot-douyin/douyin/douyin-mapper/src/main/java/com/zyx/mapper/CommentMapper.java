package com.zyx.mapper;


import com.zyx.my.mapper.MyMapper;
import com.zyx.pojo.Comment;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentMapper extends MyMapper<Comment> {
}