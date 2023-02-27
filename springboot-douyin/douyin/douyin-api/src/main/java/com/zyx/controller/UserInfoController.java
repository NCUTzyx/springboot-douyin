package com.zyx.controller;

import com.zyx.bo.UserInfoBO;
import com.zyx.config.BaseInfoProperties;
import com.zyx.config.MinIOConfig;
import com.zyx.enums.FileTypeEnum;
import com.zyx.enums.UserInfoModifyType;
import com.zyx.grace.result.GraceJSONResult;
import com.zyx.grace.result.ResponseStatusEnum;
import com.zyx.pojo.Users;
import com.zyx.service.UserService;
import com.zyx.utils.MinIOUtils;
import com.zyx.vo.UsersVO;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * @author 张宇森
 * @version 1.0
 */

@Slf4j
@Api(tags = "UserInfoController 用户信息接口模块")
@RequestMapping("/userInfo")
@RestController
public class UserInfoController extends BaseInfoProperties {

    @Resource
    private UserService userService;

    @Autowired
    private MinIOConfig minIOConfig;

    @GetMapping("/query")
    public GraceJSONResult queryUsersById(@RequestParam String userId){
        Users user = userService.queryUser(userId);
        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(user,usersVO);

        //我的关注数量
        String myFollowsCountsStr = redis.get(REDIS_MY_FOLLOWS_COUNTS + ":" + userId);

        //我的粉丝
        String myFansCountsStr = redis.get(REDIS_MY_FANS_COUNTS + ":" + userId);

        //用户获赞总数 视频博主 （点赞+喜欢）
        String totalLikeMeCountsStr = redis.get(REDIS_VLOGER_BE_LIKED_COUNTS + ":" + userId);

        Integer myFollowsCounts = 0;
        Integer myFansCounts = 0;
        Integer totalLikeMeCounts = 0;

        if (StringUtils.isNotBlank(myFollowsCountsStr)) {
            myFollowsCounts = Integer.parseInt(myFollowsCountsStr);
        }
        if (StringUtils.isNotBlank(myFansCountsStr)) {
            myFansCounts = Integer.parseInt(myFansCountsStr);
        }
        if (StringUtils.isNotBlank(totalLikeMeCountsStr)) {
            totalLikeMeCounts = Integer.parseInt(totalLikeMeCountsStr);
        }

        usersVO.setMyFollowsCounts(myFollowsCounts);
        usersVO.setMyFansCounts(myFansCounts);
        usersVO.setTotalLikeMeCounts(totalLikeMeCounts);

        return GraceJSONResult.ok(usersVO);
    }

    @PostMapping("/modifyUserInfo")
    public GraceJSONResult mmodifyUserInfo(@RequestBody UserInfoBO userInfoBO, @RequestParam Integer type){

        //检验 type 是否正确
        UserInfoModifyType.checkUserInfoTypeIsRight(type);

        Users newUserInfo = userService.updateUserInfo(userInfoBO,type);
        return GraceJSONResult.ok(newUserInfo);
    }

    @PostMapping("/modifyImage")
    public GraceJSONResult modifyImage(@RequestParam String userId,
                                       @RequestParam Integer type,
                                       MultipartFile file) throws Exception {

        if (!type.equals(FileTypeEnum.BGIMG.type) && !type.equals(FileTypeEnum.FACE.type)){
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_FAILD);
        }

        String fileName = file.getOriginalFilename();
        MinIOUtils.uploadFile(minIOConfig.getBucketName(),fileName,file.getInputStream());
        String imgUrl = minIOConfig.getFileHost() + "/" + minIOConfig.getBucketName() + "/" + fileName;

        // 修改图片地址到数据库
        UserInfoBO userInfoBO = new UserInfoBO();
        userInfoBO.setId(userId);

        if (type.equals(FileTypeEnum.BGIMG.type)){
            userInfoBO.setBgImg(imgUrl);
        } else {
            userInfoBO.setFace(imgUrl);
        }
        Users users = userService.updateUserInfo(userInfoBO);
        return GraceJSONResult.ok(users);
    }


}
