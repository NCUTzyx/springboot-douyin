package com.zyx.interceptor;

import com.zyx.config.BaseInfoProperties;
import com.zyx.exceptions.GraceException;
import com.zyx.grace.result.ResponseStatusEnum;
import com.zyx.utils.IPUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 张宇森
 * @version 1.0
 * 短信拦截器 =》 限制用户在60秒之内只能获得一次验证码
 */
@Slf4j
public class PassportInterceptor extends BaseInfoProperties implements HandlerInterceptor {

    //拦截请求 =》访问请求前
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 获得用户的IP
        String userIP = IPUtil.getRequestIp(request);

        // 判断是否存在
        boolean b = redis.keyIsExist(MOBILE_SMSCODE + ":" + userIP);

        if (b){
            GraceException.display(ResponseStatusEnum.SMS_NEED_WAIT_ERROR);
            log.info("已发送短信，请稍后再试!");
            return false;  //拦截
        }
        //请求放行
        return true;
    }

    //请求访问到controller后 =》 渲染视图之前
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    //请求和渲染视图之后，走完controller
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
