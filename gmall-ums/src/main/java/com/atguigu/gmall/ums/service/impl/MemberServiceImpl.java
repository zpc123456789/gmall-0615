package com.atguigu.gmall.ums.service.impl;

import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;
import com.atguigu.gmall.ums.dao.MemberDao;
import com.atguigu.gmall.ums.entity.MemberEntity;
import com.atguigu.gmall.ums.service.MemberService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageVo(page);
    }


    @Override
    public Boolean checkData(String data, Integer type) {

        QueryWrapper<MemberEntity> wrapper = new QueryWrapper<>();

        switch (type){
            case 1: wrapper.eq("username",data);break;
            case 2: wrapper.eq("mobile",data);break;
            case 3: wrapper.eq("email",data);break;
            default: return false;
        }

        return this.count(wrapper) == 0;
    }

    @Override
    public void register(MemberEntity memberEntity,String code) {

        // 1 校验验证码 todo


        // 2 生成盐
        String salt = StringUtils.substring(UUID.randomUUID().toString(), 0, 6);
        memberEntity.setSalt(salt);

        // 3 加盐加密
        // md2Hex() 生成 64 位 比较安全
        memberEntity.setPassword(DigestUtils.md5Hex(memberEntity.getPassword()+salt));

        // 4 注册功能
        memberEntity.setLevelId(1l);
        memberEntity.setStatus(1);
        memberEntity.setCreateTime(new Date());
        memberEntity.setIntegration(0);
        memberEntity.setGrowth(0);
        this.save(memberEntity);

        // 5 删除redis中的验证码 todo


    }

    @Override
    public MemberEntity queryUser(String userName, String passowrd) {

        //先查询用户名
        MemberEntity memberEntity = this.getOne(new QueryWrapper<MemberEntity>().eq("username", userName));

        //判断用户名是否存在，不存在则抛出异常
        if (memberEntity == null){
            throw new IllegalArgumentException("用户名或者密码不合法!");
        }

        //再根据用户名来查询密码，并加盐加密
        passowrd = DigestUtils.md5Hex(passowrd + memberEntity.getSalt());
        System.out.println(passowrd);
        System.out.println(memberEntity.getSalt());

        //加密后的密码与数据库中的密码进行比对
        if (!StringUtils.equals(passowrd,memberEntity.getPassword())){

            throw new IllegalArgumentException("用户名或者密码不合法!");
        }

        return memberEntity;
    }

}



























