package com.zyx.controller;

import com.zyx.bo.CommentBO;
import com.zyx.config.BaseInfoProperties;
import com.zyx.config.RabbitMQConfig;
import com.zyx.enums.MessageEnum;
import com.zyx.grace.result.GraceJSONResult;
import com.zyx.mapper.CommentMapper;
import com.zyx.mo.MessageMO;
import com.zyx.pojo.Comment;
import com.zyx.pojo.Vlog;
import com.zyx.service.CommentService;
import com.zyx.service.MsgService;
import com.zyx.service.VlogService;
import com.zyx.utils.JsonUtils;
import com.zyx.utils.PagedGridResult;
import com.zyx.vo.CommentVO;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;


/**
 * @author 张宇森
 * @version 1.0
 */

@Slf4j
@Api(tags = "CommentController 评论模块接口")
@RequestMapping("/comment")
@RestController
public class CommentController extends BaseInfoProperties {

    @Resource
    private CommentService commentService;

    @Resource
    private MsgService msgService;

    @Resource
    private VlogService vlogService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostMapping("/create")
    public GraceJSONResult create(@Valid @RequestBody CommentBO commentBO){

        CommentVO commentVO = commentService.addComment(commentBO);
        return GraceJSONResult.ok(commentVO);
    }

    @GetMapping("/counts")
    public GraceJSONResult counts(@RequestParam String vlogId){

        String countsStr = redis.get(REDIS_VLOG_COMMENT_COUNTS + ":" + vlogId);
        if (StringUtils.isBlank(countsStr)){
            countsStr = "0";
        }

        return GraceJSONResult.ok(Integer.parseInt(countsStr));
    }

    @GetMapping("/list")
    public GraceJSONResult list(@RequestParam String vlogId,
                                @RequestParam(defaultValue = "") String userId,
                                @RequestParam Integer page,
                                @RequestParam Integer pageSize){

        if (page == null) {
            page = COMMON_START_PAGE;
        }

        if (pageSize == null) {
            pageSize = COMMON_PAGE_SIZE;
        }

        PagedGridResult pagedGridResult = commentService.queryVlogComments(vlogId,userId, page, pageSize);
        return GraceJSONResult.ok(pagedGridResult);
    }

    @DeleteMapping("/delete")
    public GraceJSONResult delete(@RequestParam String commentUserId,
                                  @RequestParam String commentId,
                                  @RequestParam String vlogId){

        commentService.deleteComment(commentUserId,commentId,vlogId);
        return GraceJSONResult.ok();
    }

    @PostMapping("/like")
    public GraceJSONResult like(@RequestParam String commentId,
                                @RequestParam String userId){

        // bigkey
        redis.incrementHash(REDIS_VLOG_COMMENT_LIKED_COUNTS,commentId,1);
        redis.setHashValue(REDIS_USER_LIKE_COMMENT,userId + ":" + commentId,"1");

        // 系统消息: 点赞评论
        Comment comment = commentService.getCommentById(commentId);
        Vlog vlog = vlogService.getVlogById(comment.getVlogId());
        Map msgContent = new HashMap();
        msgContent.put("vlogId",vlog.getId());
        msgContent.put("vlogCover",vlog.getCover());
        msgContent.put("commentId",commentId);

        MessageMO messageMO = new MessageMO();
        messageMO.setFromUserId(userId);
        messageMO.setToUserId(vlog.getVlogerId());
        messageMO.setMsgContent(msgContent);

        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_MSG,
                "sys.msg" + MessageEnum.LIKE_COMMENT,
                JsonUtils.objectToJson(messageMO));

//        msgService.doNewMsg(userId,comment.getCommentUserId(), MessageEnum.LIKE_COMMENT.type,msgContent);

        return GraceJSONResult.ok();
    }

    @PostMapping("/unlike")
    public GraceJSONResult unlike(@RequestParam String commentId,
                                @RequestParam String userId){


        redis.decrementHash(REDIS_VLOG_COMMENT_LIKED_COUNTS,commentId,1);
        redis.hdel(REDIS_USER_LIKE_COMMENT,userId + ":" + commentId);

        return GraceJSONResult.ok();
    }



}
