package com.zyx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author 张宇森
 * @version 1.0
 */

@SpringBootApplication
@MapperScan(basePackages = "com.zyx.mapper")
@ComponentScan(basePackages = {"com.zyx","org.n3r.idworker"})
@EnableMongoRepositories   //Mongodb 启动器
public class Application {
    public static void main(String[] args){
        SpringApplication.run(Application.class);
    }
}
