package com.zyx.mo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.Map;

/**
 * @author 张宇森
 * @version 1.0
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Document("message")   //Mongodb 映射
public class MessageMO {

    @Id  //主键id  mongodb自动生成
    private String id;

    @Field("fromUserId")
    private String fromUserId;
    @Field("fromNickname")
    private String fromNickname;
    @Field("fromFace")
    private String fromFace;
    @Field("toUserId")
    private String toUserId;
    @Field("msgType")
    private Integer msgType;  //枚举
    @Field("msgContent")
    private Map msgContent;
    @Field("createTime")
    private Date createTime;

}
