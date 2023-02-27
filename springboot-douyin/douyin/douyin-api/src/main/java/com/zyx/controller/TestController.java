package com.zyx.controller;

import com.zyx.config.MinIOConfig;
import com.zyx.config.RabbitMQConfig;
import com.zyx.grace.result.GraceJSONResult;
import com.zyx.utils.MinIOUtils;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


/**
 * @author 张宇森
 * @version 1.0
 */

@Slf4j
@Api(tags = " TestController 项目测试接口")
@RequestMapping("/test")
@RestController
@RefreshScope  //刷新配置
public class TestController {

    @Autowired
    private MinIOConfig minIOConfig;

    @Autowired
    private RabbitTemplate rabbitTemplate;


    @Value("${nacos.counts}")
    private Integer nacosCounts;

    @GetMapping("/nacos")
    public Object nacosCounts(){

        return GraceJSONResult.ok("nacosCounts: " + nacosCounts);
    }

    @PostMapping("/upload")
    public GraceJSONResult upload(MultipartFile file) throws Exception {

        String fileName = file.getOriginalFilename();
        MinIOUtils.uploadFile(minIOConfig.getBucketName(),fileName,file.getInputStream());
        String imgUrl = minIOConfig.getFileHost() + "/" + minIOConfig.getBucketName() + "/" + fileName;
        return GraceJSONResult.ok(imgUrl);
    }

    @GetMapping("/produce")
    public Object produce() {

        //异步解耦
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_MSG,"sys.msg.send","发送消息");
        return GraceJSONResult.ok();
    }

    @GetMapping("/produce2")
    public Object produce2() {

        //异步解耦
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_MSG,"sys.msg.delete","删除消息");
        return GraceJSONResult.ok();
    }

}
