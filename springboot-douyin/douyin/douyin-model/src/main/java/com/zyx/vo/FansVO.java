package com.zyx.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author 张宇森
 * @version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class FansVO {

    private String fanId;
    private String nickname;
    private String face;
    private boolean isFriend = false;
}
