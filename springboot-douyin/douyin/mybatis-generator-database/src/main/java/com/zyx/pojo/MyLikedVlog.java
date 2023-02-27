package com.zyx.pojo;

import java.io.Serializable;
import javax.persistence.*;

@Table(name = "my_liked_vlog")
public class MyLikedVlog implements Serializable {
    @Id
    private String id;

    /**
     * 用户id
     */
    private String user_id;

    /**
     * 喜欢的短视频id
     */
    private String vlog_id;

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
     * 获取用户id
     *
     * @return user_id - 用户id
     */
    public String getUser_id() {
        return user_id;
    }

    /**
     * 设置用户id
     *
     * @param user_id 用户id
     */
    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    /**
     * 获取喜欢的短视频id
     *
     * @return vlog_id - 喜欢的短视频id
     */
    public String getVlog_id() {
        return vlog_id;
    }

    /**
     * 设置喜欢的短视频id
     *
     * @param vlog_id 喜欢的短视频id
     */
    public void setVlog_id(String vlog_id) {
        this.vlog_id = vlog_id;
    }
}