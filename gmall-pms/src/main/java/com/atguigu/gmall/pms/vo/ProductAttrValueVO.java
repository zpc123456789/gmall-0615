package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.ProductAttrValueEntity;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.util.List;

@Data
public class ProductAttrValueVO extends ProductAttrValueEntity {//相当于包装了一边

    public void setValueSelected(List<String> valueSelected){

        this.setAttrValue(StringUtils.join(valueSelected, ",")); //把每个单独的数据用 " ,  " 隔开，组成集合,而且不用判空
    }
}







































