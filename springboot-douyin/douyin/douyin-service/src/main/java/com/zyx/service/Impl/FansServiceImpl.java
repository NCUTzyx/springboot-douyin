package com.zyx.service.Impl;

import com.github.pagehelper.PageHelper;
import com.zyx.config.BaseInfoProperties;
import com.zyx.config.RabbitMQConfig;
import com.zyx.enums.MessageEnum;
import com.zyx.enums.YesOrNo;
import com.zyx.mapper.FansMapper;
import com.zyx.mapper.FansMapperCustom;
import com.zyx.mo.MessageMO;
import com.zyx.pojo.Fans;
import com.zyx.service.FansService;
import com.zyx.service.MsgService;
import com.zyx.utils.JsonUtils;
import com.zyx.utils.PagedGridResult;
import com.zyx.vo.FansVO;
import com.zyx.vo.VlogerVO;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Id;
import org.n3r.idworker.Sid;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 张宇森
 * @version 1.0
 */
@Service
public class FansServiceImpl extends BaseInfoProperties implements FansService {

    @Resource
    private FansMapper fansMapper;

    @Resource
    private FansMapperCustom fansMapperCustom;

    @Autowired
    private Sid sid;

    @Autowired
    private MsgService msgService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void doFollow(String myId, String volgerId) {

        String fid = sid.nextShort();
        Fans fans = new Fans();
        fans.setId(fid);
        fans.setFanId(myId);
        fans.setVlogerId(volgerId);

        //判断对方是否关注我，如果关注我，那么双方都要互为朋友关系
        Fans my = queryFansRelation(volgerId, myId);
        if (my != null) {
            fans.setIsFanFriendOfMine(YesOrNo.YES.type);

            my.setIsFanFriendOfMine(YesOrNo.YES.type);
            fansMapper.updateByPrimaryKeySelective(my);
        } else {
            fans.setIsFanFriendOfMine(YesOrNo.NO.type);
        }

        fansMapper.insert(fans);

        //系统消息: 关注 =>同步的
//        msgService.doNewMsg(myId,volgerId, MessageEnum.FOLLOW_YOU.type,null);

        MessageMO messageMO = new MessageMO();
        messageMO.setFromUserId(myId);
        messageMO.setToUserId(volgerId);

        //优化: 使用rabbitmq 异步解耦
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_MSG,
                "sys.msg" + MessageEnum.FOLLOW_YOU.enValue,
                JsonUtils.objectToJson(messageMO));

    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void doCancel(String myId, String volgerId) {

        //判断是否朋友关系，如果是，取消双方的关系
        Fans fan = queryFansRelation(myId, volgerId);
        if (fan != null && fan.getIsFanFriendOfMine().equals(YesOrNo.YES.type)){
            // 抹除双方的朋友关系 =》 删除自己的关系
            Fans my = queryFansRelation(volgerId, myId);
            my.setIsFanFriendOfMine(YesOrNo.NO.type);
            fansMapper.updateByPrimaryKeySelective(my);
        }

        //删除自己的关注关联表记录
        fansMapper.delete(fan);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public boolean queryIsFollowVloger(String myId, String volgerId) {

        Fans fan = queryFansRelation(myId, volgerId);
        return fan != null;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedGridResult queryMyFollows(String myId, Integer page, Integer pageSize) {

        Map<String,Object> map = new HashMap<>();
        map.put("myId",myId);

        PageHelper.startPage(page,pageSize);

        List<VlogerVO> list = fansMapperCustom.queryMyFollows(map);

        return setterPagedGrid(list,page);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedGridResult queryMyFans(String myId, Integer page, Integer pageSize) {

        Map<String,Object> map = new HashMap<>();
        map.put("myId",myId);
        PageHelper.startPage(page,pageSize);
        List<FansVO> list = fansMapperCustom.queryMyFans(map);

        //判断粉丝是否是我的朋友（互粉互关）
        //关注/取关 关联关系保存在redis中，不要依赖数据库
        //1.首先数据库查询，然后循环查询redis
        for (FansVO f : list){
            String s = redis.get(REDIS_FANS_AND_VLOGGER_RELATIONSHIP + ":" + myId + ":" + f.getFanId());
            if (StringUtils.isNotBlank(s) && s.equalsIgnoreCase("1")){
                f.setFriend(true);
            }
        }

        return setterPagedGrid(list,page);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public Fans queryFansRelation(String fanId, String vlogerId) {

        Example example = new Example(Fans.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("vlogerId",vlogerId);
        criteria.andEqualTo("fanId",fanId);
        List<Fans> list = fansMapper.selectByExample(example);
        Fans fans = null;
        if (list != null && list.size() > 0 && !list.isEmpty()) {
            fans = list.get(0);
        }
        return fans;
    }
}
