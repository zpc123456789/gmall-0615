package com.atguigu.gmall.wms.dao;

import com.atguigu.gmall.wms.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品库存
 * 
 * @author lixianfeng
 * @email lxf@atguigu.com
 * @date 2019-10-29 17:39:28
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {
    //验库存方法
    public List<WareSkuEntity> checkStore(@Param("skuId") Long skuId,@Param("count") Integer count);
    //锁库存方法
    public int lock(@Param("id") Long id,@Param("count") Integer count);
    //解库存方法
    public int unlock(@Param("id") Long id,@Param("count") Integer count);

    void minus(Long skuWareId, Integer count);
}






















