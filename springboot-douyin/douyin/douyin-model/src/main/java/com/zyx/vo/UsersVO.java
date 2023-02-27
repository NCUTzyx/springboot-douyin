package com.zyx.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Id;
import java.util.Date;

/**
 * @author 张宇森
 * @version 1.0
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UsersVO {

    @Id
    private String id;
    private String mobile;
    private String nickname;
    private String imoocNum;
    private String face;
    private Integer sex;
    private Date birthday;
    private String country;
    private String province;
    private String city;
    private String district;
    private String description;
    private String bgImg;
    private Integer canImoocNumBeUpdated;
    private Date createdTime;
    private Date updatedTime;

    private String userToken;  //用户token，传递给前端的

    private Integer myFollowsCounts;
    private Integer myFansCounts;
    private Integer totalLikeMeCounts;

}
