package com.atguigu.gmall.order.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

// 玩 rabbitMQ 步骤
//1.引入ampq依赖
//2.配置文件
// spring:
//  rabbitmq:
//    host: 192.168.127.66
//    virtual-host: /zpc
//    username: /zpc
//    password: /zpc

//3.
@Configuration
public class RabbitMaConfig {

    @Bean//注入到spring容器中，整个项目中都可以使用
    public Exchange exchange(){
        //声明 一个交换机                                  //是否持久化  //是否自动删除交换机    //其它参数
        return new TopicExchange("WMS-EXCHANGE",true,false,null);
    }

    @Bean
    public Queue queue(){
        Map<String,Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", "OMS-EXCHANGE");
        arguments.put("x-dead-letter-routing-key", "oms");
        arguments.put("x-message-ttl", 60000);//过期时间单位是毫秒

        //声明 一个消息队列     //延时队列                                 //是否排他： 是否只有一个消息
        return new Queue("OMS-TTL-QUEUE",true,false,false);
    }

    //创建交换机和消息队列绑定
    @Bean
    public Binding binding(){
        return new Binding("OMS-TTL-QUEUE",Binding.DestinationType.QUEUE,"OMS-EXCHANGE","oms.close",null);
    }


    @Bean//声明 一个死信队列
    public Queue deadQueue(){
        return new Queue("OMS-DEAD-QUEUE",true,false,false,null);
    }

    //创建交换机和死信队列绑定
    @Bean
    public Binding deadBinding(){
        return new Binding("OMS-DEAD-QUEUE",Binding.DestinationType.QUEUE,"OMS-EXCHANGE","oms.dead",null);
    }

}

















































