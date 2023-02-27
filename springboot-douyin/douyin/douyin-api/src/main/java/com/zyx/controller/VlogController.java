package com.zyx.controller;

import com.zyx.bo.VlogBO;
import com.zyx.config.BaseInfoProperties;
import com.zyx.enums.YesOrNo;
import com.zyx.grace.result.GraceJSONResult;
import com.zyx.grace.result.ResponseStatusEnum;
import com.zyx.pojo.Users;
import com.zyx.pojo.Vlog;
import com.zyx.service.UserService;
import com.zyx.service.VlogService;
import com.zyx.utils.PagedGridResult;
import com.zyx.vo.VlogVO;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

/**
 * @author 张宇森
 * @version 1.0
 */

@Slf4j
@Api(tags = "VlogController 短视频相关模块")
@RequestMapping("/vlog")
@RestController
@RefreshScope  //刷新配置
public class VlogController extends BaseInfoProperties {

    @Autowired
    private VlogService vlogService;

    @Autowired
    private UserService userService;

    @Value("${nacos.counts}")
    private Integer nacosCounts;

    @PostMapping("/publish")
    public GraceJSONResult publish(@RequestBody VlogBO vlogBO){
        //TODO 检验vlogBo
        vlogService.addVlog(vlogBO);
        return GraceJSONResult.ok();
    }

    @GetMapping("/indexList")
    public GraceJSONResult indexList(@RequestParam(defaultValue = "") String userId,
                                     @RequestParam(defaultValue = "") String search,
                                     @RequestParam Integer page,
                                     @RequestParam Integer pageSize){
        if (page == null) {
            page = COMMON_START_PAGE;
        }

        if (pageSize == null) {
            pageSize = COMMON_PAGE_SIZE;
        }

        PagedGridResult vlogResult = vlogService.getVlogList(userId,search, page, pageSize);

        return GraceJSONResult.ok(vlogResult);
    }


    @GetMapping("/detail")
    public GraceJSONResult detail(@RequestParam(defaultValue = "") String userId,
                                  @RequestParam String vlogId) {

        VlogVO vlogVO = vlogService.getVlogById(userId,vlogId);
        return GraceJSONResult.ok(vlogVO);
    }

    @PostMapping("/changeToPrivate")
    public GraceJSONResult changeToPrivate(@RequestParam String userId,
                                           @RequestParam String vlogId){

        vlogService.changeToPriOrPub(userId,vlogId,YesOrNo.YES.type);
        return GraceJSONResult.ok();
    }

    @PostMapping("/changeToPublic")
    public GraceJSONResult changeToPublic(@RequestParam String userId,
                                          @RequestParam String vlogId){

        vlogService.changeToPriOrPub(userId,vlogId,YesOrNo.NO.type);
        return GraceJSONResult.ok();
    }

    @GetMapping("/myPublicList")
    public GraceJSONResult myPublicList(@RequestParam String userId,
                                        @RequestParam Integer page,
                                        @RequestParam Integer pageSize
                                        ){
        if (page == null) {
            page = COMMON_START_PAGE;
        }

        if (pageSize == null) {
            pageSize = COMMON_PAGE_SIZE;
        }

        PagedGridResult pagedGridResult = vlogService.queryMyVlogList(userId, page, pageSize, YesOrNo.NO.type);
        return GraceJSONResult.ok(pagedGridResult);
    }

    @GetMapping("/myPrivateList")
    public GraceJSONResult myPrivateList(@RequestParam String userId,
                                        @RequestParam Integer page,
                                        @RequestParam Integer pageSize
                                        ){
        if (page == null) {
            page = COMMON_START_PAGE;
        }

        if (pageSize == null) {
            pageSize = COMMON_PAGE_SIZE;
        }

        PagedGridResult pagedGridResult = vlogService.queryMyVlogList(userId, page, pageSize, YesOrNo.YES.type);
        return GraceJSONResult.ok(pagedGridResult);
    }


    @PostMapping("/like")
    public GraceJSONResult like(@RequestParam String userId,
                                @RequestParam String vlogerId,
                                @RequestParam String vlogId){

        // 判断id不为null
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(vlogerId) || StringUtils.isBlank(vlogId)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.SYSTEM_ERROR);
        }

        // 判断两个id对应的用户是否存在
        Users my = userService.queryUser(userId);
        Users vloger = userService.queryUser(vlogerId);
        Vlog vlog = vlogService.queryById(vlogId);
        if (my == null || vloger == null || vlog == null){
            return GraceJSONResult.errorCustom(ResponseStatusEnum.SYSTEM_RESPONSE_NO_INFO);
        }

        //点赞的视频，保存到数据库
        vlogService.userLikeVlog(userId,vlogId);

        //点赞后，视频和视频发布者的获赞 + 1
        redis.increment(REDIS_VLOG_BE_LIKED_COUNTS + ":" + vlogId,1);
        redis.increment(REDIS_VLOGER_BE_LIKED_COUNTS + ":" + vlogerId,1);

        //我点赞的视频 在redis中保存关联关系
        redis.set(REDIS_USER_LIKE_VLOG + ":" + userId + ":" + vlogId, "1");

        // 到达阈值入库
        // 点赞完毕，获得当前在redis 中的总数
        // 假定阈值（配置）为2000
        // 此时点赞数达到2000，则触发入库  => 弱一致性
        String countsStr = redis.get(REDIS_VLOG_BE_LIKED_COUNTS + ":" + vlogId);
        log.info("=====[ " + REDIS_VLOG_BE_LIKED_COUNTS + ":" + vlogId + " ]======");
        Integer counts = 0;

        if (StringUtils.isNotBlank(countsStr)) {
            counts = Integer.parseInt(countsStr);

            if (counts >= nacosCounts){
                vlogService.flushCounts(vlogId,counts);
            }
        }
        return GraceJSONResult.ok();
    }

    @PostMapping("/unlike")
    public GraceJSONResult unlike(@RequestParam String userId,
                                @RequestParam String vlogerId,
                                @RequestParam String vlogId){

        // 判断id不为null
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(vlogerId) || StringUtils.isBlank(vlogId)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.SYSTEM_ERROR);
        }

        // 判断两个id对应的用户是否存在
        Users my = userService.queryUser(userId);
        Users vloger = userService.queryUser(vlogerId);
        Vlog vlog = vlogService.queryById(vlogId);
        if (my == null || vloger == null || vlog == null){
            return GraceJSONResult.errorCustom(ResponseStatusEnum.SYSTEM_RESPONSE_NO_INFO);
        }

        //取消点赞视频
        vlogService.userUnLikeVlog(userId,vlogId);

        //点赞后，视频和视频发布者的获赞 - 1
        redis.decrement(REDIS_VLOG_BE_LIKED_COUNTS + ":" + vlogId,1);
        redis.decrement(REDIS_VLOGER_BE_LIKED_COUNTS + ":" + vlogerId,1);

        //在redis中删除关联关系
        redis.del(REDIS_USER_LIKE_VLOG + ":" + userId + ":" + vlogId);

        return GraceJSONResult.ok();
    }

    @PostMapping("/totalLikedCounts")
    public GraceJSONResult totalLikedCounts(@RequestParam String vlogId){

        Integer num = vlogService.getVlogBeLikeCounts(vlogId);
        return GraceJSONResult.ok(num);
    }

    @GetMapping("/myLikedList")
    public GraceJSONResult myLikedList(@RequestParam String userId,
                                         @RequestParam Integer page,
                                         @RequestParam Integer pageSize){

        if (page == null) {
            page = COMMON_START_PAGE;
        }

        if (pageSize == null) {
            pageSize = COMMON_PAGE_SIZE;
        }

        PagedGridResult pagedGridResult = vlogService.getMyLikedVlogList(userId,page,pageSize);
        return GraceJSONResult.ok(pagedGridResult);
    }


    @GetMapping("/followList")
    public GraceJSONResult followList(@RequestParam String myId,
                                       @RequestParam Integer page,
                                       @RequestParam Integer pageSize){

        if (page == null) {
            page = COMMON_START_PAGE;
        }

        if (pageSize == null) {
            pageSize = COMMON_PAGE_SIZE;
        }

        PagedGridResult pagedGridResult = vlogService.getMyFollowedVlogList(myId,page,pageSize);
        return GraceJSONResult.ok(pagedGridResult);
    }

    @GetMapping("/friendList")
    public GraceJSONResult friendList(@RequestParam String myId,
                                      @RequestParam Integer page,
                                      @RequestParam Integer pageSize){

        if (page == null) {
            page = COMMON_START_PAGE;
        }

        if (pageSize == null) {
            pageSize = COMMON_PAGE_SIZE;
        }

        PagedGridResult pagedGridResult = vlogService.getMyFriendVlogList(myId,page,pageSize);
        return GraceJSONResult.ok(pagedGridResult);
    }


}
