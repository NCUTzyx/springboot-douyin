package com.zyx.controller;

import com.zyx.bo.LoginBO;
import com.zyx.config.BaseInfoProperties;
import com.zyx.grace.result.GraceJSONResult;
import com.zyx.grace.result.ResponseStatusEnum;
import com.zyx.pojo.Users;
import com.zyx.service.UserService;
import com.zyx.utils.IPUtil;
import com.zyx.utils.SMSUtils;
import com.zyx.vo.UsersVO;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Random;
import java.util.UUID;

/**
 * @author 张宇森
 * @version 1.0
 */

@Slf4j
@Api(tags = "PassPortController 通行证接口模块")
@RequestMapping("/passport")
@RestController
public class PassPortController extends BaseInfoProperties {

    @Resource
    private SMSUtils smsUtils;

    @Resource
    private UserService userService;

    @PostMapping("/getSMSCode")
    public GraceJSONResult getSMSCode(@RequestParam String phone, HttpServletRequest httpServletRequest) throws Exception {

        if (StringUtils.isBlank(phone)){
            return GraceJSONResult.ok();
        }

        // 获取用户ip,
        String userIP = IPUtil.getRequestIp(httpServletRequest);

        // 根据用户ip进行限制，限制用户在60秒之内只能获得一次验证码
        redis.setnx60s(MOBILE_SMSCODE + ":" +userIP,userIP);

        StringBuilder str = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            str.append(random.nextInt(10));
        }
        //随机生成的验证码
        String code = str.toString();
        smsUtils.sendSMS(phone,code);
        log.info(code);

        // 把验证码放入到redis中，用于后续的验证  10分钟失效
        redis.set(MOBILE_SMSCODE + ":" +phone,code,10*60);
        return GraceJSONResult.ok();
    }

    @PostMapping("/login")   //开启@Valid，进行校验
    public GraceJSONResult login(@Valid @RequestBody LoginBO loginBO,
                                 //BindingResult result, 对代码有侵入性
                                 HttpServletRequest httpServletRequest) throws Exception{

        //1.判断result中是否保存了错误的验证信息，如果有需要返回到前端 => 移送到GraceExceptionHandler进行处理

        String mobile = loginBO.getMobile();
        String code = loginBO.getSmsCode();

        //验证码检验
        //2.从redis 中获得验证码并进行校验
        String redisCode = redis.get(MOBILE_SMSCODE + ":" + mobile);
        if (StringUtils.isBlank(redisCode) || !redisCode.equalsIgnoreCase(code)){
             return GraceJSONResult.errorCustom(ResponseStatusEnum.SMS_CODE_ERROR);
        }

        //3.查询数据库判断用户是否存在
        Users user = userService.queryMobileIsExist(mobile);
        if (user == null){
            //用户为null,则注册user
            user = userService.createUser(mobile);
        }

        //4.如果不为null,保存用户信息和会话（token）信息
        String userToken = UUID.randomUUID().toString();
        redis.set(REDIS_USER_TOKEN + ":" + user.getId(),userToken);  //不设置timeout，代表永远存在

        //5.用户登陆成功后，删除redis中的验证码
        redis.del(MOBILE_SMSCODE+":"+mobile);

        //6.返回用户信息，包含token令牌
        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(user,usersVO);
        usersVO.setUserToken(userToken);

        return GraceJSONResult.ok(usersVO);
    }

    @PostMapping("/logout")
    public GraceJSONResult logout(@RequestParam String userId, HttpServletRequest httpServletRequest) throws Exception {

        //后端只需要清除用户的token即可
        redis.del(REDIS_USER_TOKEN + ":" + userId);
        return GraceJSONResult.ok();
    }
}
