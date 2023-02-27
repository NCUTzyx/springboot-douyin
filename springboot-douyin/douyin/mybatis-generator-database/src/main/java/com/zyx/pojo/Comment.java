package com.zyx.pojo;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

public class Comment implements Serializable {
    @Id
    private String id;

    /**
     * 评论的视频是哪个作者（vloger）的关联id
     */
    private String vloger_id;

    /**
     * 如果是回复留言，则本条为子留言，需要关联查询
     */
    private String father_comment_id;

    /**
     * 回复的那个视频id
     */
    private String vlog_id;

    /**
     * 发布留言的用户id
     */
    private String comment_user_id;

    /**
     * 留言内容
     */
    private String content;

    /**
     * 留言的点赞总数
     */
    private Integer like_counts;

    /**
     * 留言时间
     */
    private Date create_time;

    private static final long serialVersionUID = 1L;

    /**
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 获取评论的视频是哪个作者（vloger）的关联id
     *
     * @return vloger_id - 评论的视频是哪个作者（vloger）的关联id
     */
    public String getVloger_id() {
        return vloger_id;
    }

    /**
     * 设置评论的视频是哪个作者（vloger）的关联id
     *
     * @param vloger_id 评论的视频是哪个作者（vloger）的关联id
     */
    public void setVloger_id(String vloger_id) {
        this.vloger_id = vloger_id;
    }

    /**
     * 获取如果是回复留言，则本条为子留言，需要关联查询
     *
     * @return father_comment_id - 如果是回复留言，则本条为子留言，需要关联查询
     */
    public String getFather_comment_id() {
        return father_comment_id;
    }

    /**
     * 设置如果是回复留言，则本条为子留言，需要关联查询
     *
     * @param father_comment_id 如果是回复留言，则本条为子留言，需要关联查询
     */
    public void setFather_comment_id(String father_comment_id) {
        this.father_comment_id = father_comment_id;
    }

    /**
     * 获取回复的那个视频id
     *
     * @return vlog_id - 回复的那个视频id
     */
    public String getVlog_id() {
        return vlog_id;
    }

    /**
     * 设置回复的那个视频id
     *
     * @param vlog_id 回复的那个视频id
     */
    public void setVlog_id(String vlog_id) {
        this.vlog_id = vlog_id;
    }

    /**
     * 获取发布留言的用户id
     *
     * @return comment_user_id - 发布留言的用户id
     */
    public String getComment_user_id() {
        return comment_user_id;
    }

    /**
     * 设置发布留言的用户id
     *
     * @param comment_user_id 发布留言的用户id
     */
    public void setComment_user_id(String comment_user_id) {
        this.comment_user_id = comment_user_id;
    }

    /**
     * 获取留言内容
     *
     * @return content - 留言内容
     */
    public String getContent() {
        return content;
    }

    /**
     * 设置留言内容
     *
     * @param content 留言内容
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * 获取留言的点赞总数
     *
     * @return like_counts - 留言的点赞总数
     */
    public Integer getLike_counts() {
        return like_counts;
    }

    /**
     * 设置留言的点赞总数
     *
     * @param like_counts 留言的点赞总数
     */
    public void setLike_counts(Integer like_counts) {
        this.like_counts = like_counts;
    }

    /**
     * 获取留言时间
     *
     * @return create_time - 留言时间
     */
    public Date getCreate_time() {
        return create_time;
    }

    /**
     * 设置留言时间
     *
     * @param create_time 留言时间
     */
    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }
}