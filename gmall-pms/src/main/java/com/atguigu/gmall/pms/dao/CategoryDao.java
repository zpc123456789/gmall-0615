package com.atguigu.gmall.pms.dao;

import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.vo.CategoryVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 商品三级分类
 * 
 * @author lixianfeng
 * @email lxf@atguigu.com
 * @date 2019-10-28 16:25:30
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {


    List<CategoryVO> queryCategoryWithSub(Long pid);
}
