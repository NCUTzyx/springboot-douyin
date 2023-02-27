package com.zyx.controller;

import com.zyx.config.BaseInfoProperties;
import com.zyx.grace.result.GraceJSONResult;
import com.zyx.grace.result.ResponseStatusEnum;
import com.zyx.pojo.Users;
import com.zyx.service.FansService;
import com.zyx.service.UserService;
import com.zyx.utils.PagedGridResult;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author 张宇森
 * @version 1.0
 */
@Slf4j
@Api(tags = "FansController 粉丝相关模块")
@RestController
@RequestMapping("/fans")
public class FansController extends BaseInfoProperties {

    @Autowired
    private UserService userService;

    @Autowired
    private FansService fansService;

    @PostMapping("/follow")
    public GraceJSONResult follow(@RequestParam String myId,
                                  @RequestParam String vlogerId){

        // 判断id不为null
        if (StringUtils.isBlank(myId) || StringUtils.isBlank(vlogerId)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.SYSTEM_ERROR);
        }

        // 判断当前用户自己不能关注自己
        if (myId.equalsIgnoreCase(vlogerId)){
            return GraceJSONResult.errorCustom(ResponseStatusEnum.SYSTEM_RESPONSE_NO_INFO);
        }

        // 判断两个id对应的用户是否存在
        Users my = userService.queryUser(myId);
        Users vloger = userService.queryUser(vlogerId);
        if (my == null || vloger == null){
            return GraceJSONResult.errorCustom(ResponseStatusEnum.SYSTEM_RESPONSE_NO_INFO);
        }

        //保存粉丝关系到数据库
        fansService.doFollow(myId,vlogerId);

        // 博主粉丝+1，我的关注+1
        redis.increment(REDIS_MY_FOLLOWS_COUNTS + ":" + myId,1);
        redis.increment(REDIS_MY_FANS_COUNTS + ":" + vlogerId,1);

        // 我和博主地关联，依赖redis,不要存储到数据库，避免db的性能瓶颈
        redis.set(REDIS_FANS_AND_VLOGGER_RELATIONSHIP + ":" + myId + ":" + vlogerId,"1");

        return GraceJSONResult.ok();
    }

    @PostMapping("/cancel")
    public GraceJSONResult cancel(@RequestParam String myId,
                                  @RequestParam String vlogerId){

        // 判断id不为null
        if (StringUtils.isBlank(myId) || StringUtils.isBlank(vlogerId)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.SYSTEM_ERROR);
        }

        // 判断两个id对应的用户是否存在
        Users my = userService.queryUser(myId);
        Users vloger = userService.queryUser(vlogerId);
        if (my == null || vloger == null){
            return GraceJSONResult.errorCustom(ResponseStatusEnum.SYSTEM_RESPONSE_NO_INFO);
        }

        //删除
        fansService.doCancel(myId, vlogerId);

        // 博主粉丝-11，我的关注-1
        redis.decrement(REDIS_MY_FOLLOWS_COUNTS + ":" + myId,1);
        redis.decrement(REDIS_MY_FANS_COUNTS + ":" + vlogerId,1);

        // 我和博主地关联，依赖redis,不要存储到数据库，避免db的性能瓶颈
        redis.del(REDIS_FANS_AND_VLOGGER_RELATIONSHIP + ":" + myId + ":" + vlogerId);


        return GraceJSONResult.ok();
    }

    @GetMapping("/queryDoIFollowVloger")
    public GraceJSONResult queryDoIFollowVloger(@RequestParam String myId,
                                  @RequestParam String vlogerId){

        // 判断id不为null
        if (StringUtils.isBlank(myId) || StringUtils.isBlank(vlogerId)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.SYSTEM_ERROR);
        }

        // 判断两个id对应的用户是否存在
        Users my = userService.queryUser(myId);
        Users vloger = userService.queryUser(vlogerId);
        if (my == null || vloger == null){
            return GraceJSONResult.errorCustom(ResponseStatusEnum.SYSTEM_RESPONSE_NO_INFO);
        }

        boolean flag = fansService.queryIsFollowVloger(myId, vlogerId);

        return GraceJSONResult.ok(flag);
    }

    @GetMapping("/queryMyFollows")
    public GraceJSONResult queryMyFollows(@RequestParam String myId,
                                          @RequestParam Integer page,
                                          @RequestParam Integer pageSize){

        if (page == null) {
            page = COMMON_START_PAGE;
        }

        if (pageSize == null) {
            pageSize = COMMON_PAGE_SIZE;
        }

        PagedGridResult pagedGridResult = fansService.queryMyFollows(myId, page, pageSize);
        return GraceJSONResult.ok(pagedGridResult);
    }

    @GetMapping("/queryMyFans")
    public GraceJSONResult queryMyFans(@RequestParam String myId,
                                          @RequestParam Integer page,
                                          @RequestParam Integer pageSize){

        if (page == null) {
            page = COMMON_START_PAGE;
        }

        if (pageSize == null) {
            pageSize = COMMON_PAGE_SIZE;
        }

        PagedGridResult pagedGridResult = fansService.queryMyFans(myId, page, pageSize);
        return GraceJSONResult.ok(pagedGridResult);
    }


}
