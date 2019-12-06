package com.atguigu.gmall.index.aspectj;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.client.utils.StringUtils;
import com.atguigu.gmall.index.annotation.GmallCache;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
public class CaheAspect {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 1.方法的返回值是Object
     * 2.方法的参数ProceedingJoinPoint
     * 3.方法必须抛出Throwable异常
     * 4.通过joinPoint.proceed(args)执行原始方法
     */

    //注解标致他是  环绕 通知
    @Around("@annotation(com.atguigu.gmall.index.annotation.GmallCache)")
    //切点表达式，表示在哪里切入  ,此处切入含有 注解的那个方法
    public Object cacheAroudAdvice (ProceedingJoinPoint joinPoint) throws Throwable {




        // 环绕前业务逻辑

        // 获取注解
        MethodSignature signature = (MethodSignature)joinPoint.getSignature(); // 获取方法签名
        GmallCache annotation = signature.getMethod().getAnnotation(GmallCache.class); // 获取注解对象
        Class returnType = signature.getReturnType(); // 获取方法的返回值类型
        String prefix = annotation.prefix();  // 获取缓存的前缀
        String args = Arrays.asList(joinPoint.getArgs()).toString(); // 获取方法参数

        String key = prefix + ":" + args;
        // 查询缓存
        Object result = this.cacheHit(key, returnType);
        if (result != null) {
            return result;
        }

        // 分布式锁
        RLock lock = this.redissonClient.getLock("lock" + args);
        lock.lock();

        // 查询缓存
        result = this.cacheHit(key, returnType);
        // 如果缓存中有，直接返回，并且释放分布式锁
        if (result != null) {
            lock.unlock();
            return result;
        }



        // 连接业务逻辑

         result = joinPoint.proceed(joinPoint.getArgs());





        // 环绕后业务逻辑

        // 放入缓存，释放分布式锁
        long timeout = annotation.timeout();
        timeout = timeout + (long) (Math.random() * annotation.random());
        this.redisTemplate.opsForValue().set(key, JSON.toJSONString(result), timeout, TimeUnit.SECONDS);
        lock.unlock();

        return result;


    }

    private Object cacheHit(String key, Class returnType){

        String jsonString = this.redisTemplate.opsForValue().get(key);
        // 如果缓存中有，直接返回
        if (StringUtils.isNotBlank(jsonString)){
            return JSON.parseObject(jsonString, returnType);
        }
        return null;
    }

}
