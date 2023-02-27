package com.zyx.service;

import com.zyx.mo.MessageMO;

import java.util.List;
import java.util.Map;

/**
 * @author 张宇森
 * @version 1.0
 */
public interface MsgService {

    //新建消息
    void doNewMsg(String fromUserId, String toUserId, Integer msgType, Map msgContent);

    //查询消息列表
    List<MessageMO> queryMsgList(String toUserId, Integer page, Integer pageSize);

}
