package com.zyx.interceptor;

import com.zyx.config.BaseInfoProperties;
import com.zyx.exceptions.GraceException;
import com.zyx.grace.result.ResponseStatusEnum;
import com.zyx.utils.IPUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 张宇森
 * @version 1.0
 * 对用户是否合法操作（是否登录）进行验证
 */
@Slf4j
public class UserAccessInterceptor extends BaseInfoProperties implements HandlerInterceptor {

    //拦截请求 =》访问请求前
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //从header中获取用户id和token
        String userId = request.getHeader("headerUserId");
        String token = request.getHeader("headerUserToken");

        //判断id 和 token 不能为null
        if (StringUtils.isNotBlank(userId) && StringUtils.isNotBlank(token)) {

            String redisToken = redis.get(REDIS_USER_TOKEN + ":" + userId);
            if (StringUtils.isBlank(redisToken)){
                GraceException.display(ResponseStatusEnum.UN_LOGIN);
                return false;
            } else {
                //比较token是否一致
                //不一致，表示被顶号了
                if (!redisToken.equalsIgnoreCase(token)) {
                    GraceException.display(ResponseStatusEnum.TICKET_INVALID);
                    return false;
                }
            }
        } else {
            GraceException.display(ResponseStatusEnum.UN_LOGIN);
            return false;
        }
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
