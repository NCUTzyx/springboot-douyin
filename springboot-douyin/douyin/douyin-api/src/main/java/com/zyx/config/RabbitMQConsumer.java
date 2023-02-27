package com.zyx.config;

import com.zyx.enums.MessageEnum;
import com.zyx.exceptions.GraceException;
import com.zyx.grace.result.ResponseStatusEnum;
import com.zyx.mo.MessageMO;
import com.zyx.service.MsgService;
import com.zyx.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * @author 张宇森
 * @version 1.0
 */
@Slf4j
@Configuration
public class RabbitMQConsumer {

    @Autowired
    private MsgService msgService;

    //监听队列
    @RabbitListener(queues = RabbitMQConfig.QUEUE_SYS_MSG)
    public void listenQueue(String payload, Message message) {  //payload: 消息的载体 message: 消息的具体信息

        log.info(payload);
        MessageMO messageMO = JsonUtils.jsonToPojo(payload, MessageMO.class);

        String routingKey = message.getMessageProperties().getReceivedRoutingKey();
        log.info(routingKey);

        if (routingKey.equalsIgnoreCase("sys.msg.follow")) {
            //关注
            msgService.doNewMsg(
                    messageMO.getFromUserId(),
                    messageMO.getToUserId(),
                    MessageEnum.FOLLOW_YOU.type,
                    null);

        } else if(routingKey.equalsIgnoreCase("sys.msg.likeVideo")) {
            //点赞视频
            msgService.doNewMsg(
                    messageMO.getFromUserId(),
                    messageMO.getToUserId(),
                    MessageEnum.LIKE_VLOG.type,
                    messageMO.getMsgContent());

        } else if(routingKey.equalsIgnoreCase("sys.msg.comment")) {
            //评论视频
            msgService.doNewMsg(
                    messageMO.getFromUserId(),
                    messageMO.getToUserId(),
                    MessageEnum.COMMENT_VLOG.type,
                    messageMO.getMsgContent());

        } else if (routingKey.equalsIgnoreCase("sys.msg.replay")) {
            //回复评论
            msgService.doNewMsg(
                    messageMO.getFromUserId(),
                    messageMO.getToUserId(),
                    MessageEnum.REPLY_YOU.type,
                    messageMO.getMsgContent());

        } else if (routingKey.equalsIgnoreCase("sys.msg.likeComment")){
            //点赞评论
            msgService.doNewMsg(
                    messageMO.getFromUserId(),
                    messageMO.getToUserId(),
                    MessageEnum.LIKE_COMMENT.type,
                    messageMO.getMsgContent());

        } else {
            GraceException.display(ResponseStatusEnum.SYSTEM_OPERATION_ERROR);
        }
    }

}
