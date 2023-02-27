package com.zyx.service.Impl;

import com.github.pagehelper.PageHelper;
import com.zyx.bo.VlogBO;
import com.zyx.config.BaseInfoProperties;
import com.zyx.config.RabbitMQConfig;
import com.zyx.enums.MessageEnum;
import com.zyx.enums.YesOrNo;
import com.zyx.mapper.MyLikedVlogMapper;
import com.zyx.mapper.VlogMapper;
import com.zyx.mapper.VlogMapperCustom;
import com.zyx.mo.MessageMO;
import com.zyx.pojo.MyLikedVlog;
import com.zyx.pojo.Vlog;
import com.zyx.service.FansService;
import com.zyx.service.MsgService;
import com.zyx.service.VlogService;
import com.zyx.utils.JsonUtils;
import com.zyx.utils.PagedGridResult;
import com.zyx.vo.VlogVO;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 张宇森
 * @version 1.0
 */
@Service
public class VlogServiceImpl extends BaseInfoProperties implements VlogService {

    @Resource
    private VlogMapper vlogMapper;

    @Resource
    private VlogMapperCustom vlogMapperCustom;

    @Autowired
    private MyLikedVlogMapper myLikedVlogMapper;

    @Autowired
    private FansService fansService;

    @Autowired
    private MsgService msgService;

    @Autowired
    private Sid sid;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void addVlog(VlogBO vlogBO) {

        Vlog vlog = new Vlog();
        String id = sid.nextShort();
        BeanUtils.copyProperties(vlogBO,vlog);
        vlog.setId(id);

        vlog.setLikeCounts(0);
        vlog.setCommentsCounts(0);
        vlog.setIsPrivate(YesOrNo.NO.type);
        vlog.setCreatedTime(new Date());
        vlog.setUpdatedTime(new Date());

        vlogMapper.insert(vlog);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Vlog queryById(String vlogId) {

        return vlogMapper.selectByPrimaryKey(vlogId);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedGridResult getVlogList(String userId,String search, Integer page, Integer pageSize) {

        //分页设置
        PageHelper.startPage(page,pageSize);

        Map<String,Object> map = new HashMap<>();
        if (StringUtils.isNotBlank(search)){
            map.put("search",search);
        }

        List<VlogVO> vlogList = vlogMapperCustom.getVlogList(map);

        for (VlogVO v: vlogList){
            String vlogerId = v.getVlogerId();
            String vlogId = v.getVlogId();
            if (StringUtils.isNotBlank(userId)){
                //用户是否关注该博主
                boolean b1 = fansService.queryIsFollowVloger(userId, vlogerId);
                v.setDoIFollowVloger(b1);
                //判断当前用户是否点赞过该视频
                boolean b2 = doILikeVlog(userId, vlogId);
                v.setDoILikeThisVlog(b2);
            }
            //获得当前视频被点赞过的总数
            Integer num = getVlogBeLikeCounts(vlogId);
            v.setLikeCounts(num);
        }
        return setterPagedGrid(vlogList,page);
    }

    private VlogVO setterVO(VlogVO v,String userId) {

        String vlogerId = v.getVlogerId();
        String vlogId = v.getVlogId();
        if (StringUtils.isNotBlank(userId)){
            //用户是否关注该博主
            boolean b1 = fansService.queryIsFollowVloger(userId, vlogerId);
            v.setDoIFollowVloger(b1);
            //判断当前用户是否点赞过该视频
            boolean b2 = doILikeVlog(userId, vlogId);
            v.setDoILikeThisVlog(b2);
        }
        //获得当前视频被点赞过的总数
        Integer num = getVlogBeLikeCounts(vlogId);
        v.setLikeCounts(num);

        return v;
    }

    private boolean doILikeVlog(String userId,String vlogId) {

        String doILike = redis.get(REDIS_USER_LIKE_VLOG + ":" + userId + ":" + vlogId);
        boolean isLike = false;
        if (StringUtils.isNotBlank(doILike) && doILike.equalsIgnoreCase("1")){
            isLike = true;
        }
        return isLike;
    }

    @Override
    public Integer getVlogBeLikeCounts(String vlogId) {
        String countsStr = redis.get(REDIS_VLOG_BE_LIKED_COUNTS + ":" + vlogId);
        if (StringUtils.isBlank(countsStr)){
            countsStr = "0";
        }

        return Integer.parseInt(countsStr);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public VlogVO getVlogById(String userId,String vlogId) {

        Map<String,Object> map = new HashMap<>();
        map.put("vlogId",vlogId);
        List<VlogVO> vlogVOList = vlogMapperCustom.getVlogById(map);

        if (vlogVOList != null && vlogVOList.size() > 0 && !vlogVOList.isEmpty()) {
            VlogVO vlogVO = vlogVOList.get(0);
            return setterVO(vlogVO, userId);
        }
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void changeToPriOrPub(String userId, String vlogId, Integer yesOrNo) {

        Example example = new Example(Vlog.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id",vlogId);
        criteria.andEqualTo("vlogerId",userId);

        Vlog vlog = new Vlog();
        vlog.setIsPrivate(yesOrNo);

        vlogMapper.updateByExampleSelective(vlog,example);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedGridResult queryMyVlogList(String userId, Integer page, Integer pageSize, Integer yesOrNo) {

        //分页设置
        PageHelper.startPage(page,pageSize);

        Example example = new Example(Vlog.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("vlogerId",userId);
        criteria.andEqualTo("isPrivate",yesOrNo);

        List<Vlog> vlogs = vlogMapper.selectByExample(example);
        return setterPagedGrid(vlogs,page);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void userLikeVlog(String userId, String vlogId) {

        String rid  = sid.nextShort();
        MyLikedVlog likedVlog = new MyLikedVlog();
        likedVlog.setId(rid);
        likedVlog.setVlogId(vlogId);
        likedVlog.setUserId(userId);

        myLikedVlogMapper.insert(likedVlog);

        //系统消息: 点赞短视频
        Vlog vlog = this.getVlogById(vlogId);
        Map msgContent = new HashMap();
        msgContent.put("vlogId",vlogId);
        msgContent.put("vlogCover",vlog.getCover());

//        msgService.doNewMsg(userId,vlog.getVlogerId(), MessageEnum.LIKE_VLOG.type,msgContent);

        MessageMO messageMO = new MessageMO();
        messageMO.setFromUserId(userId);
        messageMO.setToUserId(vlog.getVlogerId());
        messageMO.setMsgContent(msgContent);

        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_MSG,
                            "sys.msg" + MessageEnum.LIKE_VLOG,
                                      JsonUtils.objectToJson(messageMO));

    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Vlog getVlogById(String id) {
        return vlogMapper.selectByPrimaryKey(id);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void userUnLikeVlog(String userId, String vlogId) {

        MyLikedVlog unlikedVlog = new MyLikedVlog();
        unlikedVlog.setVlogId(vlogId);
        unlikedVlog.setUserId(userId);
        myLikedVlogMapper.delete(unlikedVlog);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedGridResult getMyLikedVlogList(String userId, Integer page, Integer pageSize) {

        PageHelper.startPage(page,pageSize);

        Map<String,Object> map = new HashMap<>();
        map.put("userId",userId);
        List<Vlog> list = vlogMapperCustom.getMyLikedVlogList(map);

        return setterPagedGrid(list,page);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedGridResult getMyFollowedVlogList(String myId, Integer page, Integer pageSize) {

        PageHelper.startPage(page,pageSize);
        Map<String,Object> map = new HashMap<>();
        map.put("myId",myId);
        List<VlogVO> list = vlogMapperCustom.getMyFollowedVlogList(map);

        for (VlogVO v: list){
            String vlogId = v.getVlogId();
            if (StringUtils.isNotBlank(myId)){
                //用户必定关注该播主
                v.setDoIFollowVloger(true);
                //判断当前用户是否点赞过该视频
                v.setDoILikeThisVlog(doILikeVlog(myId, vlogId));
            }
            //获得当前视频被点赞过的总数
            Integer num = getVlogBeLikeCounts(vlogId);
            v.setLikeCounts(num);
        }
        return setterPagedGrid(list,page);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedGridResult getMyFriendVlogList(String myId, Integer page, Integer pageSize) {

        PageHelper.startPage(page,pageSize);
        Map<String,Object> map = new HashMap<>();
        map.put("myId",myId);

        List<VlogVO> list = vlogMapperCustom.getMyFriendVlogList(map);
        for (VlogVO v: list){
            String vlogId = v.getVlogId();
            if (StringUtils.isNotBlank(myId)){
                //用户必定关注该播主
                v.setDoIFollowVloger(true);
                //判断当前用户是否点赞过该视频
                v.setDoILikeThisVlog(doILikeVlog(myId, vlogId));
            }
            //获得当前视频被点赞过的总数
            Integer num = getVlogBeLikeCounts(vlogId);
            v.setLikeCounts(num);
        }

        return setterPagedGrid(list,page);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void flushCounts(String vlogId, Integer counts) {

        Vlog vlog = new Vlog();
        vlog.setId(vlogId);
        vlog.setLikeCounts(counts);
        vlogMapper.updateByPrimaryKeySelective(vlog);
    }
}
