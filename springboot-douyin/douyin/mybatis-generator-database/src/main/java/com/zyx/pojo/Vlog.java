package com.zyx.pojo;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

public class Vlog implements Serializable {
    @Id
    private String id;

    /**
     * 对应用户表id，vlog视频发布者
     */
    private String vloger_id;

    /**
     * 视频播放地址
     */
    private String url;

    /**
     * 视频封面
     */
    private String cover;

    /**
     * 视频标题，可以为空
     */
    private String title;

    /**
     * 视频width
     */
    private Integer width;

    /**
     * 视频height
     */
    private Integer height;

    /**
     * 点赞总数
     */
    private Integer like_counts;

    /**
     * 评论总数
     */
    private Integer comments_counts;

    /**
     * 是否私密，用户可以设置私密，如此可以不公开给比人看
     */
    private Integer is_private;

    /**
     * 创建时间 创建时间
     */
    private Date created_time;

    /**
     * 更新时间 更新时间
     */
    private Date updated_time;

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
     * 获取对应用户表id，vlog视频发布者
     *
     * @return vloger_id - 对应用户表id，vlog视频发布者
     */
    public String getVloger_id() {
        return vloger_id;
    }

    /**
     * 设置对应用户表id，vlog视频发布者
     *
     * @param vloger_id 对应用户表id，vlog视频发布者
     */
    public void setVloger_id(String vloger_id) {
        this.vloger_id = vloger_id;
    }

    /**
     * 获取视频播放地址
     *
     * @return url - 视频播放地址
     */
    public String getUrl() {
        return url;
    }

    /**
     * 设置视频播放地址
     *
     * @param url 视频播放地址
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * 获取视频封面
     *
     * @return cover - 视频封面
     */
    public String getCover() {
        return cover;
    }

    /**
     * 设置视频封面
     *
     * @param cover 视频封面
     */
    public void setCover(String cover) {
        this.cover = cover;
    }

    /**
     * 获取视频标题，可以为空
     *
     * @return title - 视频标题，可以为空
     */
    public String getTitle() {
        return title;
    }

    /**
     * 设置视频标题，可以为空
     *
     * @param title 视频标题，可以为空
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 获取视频width
     *
     * @return width - 视频width
     */
    public Integer getWidth() {
        return width;
    }

    /**
     * 设置视频width
     *
     * @param width 视频width
     */
    public void setWidth(Integer width) {
        this.width = width;
    }

    /**
     * 获取视频height
     *
     * @return height - 视频height
     */
    public Integer getHeight() {
        return height;
    }

    /**
     * 设置视频height
     *
     * @param height 视频height
     */
    public void setHeight(Integer height) {
        this.height = height;
    }

    /**
     * 获取点赞总数
     *
     * @return like_counts - 点赞总数
     */
    public Integer getLike_counts() {
        return like_counts;
    }

    /**
     * 设置点赞总数
     *
     * @param like_counts 点赞总数
     */
    public void setLike_counts(Integer like_counts) {
        this.like_counts = like_counts;
    }

    /**
     * 获取评论总数
     *
     * @return comments_counts - 评论总数
     */
    public Integer getComments_counts() {
        return comments_counts;
    }

    /**
     * 设置评论总数
     *
     * @param comments_counts 评论总数
     */
    public void setComments_counts(Integer comments_counts) {
        this.comments_counts = comments_counts;
    }

    /**
     * 获取是否私密，用户可以设置私密，如此可以不公开给比人看
     *
     * @return is_private - 是否私密，用户可以设置私密，如此可以不公开给比人看
     */
    public Integer getIs_private() {
        return is_private;
    }

    /**
     * 设置是否私密，用户可以设置私密，如此可以不公开给比人看
     *
     * @param is_private 是否私密，用户可以设置私密，如此可以不公开给比人看
     */
    public void setIs_private(Integer is_private) {
        this.is_private = is_private;
    }

    /**
     * 获取创建时间 创建时间
     *
     * @return created_time - 创建时间 创建时间
     */
    public Date getCreated_time() {
        return created_time;
    }

    /**
     * 设置创建时间 创建时间
     *
     * @param created_time 创建时间 创建时间
     */
    public void setCreated_time(Date created_time) {
        this.created_time = created_time;
    }

    /**
     * 获取更新时间 更新时间
     *
     * @return updated_time - 更新时间 更新时间
     */
    public Date getUpdated_time() {
        return updated_time;
    }

    /**
     * 设置更新时间 更新时间
     *
     * @param updated_time 更新时间 更新时间
     */
    public void setUpdated_time(Date updated_time) {
        this.updated_time = updated_time;
    }
}