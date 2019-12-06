package com.atguigu.gmall.auth;

import com.atguigu.core.utils.JwtUtils;
import com.atguigu.core.utils.RsaUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
public class JwtTest {
    private static final String pubKeyPath = "D:\\project-0615\\tmp\\rsa.pub";

    private static final String priKeyPath = "D:\\project-0615\\tmp\\rsa.pri";

    private PublicKey publicKey;

    private PrivateKey privateKey;

    @Test
    public void testRsa() throws Exception {
        RsaUtils.generateKey(pubKeyPath, priKeyPath, "ssd23243sFED%&^*&2132");
    }

    @Before
    public void testGetRsa() throws Exception {
        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
    }

    @Test
    public void testGenerateToken() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("id", "11");
        map.put("username", "liuyan");
        // 生成token
        String token = JwtUtils.generateToken(map, privateKey, 1);
        System.out.println("token = " + token);
    }

    @Test
    public void testParseToken() throws Exception {
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJpZCI6IjExIiwidXNlcm5hbWUiOiJsaXV5YW4iLCJleHAiOjE1NzQxMzg5Nzl9.RkIxYP63CzKchyEIDbgkvlbklT2Vcrw5ouLjGu9ZShaCjY4p_HDeQ53BtBLFg5wXYZpPP0sY-338i_8VJYAjPmbE_QOz9x6xxNx_Ml57RKwT1gHXag0MXll4g7lte4TXVmzM70OxkTiDj1kNb0or3buxjsmmvso9YzRwiLvavGPK2arcjP9mnC090rkgMQkJxuEvjkfJajJH-HEqhOpDadAYMQSacNW_0BbqjWOzAX2KBb-u-hXW1l1aC6qGhdltPBENHTu5d-RsI2QABzgExskoFLdaaY-4VXgGfYUMKzI6Vupc1JUcxpOaEn1sRmjG2gkXGs4LrEmPtLXNlhRXpw";

        // 解析token
        Map<String, Object> map = JwtUtils.getInfoFromToken(token, publicKey);
        System.out.println("id: " + map.get("id"));
        System.out.println("userName: " + map.get("username"));
    }
}