package com.atguigu.gmallcart.service;

import com.alibaba.fastjson.JSON;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.cart.vo.CartItemVO;
import com.atguigu.gmall.pms.entity.SkuInfoEntity;
import com.atguigu.gmall.pms.entity.SkuSaleAttrValueEntity;
import com.atguigu.gmall.sms.vo.ItemSaleVO;
import com.atguigu.gmallcart.feign.GmallPmsClient;
import com.atguigu.gmallcart.feign.GmallSmsClient;
import com.atguigu.gmallcart.interceptor.LoginInterceptor;
import com.atguigu.gmallcart.vo.Cart;
import com.atguigu.gmallcart.vo.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService {

    private static final String CURRENT_PRICE_PRFIX ="cart:price:" ;

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private GmallPmsClient gmallPmsClient;
    @Autowired
    private GmallSmsClient gmallSmsClient;

    private static final String KEY_PREFIX = "cart:key";


    public void addCart(Cart cart) {

        //共同部分提取为一个私有的方法，公共使用  快捷键 ctrl + alt + m
        String key = getKey();

        //判断购物车中是否有该记录
        //hash结构 Map<String,Map<String,String>>
        BoundHashOperations<String, Object, Object> hashOps = this.redisTemplate.boundHashOps(key);  //获取redis操作对象

        //取出用户新增购物车商品的数量
        Integer count = cart.getCount();

        String skuId = cart.getSkuId().toString();

        if (hashOps.hasKey(skuId)){
            //有更新数量(购买多个)
            String carJson = hashOps.get(cart.getSkuId().toString()).toString();//redis里存储的都是 json 字符串型的数据

                                //要反序列化的数据   //反序列化的类型
            cart = JSON.parseObject(carJson, Cart.class); //json反序列化

            cart.setCount(cart.getCount()+count);//更新数量


        } else {
            //没有就新增记录

            Resp<SkuInfoEntity> skuInfoEntityResp = this.gmallPmsClient.querySkuById(cart.getSkuId());
            SkuInfoEntity skuInfoEntity = skuInfoEntityResp.getData();
            
            cart.setTitle(skuInfoEntity.getSkuTitle());
            cart.setCheck(true);
            cart.setPrice(skuInfoEntity.getPrice());
            // 查询销售属性
            Resp<List<SkuSaleAttrValueEntity>> listResp = this.gmallPmsClient.querySaleAttrBySkuId(cart.getSkuId());
            List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = listResp.getData();
            cart.setSkuAttrValue(skuSaleAttrValueEntities);
            cart.setDefaultImage(skuInfoEntity.getSkuDefaultImg());
            // 查询营销信息
            Resp<List<ItemSaleVO>> listResp1 = this.gmallSmsClient.queryItemSaleVOs(cart.getSkuId());
            cart.setSales(listResp1.getData());


            //从商品详情页里获取当前价格，以便实时更新购物车内商品价格
            this.redisTemplate.opsForValue().set(CURRENT_PRICE_PRFIX + skuId, skuInfoEntity.getPrice().toString());


        }

        //跟新后放入redis中
        hashOps.put(skuId,JSON.toJSONString(cart));

    }

    public List<Cart> queryCarts() {


        //查询未登录状态的购物车
        UserInfo userInfo = LoginInterceptor.get();
        String key1 = KEY_PREFIX + userInfo.getUserKey();

        // 获取redis操作对象
        BoundHashOperations<String, Object, Object> userKeyOps = this.redisTemplate.boundHashOps(key1);
        List<Object> cartJsonList = userKeyOps.values();

        // 建议 获得任何的结果 都要 判断是否为空

        List<Cart> userKeyCarts = null;
        if (!CollectionUtils.isEmpty(cartJsonList)) {
           userKeyCarts = cartJsonList.stream().map(cartJson -> {

               // 把购物车里的价格换成当前价格
                 Cart cart = JSON.parseObject(cartJson.toString(), Cart.class);
                 cart.setCurrentPrice(new BigDecimal(this.redisTemplate.opsForValue().get(CURRENT_PRICE_PRFIX+cart.getSkuId())));

                 return cart;
           }).collect(Collectors.toList());
        }

        //判断登录状态
        if (userInfo.getUserId() == null){

            //未登录直接返回
            return userKeyCarts;
        }


        //登录，查询登录状态的购物车
        String key2 = KEY_PREFIX + userInfo.getUserId();
        BoundHashOperations<String, Object, Object> userIdOps = this.redisTemplate.boundHashOps(key2);


        //判断未登录的购物车是否为空
        if (!CollectionUtils.isEmpty(userKeyCarts)){
            //不为空，合并购物车
            userKeyCarts.forEach(cart -> {

                if (userIdOps.hasKey(cart.getSkuId().toString())){
                    //有更新数量(购买多个)
                    String carJson = userIdOps.get(cart.getSkuId().toString()).toString();//redis里存储的都是 json 字符串型的数据

                    //要反序列化的数据   //反序列化的类型
                    Cart idCart = JSON.parseObject(carJson, Cart.class); //json反序列化

                    idCart.setCount(idCart.getCount()+cart.getCount());//更新数量
                    userIdOps.put(cart.getSkuId().toString(),JSON.toJSONString(idCart));

                } else {
                    //没有就新增记录
                    userIdOps.put(cart.getSkuId().toString(),JSON.toJSONString(cart));

                }

            });
        }

        this.redisTemplate.delete(key1);
        //为空，直接返回登录状态的购物车


        //查询返回
        List<Object> userIdCartJsonList = userIdOps.values();//登录状态下购物车的数据

        if (CollectionUtils.isEmpty(userIdCartJsonList)){
            return null;
        }

        return userIdCartJsonList.stream().map(userIdCartJson -> {
            Cart cart = JSON.parseObject(userIdCartJson.toString(),Cart.class);
            cart.setCurrentPrice(new BigDecimal(this.redisTemplate.opsForValue().get(CURRENT_PRICE_PRFIX+cart.getSkuId())));
            return cart;
        }).collect(Collectors.toList());
    }

    public void updateCart(Cart cart) {

        String key = getKey();
        Integer count = cart.getCount();//获取购物车商品原有数量

        BoundHashOperations<String, Object, Object> hashOps = this.redisTemplate.boundHashOps(key);

        if (hashOps.hasKey(cart.getSkuId().toString())){

            //获取购物车中的更新数量的购物记录
            String cartJson = hashOps.get(cart.getSkuId().toString()).toString();

            cart= JSON.parseObject(cartJson,Cart.class);//反序列化

            cart.setCount(count);//更改购物车商品数量

            hashOps.put(cart.getSkuId().toString(), JSON.toJSONString(cart));//序列化后存入redis中
        }

    }

    public void deleteCart(Long skuId) {

        String key = getKey();

        BoundHashOperations<String, Object, Object> hashOps = this.redisTemplate.boundHashOps(key);

        if (hashOps.hasKey(skuId.toString())){
            hashOps.delete(skuId.toString());
        }
    }

    public void checkCart(List<Cart> carts) {

        String key = getKey();
        BoundHashOperations<String, Object, Object> hashOps = this.redisTemplate.boundHashOps(key);

        carts.forEach(cart -> {
            Boolean check = cart.getCheck();
            if (hashOps.hasKey(cart.getSkuId().toString())){
                //获取购物车中的更新数量的购物记录

                String cartJson = hashOps.get(cart.getSkuId().toString()).toString();

                cart= JSON.parseObject(cartJson,Cart.class);//反序列化

                cart.setCheck(check);

                hashOps.put(cart.getSkuId().toString(), JSON.toJSONString(cart));//序列化后存入redis中


            }
        });


    }


    private String getKey(){
        String key =KEY_PREFIX;

        //判断登录状态
        UserInfo userInfo = LoginInterceptor.get();

        if (userInfo.getUserId() != null){
            key += userInfo.getUserId();
        } else {
            key += userInfo.getUserKey();
        }
        return key;
    }


    public List<CartItemVO> queryCartItemVO(Long userId) {
        // 登录，查询登录状态的购物车
        String key = KEY_PREFIX + userId;
        BoundHashOperations<String, Object, Object> userIdOps = this.redisTemplate.boundHashOps(key);
        // 查询返回
        List<Object> userIdCartJsonList = userIdOps.values();
        if (CollectionUtils.isEmpty(userIdCartJsonList)){
            return null;
        }
        // 获取所有的购物车记录
        return userIdCartJsonList.stream().map(userIdCartJson -> {
            Cart cart = JSON.parseObject(userIdCartJson.toString(), Cart.class);
            cart.setCurrentPrice(new BigDecimal(this.redisTemplate.opsForValue().get(CURRENT_PRICE_PRFIX + cart.getSkuId())));
            return cart;
        }).filter(cart -> cart.getCheck()).map(cart -> {
            CartItemVO cartItemVO = new CartItemVO();
            cartItemVO.setSkuId(cart.getSkuId());
            cartItemVO.setCount(cart.getCount());
            return cartItemVO;
        }).collect(Collectors.toList());
    }
}













































