package com.atguigu.gmall.order.controller;

import com.atguigu.core.bean.Resp;
import com.atguigu.core.bean.UserInfo;
import com.atguigu.gmall.oms.entity.OrderEntity;
import com.atguigu.gmall.oms.vo.OrderSubmitVO;
import com.atguigu.gmall.order.config.AlipayTemplate;
import com.atguigu.gmall.order.interceptor.LoginInterceptor;
import com.atguigu.gmall.order.service.OrderService;
import com.atguigu.gmall.order.vo.OrderConfirmVO;
import com.atguigu.gmall.order.vo.PayAsyncVo;
import com.atguigu.gmall.order.vo.PayVo;
import com.atguigu.gmall.order.vo.SeckillVO;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RCountDownLatch;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("order")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private AlipayTemplate alipayTemplate;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private AmqpTemplate amqpTemplate;

    @GetMapping("confirm")
    public Resp<OrderConfirmVO> confirm(){
       OrderConfirmVO orderConfirmVO = this.orderService.confirm();
       return Resp.ok(orderConfirmVO);
    }

    @PostMapping("submit")
    public Resp<Object> submit(@RequestBody OrderSubmitVO orderSubmitVO){
        String form = null;
        try {

            //提交订单
            OrderEntity orderEntity = this.orderService.submit(orderSubmitVO);

            //支付
            PayVo payVo = new PayVo();
            payVo.setBody("谷粒商城支付系统");
            payVo.setOut_trade_no("支付平台");
            payVo.setSubject(orderEntity.getTotalAmount().toString());
            payVo.setTotal_amount(orderEntity.getOrderSn());//获取订单编号防重

            form = this.alipayTemplate.pay(payVo);
            System.out.println(form);
            return Resp.ok(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Resp.ok(form);
    }

    @GetMapping("pay/success")
    public Resp<Object> paySuccess(PayAsyncVo payAsyncVo){

        System.out.println("======支付成功======");

        //订单状态的修改和库存的扣除
        this.orderService.paySucess(payAsyncVo.getOut_trade_no());
        return Resp.ok(null);

    }

    @RequestMapping("seckill/skuId")
    public Resp<Object> seckill(@PathVariable("skuId")Long skuId) throws InterruptedException {

        //查询秒杀库存
        String stockJson = this.redisTemplate.opsForValue().get("seckill:stock:" + skuId);
        if (StringUtils.isEmpty(stockJson)){
            return Resp.ok("秒杀已结束，或者该秒杀不存在！");
        }

        Integer stock = Integer.valueOf(stockJson);//类型转换

        RSemaphore semaphore = this.redissonClient.getSemaphore("seckill:stock:" + skuId);
        semaphore.trySetPermits(stock);

        semaphore.acquire(1);//每次只能1个

        UserInfo userInfo = LoginInterceptor.get();

        RCountDownLatch countDownLatch = this.redissonClient.getCountDownLatch("seckill:stock:" + userInfo.getUserId());
        countDownLatch.trySetCount(1);

        SeckillVO seckillVO = new SeckillVO();
        seckillVO.setSkuId(skuId);
        seckillVO.setUserId(userInfo.getUserId());
        seckillVO.setCount(1);

        //把秒杀商品的信息方法 放入到消息队列中，异步同步数据库中的信息
        //发送信息修改数据库中商品信息
        //创建订单信息
        //参照之前 修改商品信息
        this.amqpTemplate.convertAndSend("SECKILL-EXCHANGE","seckill.create",seckillVO);


        //秒杀成功，创建订单信息成功后， 就可以查看杀商品的信息，不用等数据库跟新，每个id对应的 countDownLatch 数量减一
        countDownLatch.countDown();


        //更新redis中秒杀商品的数量
        this.redisTemplate.opsForValue().set("seckill:stock:"+skuId,String.valueOf(--stock));


        return Resp.ok(null);
    }

    //查看已秒杀的商品信息
    @GetMapping("searchseckill/userId")
    public Resp<OrderEntity> queryOrder(@RequestParam("userId")Long userId) throws InterruptedException {
        UserInfo userInfo = LoginInterceptor.get();

        OrderEntity orderEntity = this.orderService.queryOrder(userId);

        RCountDownLatch countDownLatch = this.redissonClient.getCountDownLatch("seckill:stock:" + userInfo.getUserId());

        countDownLatch.await();//等到countDownLatch.countDown();执行，也就是秒杀成功之后才可以查看秒杀商品信息

        return Resp.ok(orderEntity);

    }

}





































