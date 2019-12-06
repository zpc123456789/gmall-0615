package com.atguigu.gmall.order.interceptor;

import com.atguigu.core.bean.UserInfo;
import com.atguigu.core.utils.CookieUtils;
import com.atguigu.core.utils.JwtUtils;
import com.atguigu.gmall.order.config.JwtProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

//编写拦截器

//状态字段 ，可能被修改，所以是线程不安全的
// 线程是否安全取决于 是否 有状态字段

//业务字段 不会改变，所以是线程安全的


//ThreadLocal 线程局部变量   其底层为Map<k(key 为当前对象 线程hash值作为唯一标致的key,已经限定死了),v(存入属性值)>
//类是于作用域 参考request域

// 作用于域为整条线程生命周期
// 使用同一条线程，使用不同的变量



//底层源码
// public T get() {
//        Thread t = Thread.currentThread();
//        ThreadLocalMap map = getMap(t);
//        if (map != null) {
//            ThreadLocalMap.Entry e = map.getEntry(this);
//            if (e != null) {
//                @SuppressWarnings("unchecked")
//                T result = (T)e.value;
//                return result;
//            }
//        }
//        return setInitialValue();
//    }

// 代替不够优雅的request域传递 参数
@Slf4j
@Component
@EnableConfigurationProperties(JwtProperties.class)
public class LoginInterceptor extends HandlerInterceptorAdapter {

    //threadlocal中载荷信息 userId
     UserInfo userInfo = new UserInfo();
    @Autowired
    private JwtProperties jwtProperties;
    private static final ThreadLocal<UserInfo> THREAD_LOCAL = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 获取cookie信息（ GMALL_TOKEN)
        String token = CookieUtils.getCookieValue(request, jwtProperties.getCookieName());

        if (StringUtils.isEmpty(token)){
            //如果用户没有登陆，拦截或者重定向到登陆页面
            return  false;
        }

        try {
            // 解析 gamll_token

            Map<String, Object> userInfoMap = JwtUtils.getInfoFromToken(token, this.jwtProperties.getPublicKey());
            //获取token信息，并用公钥解密

            //获取了userKey 和 token 信息 如何传递，根据token信息可以判断 登录 状态

            //request.setAttribute("userId",userInfoMap.get("id").toString());//获取登录者的 id 唯一标致

            userInfo.setUserId(Long.valueOf(userInfoMap.get("id").toString()));

            } catch (Exception e) {

                log.error("解析token异常");

                e.printStackTrace();
            }

            THREAD_LOCAL.set(userInfo);

        return super.preHandle(request,response,handler);
    }

    //私有 就提供 可供公共访问的get方法
    //提供一个公共的get方法 以便后面可以获得UserInfo类的类容
    public static UserInfo get(){ return THREAD_LOCAL.get();
    }

    //记得释放资源
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 因为咱们使用的是tomcat线程池，请求结束不代表线程结束,请求结束之后，线程会放回线程池，以供下次再用
        THREAD_LOCAL.remove();
    }

}





































