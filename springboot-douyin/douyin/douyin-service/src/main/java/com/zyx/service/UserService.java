package com.zyx.service;

import com.zyx.bo.UserInfoBO;
import com.zyx.pojo.Users;

/**
 * @author 张宇森
 * @version 1.0
 */
public interface UserService {

    //根据手机号判断用户是否存在，并返回信息
    Users queryMobileIsExist(String mobile);

    //创建用户信息，并且返回用户对象
    Users createUser(String mobile);

    //根据用户id查询用户信息
    Users queryUser(String userId);

    //修改用户信息
    Users updateUserInfo(UserInfoBO userInfoBO);

    Users updateUserInfo(UserInfoBO userInfoBO,Integer type);


}
