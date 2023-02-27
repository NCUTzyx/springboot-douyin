package com.zyx.service.Impl;

import com.zyx.config.BaseInfoProperties;
import com.zyx.enums.MessageEnum;
import com.zyx.mo.MessageMO;
import com.zyx.pojo.Users;
import com.zyx.repository.MessageRepository;
import com.zyx.service.MsgService;
import com.zyx.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 张宇森
 * @version 1.0
 */
@Service
public class MsgServiceImpl extends BaseInfoProperties implements MsgService {

    @Autowired
    private UserService userService;

    @Autowired
    private MessageRepository messageRepository;

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void doNewMsg(String fromUserId, String toUserId, Integer msgType, Map msgContent) {


        MessageMO messageMO = new MessageMO();

        Users fromUser = userService.queryUser(fromUserId);

        //Mongodb自动生成id
        messageMO.setFromUserId(fromUserId);
        messageMO.setFromNickname(fromUser.getNickname());
        messageMO.setFromFace(fromUser.getFace());

        messageMO.setToUserId(toUserId);

        if (msgContent != null){
            messageMO.setMsgContent(msgContent);
        }
        messageMO.setMsgType(msgType);

        messageMO.setCreateTime(new Date());

        //save 保存单个对象
        messageRepository.save(messageMO);

    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<MessageMO> queryMsgList(String toUserId, Integer page, Integer pageSize) {

        //根据属性构造排序条件
        Pageable pageable = PageRequest.of(page,pageSize, Sort.Direction.DESC,"createTime");

        List<MessageMO> list = messageRepository.findAllByToUserIdOrderByCreateTimeDesc(toUserId, pageable);

        for (MessageMO m : list) {
            //如果类型是关注消息，则需要查询我之前有没有关注此人，前端再做展示 （互关）/（互粉）
            if (m.getMsgType() != null && m.getMsgType().equals(MessageEnum.FOLLOW_YOU.type)) {
                Map map = m.getMsgContent();
                if (map == null) {
                    map = new HashMap();
                }

                String rel = redis.get(REDIS_FANS_AND_VLOGGER_RELATIONSHIP + ":" + m.getToUserId() + ":" + m.getFromUserId());
                if (StringUtils.isNotBlank(rel) && rel.equalsIgnoreCase("1")) {
                    //代表我们是朋友
                    map.put("isFriend",true);
                } else {
                    map.put("isFriend",false);
                }
                m.setMsgContent(map);
            }
        }
        return list;
    }
}
