package com.zyx.controller;

import com.zyx.config.BaseInfoProperties;
import com.zyx.grace.result.GraceJSONResult;
import com.zyx.mo.MessageMO;
import com.zyx.service.MsgService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author 张宇森
 * @version 1.0
 */

@Slf4j
@Api(tags = "MsgController 消息接口模块")
@RequestMapping("/msg")
@RestController
public class MsgController extends BaseInfoProperties {

    @Autowired
    private MsgService msgService;

    @GetMapping("/list")
    public GraceJSONResult list(@RequestParam String userId,
                                @RequestParam Integer page,
                                @RequestParam Integer pageSize) {


        //使用Mongodb,从0开始分页,区别于数据库
        if (page == null) {
            page = MONGODB_COMMON_START_PAGE;
        }

        if (pageSize == null) {
            pageSize = COMMON_PAGE_SIZE;
        }

        List<MessageMO> messageList = msgService.queryMsgList(userId, page, pageSize);

        return GraceJSONResult.ok(messageList);
    }
}
