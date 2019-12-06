package com.atguigu.gmall.wms.config;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

//延迟队列
// 部署集群   使用多线程会出现死锁现象   解决方法:分布式锁
@Component
public class ScheduledDemo {
    @Scheduled(fixedDelay = 10000)
    public  void test(){
        System.out.println("======" + LocalDateTime.now());
    }
}












































