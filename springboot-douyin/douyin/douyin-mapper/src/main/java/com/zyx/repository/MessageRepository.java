package com.zyx.repository;

import com.zyx.mo.MessageMO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author 张宇森
 * @version 1.0
 */
@Repository
public interface MessageRepository extends MongoRepository<MessageMO,String> {

    // 通过实现MongoRepository，自定义条件查询
    // Pageable 提供分页的能力
    List<MessageMO> findAllByToUserIdOrderByCreateTimeDesc(String toUserId, Pageable pageable);




}
