package com.zyx.pojo;

import java.io.Serializable;
import javax.persistence.*;

public class Fans implements Serializable {
    @Id
    private String id;

    /**
     * 作家用户id
     */
    private String vloger_id;

    /**
     * 粉丝用户id
     */
    private String fan_id;

    /**
     * 粉丝是否是vloger的朋友，如果成为朋友，则本表的双方此字段都需要设置为1，如果有一人取关，则两边都需要设置为0
     */
    private Integer is_fan_friend_of_mine;

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
     * 获取作家用户id
     *
     * @return vloger_id - 作家用户id
     */
    public String getVloger_id() {
        return vloger_id;
    }

    /**
     * 设置作家用户id
     *
     * @param vloger_id 作家用户id
     */
    public void setVloger_id(String vloger_id) {
        this.vloger_id = vloger_id;
    }

    /**
     * 获取粉丝用户id
     *
     * @return fan_id - 粉丝用户id
     */
    public String getFan_id() {
        return fan_id;
    }

    /**
     * 设置粉丝用户id
     *
     * @param fan_id 粉丝用户id
     */
    public void setFan_id(String fan_id) {
        this.fan_id = fan_id;
    }

    /**
     * 获取粉丝是否是vloger的朋友，如果成为朋友，则本表的双方此字段都需要设置为1，如果有一人取关，则两边都需要设置为0
     *
     * @return is_fan_friend_of_mine - 粉丝是否是vloger的朋友，如果成为朋友，则本表的双方此字段都需要设置为1，如果有一人取关，则两边都需要设置为0
     */
    public Integer getIs_fan_friend_of_mine() {
        return is_fan_friend_of_mine;
    }

    /**
     * 设置粉丝是否是vloger的朋友，如果成为朋友，则本表的双方此字段都需要设置为1，如果有一人取关，则两边都需要设置为0
     *
     * @param is_fan_friend_of_mine 粉丝是否是vloger的朋友，如果成为朋友，则本表的双方此字段都需要设置为1，如果有一人取关，则两边都需要设置为0
     */
    public void setIs_fan_friend_of_mine(Integer is_fan_friend_of_mine) {
        this.is_fan_friend_of_mine = is_fan_friend_of_mine;
    }
}