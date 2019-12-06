package com.atguigu.gmall.item.vo;

import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.vo.GroupVO;
import com.atguigu.gmall.sms.vo.ItemSaleVO;
import lombok.Data;

import java.util.List;

@Data
public class ItemVO extends SkuInfoEntity {

    //标题
    private CategoryEntity Category;
    private BrandEntity brand;
    private SpuInfoEntity spuInfo;
    private BrandEntity brandEntity;



    private List<String> pics;  //sku的图片列表
    private List<ItemSaleVO> sales; // 营销信息
    private Boolean store; // 是否有货
    private List<SkuSaleAttrValueEntity> skuSales; //spu下所有的sku的销售信息
    private List<GroupVO> groups; //组及组下的规格属性及值
    private SpuInfoDescEntity desc; //描述信息


}






























