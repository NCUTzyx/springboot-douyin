package com.zyx.config;

import com.zyx.interceptor.PassportInterceptor;
import com.zyx.interceptor.UserAccessInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author 张宇森
 * @version 1.0
 * 拦截器配置类 注册短信拦截器
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Bean
    public PassportInterceptor passportInterceptor(){
        return new PassportInterceptor();
    }

    @Bean
    public UserAccessInterceptor userAccessInterceptor(){
        return new UserAccessInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        //添加拦截路由
        registry.addInterceptor(passportInterceptor())
                .addPathPatterns("/passport/getSMSCode");

        registry.addInterceptor(userAccessInterceptor())
                .addPathPatterns("/userInfo/modifyUserInfo")
                .addPathPatterns("/userInfo/modifyImage");

    }
}
