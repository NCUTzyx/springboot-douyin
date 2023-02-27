package com.zyx.service;

import com.zyx.utils.PagedGridResult;

/**
 * @author 张宇森
 * @version 1.0
 */
public interface FansService {

    //关注短视频博主
    void doFollow(String myId, String volgerId);

    //取关短视频博主
    void doCancel(String myId, String volgerId);

    //查询用户是否关注博主
    boolean queryIsFollowVloger(String myId, String volgerId);

    //查询我的关注列表
    PagedGridResult queryMyFollows(String myId, Integer page, Integer pageSize);

    //查询我的粉丝列表
    PagedGridResult queryMyFans(String myId, Integer page, Integer pageSize);


}
