package com.atguigu.gmall.pms;

import com.atguigu.gmall.pms.dao.BrandDao;
import com.atguigu.gmall.pms.entity.BrandEntity;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GmallPmsApplicationTests {

    @Autowired
    private BrandDao brandDao;

    @Test
    void contextLoads() {
    }

    @Test
    public void test(){

        BrandEntity brandEntity = new BrandEntity();
        brandEntity.setName("上大计算机专业2");
        brandEntity.setDescript("上大真好2");
        brandEntity.setShowStatus(2);

        this.brandDao.insert(brandEntity);

    }

    @Test
    public void test2(){
        IPage<BrandEntity> page = this.brandDao.selectPage(new Page<BrandEntity>(2, 2), null);

        System.out.println(page.getRecords());
        System.out.println(page.getTotal());
        System.out.println(page.getPages());



    }

}























