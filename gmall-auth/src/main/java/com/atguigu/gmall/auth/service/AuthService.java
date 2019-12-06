package com.atguigu.gmall.auth.service;

import com.atguigu.core.bean.Resp;
import com.atguigu.core.exception.GmallException;
import com.atguigu.core.utils.JwtUtils;
import com.atguigu.gmall.auth.config.JwtProperties;
import com.atguigu.gmall.auth.feign.GmallUmsClient;
import com.atguigu.gmall.ums.entity.MemberEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@EnableConfigurationProperties({JwtProperties.class})
public class AuthService {

    @Autowired
    private GmallUmsClient gmallUmsClient;

    @Autowired
    private JwtProperties jwtProperties;

    public String  accredit(String userName, String password) {

        try {
            // 1 远程调用用户中心的数据接口，调查用户信息
            Resp<MemberEntity> memberEntityResp = this.gmallUmsClient.queryUser(userName, password);
            MemberEntity memberEntity = memberEntityResp.getData();

            // 2 判断用户是否存在，不存在直接返回
            if (memberEntity==null){

                return null;
            }

            // 3 存在 则生成jwt
            Map<String,Object> map = new HashMap<>();

            map.put("id",memberEntity.getId());
            map.put("usermane",memberEntity.getUsername());


            //通过私钥生成jwt
            return JwtUtils.generateToken(map,this.jwtProperties.getPrivateKey(),this.jwtProperties.getExpire());


        } catch (Exception e) {
            e.printStackTrace();
            throw new GmallException("jwt认证失败!");

            // 为什么抛出异常，这本来就是登录，网页显示的部分，try 处理之后，
            // 网页就得不到任何信息，锁一需要抛出异常，或者自定义异常，如上49行，本质上还是抛出异常
        }

        // 4 把生成的jwt放入cookie中 需要 ruquest 和 reponse 作用域，所以方controller中完成

    }
}
