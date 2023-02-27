package com.zyx.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 张宇森
 * @version 1.0
 *  对系统消息 进行 rabbitmq 解耦
 */
@Configuration
public class RabbitMQConfig {

    /**
     * 根据具体模型编写业务代码
     * 1.定义交换机
     * 2.定义队列
     * 3.创建交换机
     * 4.创建队列
     * 5.队列和交换机的绑定
     */

    public static final String EXCHANGE_MSG = "exchange_msg";
    public static final String QUEUE_SYS_MSG = "queue_sys_msg";

    //托管给springboot容器
    @Bean(EXCHANGE_MSG)
    public Exchange exchange(){
        return ExchangeBuilder                   //构建交换机
                .topicExchange(EXCHANGE_MSG)     //topic类型
                .durable(true)                   //设置持久化，重启mq后依然存在
                .build();
    }

    @Bean(QUEUE_SYS_MSG)
    public Queue queue(){
        return new Queue(QUEUE_SYS_MSG);
    }

    @Bean
    public Binding binding(@Qualifier(EXCHANGE_MSG) Exchange exchange,
                           @Qualifier(QUEUE_SYS_MSG) Queue queue){
        /**
         * 路由规则 - route-key
         * display.*.* => display.public.message
         * but display.a.b.c 不可以
         *  *代表一个占位符
         * dispaly.# => display.a.b.c.d
         *  # 代表多个占位符
         */
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with("sys.msg.*")   // 定义路由规则
                .noargs();

    }

}
