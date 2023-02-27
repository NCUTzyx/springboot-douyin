package com.zyx.service;

import com.zyx.bo.VlogBO;
import com.zyx.pojo.Vlog;
import com.zyx.utils.PagedGridResult;
import com.zyx.vo.VlogVO;


/**
 * @author 张宇森
 * @version 1.0
 */
public interface VlogService {

    //添加视频信息
    void addVlog(VlogBO vlogBO);

    //根据视频id查讯视频
    Vlog queryById(String vlogId);

    //查询首页/搜索的vlog列表
    PagedGridResult getVlogList(String userId,String search, Integer page, Integer pageSize);

    //根据视频id，查询vlog
    VlogVO getVlogById(String userId,String vlogId);

    //用户把视频改为公开/私密的视频
    void changeToPriOrPub(String userId,String vlogId,Integer yesOrNo);

    //查询用户公开/私密视频列表
    PagedGridResult queryMyVlogList(String userId, Integer page, Integer pageSize,Integer yesOrNo);

    //用户点赞视频
    void userLikeVlog(String userId,String vlogId);

    //根据视频id，查询vlog
    Vlog getVlogById(String id);

    //用户取消点赞视频
    void userUnLikeVlog(String userId,String vlogId);

    //获得视频的总点赞数
    Integer getVlogBeLikeCounts(String vlogId);

    //查询用户点赞过的视频
    PagedGridResult getMyLikedVlogList(String userId,Integer page, Integer pageSize);

    //查询用户关注的博主发布的短视频
    PagedGridResult getMyFollowedVlogList(String myId,Integer page, Integer pageSize);

    //查询用户朋友发布的短视频
    PagedGridResult getMyFriendVlogList(String myId,Integer page, Integer pageSize);

    //把 redis 中的 counts 刷入到数据库
    void flushCounts(String vlogId, Integer counts);

}
