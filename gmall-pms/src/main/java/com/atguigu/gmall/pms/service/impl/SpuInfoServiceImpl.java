package com.atguigu.gmall.pms.service.impl;

import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;
import com.atguigu.gmall.pms.dao.SpuInfoDao;
import com.atguigu.gmall.pms.dao.SpuInfoDescDao;
import com.atguigu.gmall.pms.entity.SpuInfoDescEntity;
import com.atguigu.gmall.pms.entity.SpuInfoEntity;
import com.atguigu.gmall.pms.service.SpuInfoService;
import com.atguigu.gmall.pms.vo.SpuInfoVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {
    @Autowired
    private SpuInfoDescDao descDao;

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public PageVo querySpuInfoByKeyPage(Long catId, QueryCondition condition) {



        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();

        //判断catId是否为0
        if (catId != 0){
            wrapper.eq("catalog_id",catId);
        }


        //判断key是否为空
        String key = condition.getKey();

        if (StringUtils.isNotBlank(key)){

            wrapper.and(t-> t.eq("id",key).or().like("spu_name",key));

        }



        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(condition),
                wrapper

        );

        return new PageVo(page);
    }


    @Override
    public void bigSave(SpuInfoVO spuInfoVO) {


// 1
        //新增spu相关的3张表
        

        //新增spuInfo
        spuInfoVO.setCreateTime(new Date());
        spuInfoVO.setUodateTime(spuInfoVO.getCreateTime());
        this.save(spuInfoVO);
        Long spuId = spuInfoVO.getId();

        //新增spuInfoDesc
        List<String> spuImages = spuInfoVO.getSpuImages();
        String desc = StringUtils.join(spuImages, ",");
        SpuInfoDescEntity descEntity = new SpuInfoDescEntity();
        descEntity.setSpuId(spuId);
        descEntity.setDecript(desc);
        this.descDao.insert(descEntity);
        //新增基本属性productAttrValue





// 2
        //新增sku相关的3张表
        //新增skuInfo
        //新增sku的图片
        //新增销售属性





// 3
        //新增营销相关的3张表
        //新增积分：skuBounds
        //新增打折信息：skuLadder
        //新增满减信息



    }

}