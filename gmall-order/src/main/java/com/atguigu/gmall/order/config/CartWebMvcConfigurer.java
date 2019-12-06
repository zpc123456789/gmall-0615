package com.atguigu.gmall.order.config;

import com.atguigu.gmall.order.interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


//配置拦截器

//为什么配置拦截器
//对是否登录进行判断

//购物车系统根据用户的登录状态，购物车的增删改处理方式不同，因此需要添加登录校验，我们通过JWT鉴权即可实现。
// 而登录状态如果在每个方法中进行校验，会造成代码的冗余，不利于维护。所以这里使用拦截器统一处理。

// 因为很多接口都需要进行登录，我们直接编写SpringMVC拦截器，进行统一登录校验。
// 同时，我们还要把解析得到的用户信息保存起来，以便后续的接口可以使用。



//ThreadLocal 线程局部变量
// 使用同一条线程，使用不同的变量
// 即 同一根枪杆，不同的枪头  墙头库即为线程局部便量
// 每个变量就是每个枪头，不同的枪头适用不同的场景


@Configuration
public class CartWebMvcConfigurer implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry){

        registry.addInterceptor(loginInterceptor).addPathPatterns("/**");

    }

}









































