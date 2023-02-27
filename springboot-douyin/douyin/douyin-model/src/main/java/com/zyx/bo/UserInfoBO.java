package com.zyx.bo;

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
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoBO {

    @Id
    private String id;
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
}
