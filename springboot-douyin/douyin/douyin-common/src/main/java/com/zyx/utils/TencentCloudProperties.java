package com.zyx.utils;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @author 张宇森
 * @version 1.0
 */
@Component
@Data
@PropertySource("classpath:tencentcloud.properties")
@ConfigurationProperties(prefix = "tencent.cloud")
public class TencentCloudProperties {

    private String secretId;
    private String secretKey;

}
