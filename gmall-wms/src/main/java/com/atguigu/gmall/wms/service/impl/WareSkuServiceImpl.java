package com.atguigu.gmall.wms.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;
import com.atguigu.gmall.wms.dao.WareSkuDao;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import com.atguigu.gmall.wms.service.WareSkuService;
import com.atguigu.gmall.wms.vo.SkuLockVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Autowired
    private RedissonClient redissonClient;   //引入分布式锁
    @Autowired
    private WareSkuDao wareSkuDao;
    @Autowired
    private AmqpTemplate amqpTemplate;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                new QueryWrapper<WareSkuEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public String checkAndLock(List<SkuLockVO> skuLockVOS) {
        //遍历   锁库存的情况
        skuLockVOS.forEach(skuLockVO -> {
            lockSku(skuLockVO);
        });
        //查看有没有失败的记录
        //有失败的记录，则回滚成功的记录
        List<SkuLockVO> success = skuLockVOS.stream().filter(skuLockVO -> skuLockVO.getLock()).collect(Collectors.toList());
        List<SkuLockVO> error = skuLockVOS.stream().filter(skuLockVO -> !skuLockVO.getLock()).collect(Collectors.toList());

        //当有失败记录的时候，回滚所有 成功的锁库存  回滚的实把锁库存的量 再更新回去
        if (!CollectionUtils.isEmpty(error)){
            success.forEach(skuLockVO -> {
                wareSkuDao.unlock(skuLockVO.getSkuWareId(),skuLockVO.getCount());
            });
            return "锁定失败" + error.stream().map(skuLockVO -> skuLockVO.getSkuId()).collect(Collectors.toList()).toString();
        }

        //保存锁定库存的信息到redis中
        String orderToken = skuLockVOS.get(0).getOrderToken();

        this.redisTemplate.opsForValue().set("order:sotck:"+orderToken, JSON.toJSONString(skuLockVOS));//存入到redis中

        //发送延时消息，20分钟解锁库存
        this.amqpTemplate.convertAndSend("WMS-EXCHANGE","wms.unlock",orderToken);


        return null;
    }
    /*// 定时任务
    public static void main(String[] args) {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(3);
        scheduledExecutorService.schedule(()->{
            System.out.println("===========" + LocalDateTime.now());
        },5, TimeUnit.SECONDS);
    }
*/

    private void lockSku(SkuLockVO skuLockVO){
        RLock lock = this.redissonClient.getLock("sku:lock" + skuLockVO.getSkuId());

        //验库存
        List<WareSkuEntity> wareSkuEntities = this.wareSkuDao.checkStore(skuLockVO.getSkuId(), skuLockVO.getCount());

        skuLockVO.setLock(false);
        if (!CollectionUtils.isEmpty(wareSkuEntities)){
            //锁库存
            //锁库存的实质时  把要出的库存数量 加到 stock_locked 字段 如果库存不够，则无法方到 stock_locked 字段中
            int count = this.wareSkuDao.lock(wareSkuEntities.get(0).getId(), skuLockVO.getCount());
            if (count==1){
                skuLockVO.setLock(true);
                skuLockVO.setSkuWareId(wareSkuEntities.get(0).getId());
            }
        }//没有仓库的库存数满足要求，锁定失败

        lock.lock();
    }

}































