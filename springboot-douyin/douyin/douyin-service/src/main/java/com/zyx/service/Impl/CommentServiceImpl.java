package com.zyx.service.Impl;

import com.github.pagehelper.PageHelper;
import com.zyx.bo.CommentBO;
import com.zyx.config.BaseInfoProperties;
import com.zyx.config.RabbitMQConfig;
import com.zyx.enums.MessageEnum;
import com.zyx.enums.YesOrNo;
import com.zyx.mapper.CommentMapper;
import com.zyx.mapper.CommentMapperCustom;
import com.zyx.mo.MessageMO;
import com.zyx.pojo.Comment;
import com.zyx.pojo.Vlog;
import com.zyx.service.CommentService;
import com.zyx.service.MsgService;
import com.zyx.service.VlogService;
import com.zyx.utils.JsonUtils;
import com.zyx.utils.PagedGridResult;
import com.zyx.vo.CommentVO;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
public class CommentServiceImpl extends BaseInfoProperties implements CommentService {

    @Resource
    private CommentMapper commentMapper;

    @Resource
    private CommentMapperCustom commentMapperCustom;

    @Resource
    private MsgService msgService;

    @Resource
    private VlogService vlogService;

    @Autowired
    private Sid sid;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public CommentVO addComment(CommentBO commentBO) {

        String id = sid.nextShort();

        Comment comment = new Comment();
        comment.setId(id);

        comment.setVlogId(commentBO.getVlogId());
        comment.setVlogerId(commentBO.getVlogerId());
        comment.setFatherCommentId(commentBO.getFatherCommentId());
        comment.setCommentUserId(commentBO.getCommentUserId());
        comment.setContent(commentBO.getContent());
        comment.setLikeCounts(0);
        comment.setCreateTime(new Date());  

        commentMapper.insert(comment);

        //视频评论总数
        redis.increment(REDIS_VLOG_COMMENT_COUNTS + ":" + commentBO.getVlogId() ,1);

        //将留言后的最新评论返回给前端进行展示
        CommentVO commentVO = new CommentVO();
        BeanUtils.copyProperties(comment,commentVO);

        Integer type = MessageEnum.COMMENT_VLOG.type;

        //系统消息: 评论/回复
        if (StringUtils.isNotBlank(commentBO.getFatherCommentId())
                && !commentBO.getFatherCommentId().equalsIgnoreCase("0")){
            //不为空->是回复评论
            type = MessageEnum.REPLY_YOU.type;
        }

        Vlog vlog = vlogService.getVlogById(commentBO.getVlogId());
        Map msgContent = new HashMap();
        msgContent.put("vlogId",commentBO.getVlogId());
        msgContent.put("vlogCover",vlog.getCover());
        msgContent.put("commentId",id);
        msgContent.put("commentContent",commentBO.getContent());

//        msgService.doNewMsg(commentBO.getCommentUserId(),
//                            commentVO.getVlogerId(),
//                            type,
//                            msgContent
//                            );

        MessageMO messageMO = new MessageMO();
        messageMO.setFromUserId(commentBO.getCommentUserId());
        messageMO.setToUserId(commentBO.getVlogerId());
        messageMO.setMsgContent(msgContent);

        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_MSG,
                "sys.msg" + type,
                JsonUtils.objectToJson(messageMO));

        return commentVO;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedGridResult queryVlogComments(String vlogId, String userId, Integer page, Integer pageSize) {

        PageHelper.startPage(page,pageSize);
        Map<String,Object> map = new HashMap<>();
        map.put("vlogId",vlogId);

        List<CommentVO> commentList = commentMapperCustom.getCommentList(map);

        for (CommentVO c : commentList){

            String commentId = c.getCommentId();

            //当前视频，某个评论的点赞数
            String countsStr = redis.getHashValue(REDIS_VLOG_COMMENT_LIKED_COUNTS, commentId);
            Integer counts = 0;
            if (StringUtils.isNotBlank(countsStr)){
                counts = Integer.parseInt(countsStr);
            }
            c.setLikeCounts(counts);

            // 判断用户是否点赞过该评论
            String doILike = redis.hget(REDIS_USER_LIKE_COMMENT,userId + ":" + commentId);
            if (StringUtils.isNotBlank(doILike) && doILike.equalsIgnoreCase("1")){
                c.setIsLike(YesOrNo.YES.type);
            }
        }

        return setterPagedGrid(commentList,page);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void deleteComment(String commentUserId, String commentId, String vlogId) {

        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setCommentUserId(commentUserId);

        commentMapper.delete(comment);

        //评论总数累减
        redis.decrement(REDIS_VLOG_COMMENT_COUNTS + ":" + vlogId,1);

    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Comment getCommentById(String id) {
        return commentMapper.selectByPrimaryKey(id);
    }
}
