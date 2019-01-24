package com.mall.sso.service.impl;

import com.mall.common.pojo.E3Result;
import com.mall.mapper.TbUserMapper;
import com.mall.pojo.TbUser;
import com.mall.pojo.TbUserExample;
import com.mall.sso.service.RegisterService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class RegisterServiceImpl implements RegisterService {

    @Autowired
    private TbUserMapper userMapper;

    @Override
    public E3Result check(String value, int type) {
        // type 1 校验用户名， 2 校验手机号
        if(type == 1){
            TbUserExample example = new TbUserExample();
            TbUserExample.Criteria criteria = example.createCriteria();
            criteria.andUsernameEqualTo(value);
            List<TbUser> users = userMapper.selectByExample(example);
            if(users != null && users.size() > 0){
                return E3Result.build(400,"用户名重复",false);
            }
        }else if(type == 2){
            TbUserExample example1 = new TbUserExample();
            TbUserExample.Criteria criteria1 = example1.createCriteria();
            criteria1.andPhoneEqualTo(value);
            List<TbUser> users1 = userMapper.selectByExample(example1);
            if(users1 != null && users1.size() > 0){
                return E3Result.build(400,"手机号重复",false);
            }
        }


        return E3Result.ok(true);
    }

    @Override
    public E3Result register(TbUser user) {
        // 补全user属性
        user.setCreated(new Date());
        user.setUpdated(new Date());

        String password = user.getPassword();
        String s = DigestUtils.md5Hex(password.getBytes());
        user.setPassword(s);

        userMapper.insert(user);

        return E3Result.build(200,"注册成功","");
    }
}
