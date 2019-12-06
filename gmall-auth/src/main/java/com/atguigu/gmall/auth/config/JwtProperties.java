package com.atguigu.gmall.auth.config;

import com.atguigu.core.utils.RsaUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.io.File;
import java.security.PrivateKey;
import java.security.PublicKey;
@Slf4j
@Data
@ConfigurationProperties(prefix = "auth.jwt")
public class JwtProperties {
    /*
    publicKeyPath: D:\\project-0615\\tmp\\rsa.pub
    privateKeyPath: D:\\project-0615\\tmp\\rsa.pri
    expire: 180 #单位 分钟
    cookieName: GMALL_TOKEN
    */
    //使用spring Bean 生命周期注解  @PostConstruct // 构造之后执行  @PreDestroy // 销毁之前
    // spring   四类注解 1. 初始化 controller service 等  2. 依赖注入 Autowire等  3.作用域  scope 4. 生命周期

    private String publicKeyPath;
    //如何把公钥路径转换为公钥对象  检查对应的路径下是否存在公钥  不存在就生成  存在就读取

    private String privateKeyPath;
    //如何把私钥路径转换为私钥对象  检查对应的路径下是否存在私钥   不存在就生成  存在就读取

    private Integer expire;

    private String cookieName;

    private PublicKey publicKey;

    private PrivateKey privateKey;

    private String secret;

    @PostConstruct //@PostConstruct  构造之后执行
    public void init(){

        try {
            //1.初始化公钥私钥文件
            File publicFile = new File(publicKeyPath);
            File privateFile = new File(privateKeyPath);


            //2.检查文件对象是否为空为
            if (!publicFile.exists() || !privateFile.exists()){

                //为空就生成公钥私钥文件，后面为盐
                RsaUtils.generateKey(publicKeyPath,privateKeyPath,secret);
            }

            //3.读取密钥
            publicKey = RsaUtils.getPublicKey(publicKeyPath);
            privateKey = RsaUtils.getPrivateKey(privateKeyPath);

        } catch (Exception e) {
            log.error("初始化公钥和私钥失败！！！");
            e.printStackTrace();
        }
    }

}
