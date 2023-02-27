package com.zyx.mapper;

import com.zyx.my.mapper.MyMapper;
import com.zyx.pojo.Users;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersMapper extends MyMapper<Users> {
}