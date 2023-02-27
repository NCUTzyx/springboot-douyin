package com.zyx.service.Impl;

import com.zyx.bo.UserInfoBO;
import com.zyx.enums.Sex;
import com.zyx.enums.UserInfoModifyType;
import com.zyx.enums.YesOrNo;
import com.zyx.exceptions.GraceException;
import com.zyx.grace.result.ResponseStatusEnum;
import com.zyx.mapper.UsersMapper;
import com.zyx.pojo.Users;
import com.zyx.service.UserService;
import com.zyx.utils.DateUtil;
import com.zyx.utils.DesensitizationUtil;
import org.n3r.idworker.Sid;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author 张宇森
 * @version 1.0
 */
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UsersMapper usersMapper;

    @Resource
    private Sid sid;

    private static final String User_FACE = "https://cdn.acwing.com/media/user/profile/photo/112333_lg_21b5288d72.jpg";

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Users queryMobileIsExist(String mobile) {

        Example example = new Example(Users.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("mobile",mobile);
        Users user = usersMapper.selectOneByExample(example);

        return user;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Users createUser(String mobile) {

        String userId = sid.nextShort();

        Users user = new Users();
        user.setId(userId);
        user.setMobile(mobile);
        user.setNickname("用户: " + DesensitizationUtil.commonDisplay(mobile));
        user.setImoocNum("用户: " + DesensitizationUtil.commonDisplay(mobile));
        user.setFace(User_FACE);

        user.setBirthday(DateUtil.stringToDate("1923-01-01"));
        user.setSex(Sex.secret.type);

        user.setCountry("中国");
        user.setProvince("");
        user.setCity("");
        user.setDistrict("");
        user.setDescription("这家伙很懒，什么都没留下~");
        user.setCanImoocNumBeUpdated(YesOrNo.YES.type);

        user.setCreatedTime(new Date());
        user.setUpdatedTime(new Date());

        usersMapper.insert(user);

        return user;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Users queryUser(String userId) {

        return usersMapper.selectByPrimaryKey(userId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Users updateUserInfo(UserInfoBO userInfoBO) {

        Users user = new Users();
        BeanUtils.copyProperties(userInfoBO,user);

        int update = usersMapper.updateByPrimaryKeySelective(user);
        if (update != 1){
            GraceException.display(ResponseStatusEnum.USER_INFO_UPDATED_ERROR);
        }

        return queryUser(user.getId());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Users updateUserInfo(UserInfoBO userInfoBO, Integer type) {

        //进行前置的检验
        Example example = new Example(Users.class);
        Example.Criteria criteria = example.createCriteria();

        if (type.equals(UserInfoModifyType.NICKNAME.type)){

            criteria.andEqualTo("nickname",userInfoBO.getNickname());
            Users user = usersMapper.selectOneByExample(example);
            if (user != null) {
                GraceException.display(ResponseStatusEnum.USER_INFO_UPDATED_NICKNAME_EXIST_ERROR);
            }
        }

        if (type.equals(UserInfoModifyType.IMOOCNUM.type)){

            //判断是否重复
            criteria.andEqualTo("imoocNum",userInfoBO.getImoocNum());
            Users user = usersMapper.selectOneByExample(example);
            if (user != null) {
                GraceException.display(ResponseStatusEnum.USER_INFO_UPDATED_IMOOCNUM_EXIST_ERROR);
            }

            //查看抖音号是否可以被修改
            Users userTemp = queryUser(userInfoBO.getId());
            if (userTemp.getCanImoocNumBeUpdated().equals(YesOrNo.NO.type)) {
                GraceException.display(ResponseStatusEnum.USER_INFO_CANT_UPDATED_IMOOCNUM_ERROR);
            }

            userInfoBO.setCanImoocNumBeUpdated(YesOrNo.NO.type);
        }

        return updateUserInfo(userInfoBO);
    }
}
