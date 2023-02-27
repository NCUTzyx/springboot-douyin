package com.zyx.mapper;

import com.zyx.my.mapper.MyMapper;
import com.zyx.pojo.Fans;
import com.zyx.vo.FansVO;
import com.zyx.vo.VlogerVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface FansMapperCustom extends MyMapper<Fans> {

    List<VlogerVO> queryMyFollows(@Param("paramMap") Map<String,Object> map);

    List<FansVO> queryMyFans(@Param("paramMap") Map<String,Object> map);

}