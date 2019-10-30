package com.atguigu.gmall.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
public class GmallCorsConfig {

    @Bean
    public CorsWebFilter corsWebFilter(){

        // 初始化CORS配置对象
        CorsConfiguration configuration = new CorsConfiguration();
        // 允许的域,不要写*，否则cookie就无法使用了
        configuration.addAllowedOrigin("http://localhost:1000");
        // 允许的头信息
        configuration.addAllowedHeader("*");
        // 允许的请求方式
        configuration.addAllowedMethod("*");
        // 是否允许携带Cookie信息
        configuration.setAllowCredentials(true);

        // 添加映射路径，我们拦截一切请求
        // cors的配置源信息
        UrlBasedCorsConfigurationSource configurationSource = new UrlBasedCorsConfigurationSource();
        configurationSource.registerCorsConfiguration("/**", configuration);
        return new CorsWebFilter(configurationSource);
    };
}















