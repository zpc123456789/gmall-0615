package com.atguigu.gmall.order.config;

import com.atguigu.core.utils.RsaUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.security.PublicKey;

@Slf4j
@Data
@ConfigurationProperties(prefix = "auth.jwt")
public class JwtProperties {

    private String publicKeyPath;
    //如何把公钥路径转换为公钥对象  检查对应的路径下是否存在公钥  不存在就生成  存在就读取

    private String cookieName;
    private PublicKey publicKey;


    @PostConstruct //@PostConstruct  构造之后执行
    public void init(){

        try {
            //.读取公钥
            publicKey = RsaUtils.getPublicKey(publicKeyPath);

        } catch (Exception e) {
            log.error("读取公钥失败！！！");
            e.printStackTrace();
        }
    }

}
