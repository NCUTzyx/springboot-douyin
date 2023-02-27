package com.zyx.mapper;

import com.zyx.pojo.Vlog;
import com.zyx.vo.VlogVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


@Repository
public interface VlogMapperCustom {

    List<VlogVO> getVlogList(@Param("paramMap") Map<String,Object> map);

    List<VlogVO> getVlogById(@Param("paramMap") Map<String,Object> map);

    List<Vlog> getMyLikedVlogList(@Param("paramMap") Map<String,Object> map);

    List<VlogVO> getMyFollowedVlogList(@Param("paramMap") Map<String,Object> map);

    List<VlogVO> getMyFriendVlogList(@Param("paramMap") Map<String,Object> map);


}