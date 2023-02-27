package com.zyx.pojo;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

public class Users implements Serializable {
    @Id
    private String id;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 昵称，媒体号
     */
    private String nickname;

    /**
     * 抖音号，唯一标识，需要限制修改次数，比如终生1次，每年1次，每半年1次等，可以用于付费修改。
     */
    private String douyin_num;

    /**
     * 头像
     */
    private String face;

    /**
     * 性别 1:男  0:女  2:保密
     */
    private Integer sex;

    /**
     * 生日
     */
    private Date birthday;

    /**
     * 国家
     */
    private String country;

    /**
     * 省份
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 区县
     */
    private String district;

    /**
     * 简介
     */
    private String description;

    /**
     * 个人介绍的背景图
     */
    private String bg_img;

    /**
     * 慕课号能否被修改，1：默认，可以修改；0，无法修改
     */
    private Integer can_imooc_num_be_updated;

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
     * 获取手机号
     *
     * @return mobile - 手机号
     */
    public String getMobile() {
        return mobile;
    }

    /**
     * 设置手机号
     *
     * @param mobile 手机号
     */
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    /**
     * 获取昵称，媒体号
     *
     * @return nickname - 昵称，媒体号
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * 设置昵称，媒体号
     *
     * @param nickname 昵称，媒体号
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * 获取抖音号，唯一标识，需要限制修改次数，比如终生1次，每年1次，每半年1次等，可以用于付费修改。
     *
     * @return douyin_num - 抖音号，唯一标识，需要限制修改次数，比如终生1次，每年1次，每半年1次等，可以用于付费修改。
     */
    public String getDouyin_num() {
        return douyin_num;
    }

    /**
     * 设置抖音号，唯一标识，需要限制修改次数，比如终生1次，每年1次，每半年1次等，可以用于付费修改。
     *
     * @param douyin_num 抖音号，唯一标识，需要限制修改次数，比如终生1次，每年1次，每半年1次等，可以用于付费修改。
     */
    public void setDouyin_num(String douyin_num) {
        this.douyin_num = douyin_num;
    }

    /**
     * 获取头像
     *
     * @return face - 头像
     */
    public String getFace() {
        return face;
    }

    /**
     * 设置头像
     *
     * @param face 头像
     */
    public void setFace(String face) {
        this.face = face;
    }

    /**
     * 获取性别 1:男  0:女  2:保密
     *
     * @return sex - 性别 1:男  0:女  2:保密
     */
    public Integer getSex() {
        return sex;
    }

    /**
     * 设置性别 1:男  0:女  2:保密
     *
     * @param sex 性别 1:男  0:女  2:保密
     */
    public void setSex(Integer sex) {
        this.sex = sex;
    }

    /**
     * 获取生日
     *
     * @return birthday - 生日
     */
    public Date getBirthday() {
        return birthday;
    }

    /**
     * 设置生日
     *
     * @param birthday 生日
     */
    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    /**
     * 获取国家
     *
     * @return country - 国家
     */
    public String getCountry() {
        return country;
    }

    /**
     * 设置国家
     *
     * @param country 国家
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * 获取省份
     *
     * @return province - 省份
     */
    public String getProvince() {
        return province;
    }

    /**
     * 设置省份
     *
     * @param province 省份
     */
    public void setProvince(String province) {
        this.province = province;
    }

    /**
     * 获取城市
     *
     * @return city - 城市
     */
    public String getCity() {
        return city;
    }

    /**
     * 设置城市
     *
     * @param city 城市
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * 获取区县
     *
     * @return district - 区县
     */
    public String getDistrict() {
        return district;
    }

    /**
     * 设置区县
     *
     * @param district 区县
     */
    public void setDistrict(String district) {
        this.district = district;
    }

    /**
     * 获取简介
     *
     * @return description - 简介
     */
    public String getDescription() {
        return description;
    }

    /**
     * 设置简介
     *
     * @param description 简介
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 获取个人介绍的背景图
     *
     * @return bg_img - 个人介绍的背景图
     */
    public String getBg_img() {
        return bg_img;
    }

    /**
     * 设置个人介绍的背景图
     *
     * @param bg_img 个人介绍的背景图
     */
    public void setBg_img(String bg_img) {
        this.bg_img = bg_img;
    }

    /**
     * 获取慕课号能否被修改，1：默认，可以修改；0，无法修改
     *
     * @return can_imooc_num_be_updated - 慕课号能否被修改，1：默认，可以修改；0，无法修改
     */
    public Integer getCan_imooc_num_be_updated() {
        return can_imooc_num_be_updated;
    }

    /**
     * 设置慕课号能否被修改，1：默认，可以修改；0，无法修改
     *
     * @param can_imooc_num_be_updated 慕课号能否被修改，1：默认，可以修改；0，无法修改
     */
    public void setCan_imooc_num_be_updated(Integer can_imooc_num_be_updated) {
        this.can_imooc_num_be_updated = can_imooc_num_be_updated;
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