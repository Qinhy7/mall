package com.mall.sso.service.impl;

import com.mall.common.pojo.E3Result;
import com.mall.common.redis.JedisClient;
import com.mall.common.utils.JsonUtils;
import com.mall.mapper.TbUserMapper;
import com.mall.pojo.TbUser;
import com.mall.pojo.TbUserExample;
import com.mall.sso.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.List;
import java.util.UUID;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private TbUserMapper userMapper;
    @Autowired
    private JedisClient jedisClient;

    @Value("${SESSION_PREFIX}")
    private String SESSION_PREFIX;
    @Value("${SESSION_EXPIRE}")
    private Integer SESSION_EXPIRE;

    @Override
    public E3Result login(String name, String pass) {
        // 校验用户名密码
        TbUserExample example = new TbUserExample();
        TbUserExample.Criteria criteria = example.createCriteria();
        criteria.andUsernameEqualTo(name);

        List<TbUser> tbUsers = userMapper.selectByExample(example);
        if(tbUsers == null || tbUsers.size() == 0){
            return E3Result.build(500,"用户名密码错误");
        }

        // 获取第一个
        TbUser user = tbUsers.get(0);
        if(!user.getPassword().equals(DigestUtils.md5DigestAsHex(pass.getBytes()))){
            return E3Result.build(501,"用户名密码错误");
        }

        // 生成token写入redis，设置过期时间
        String token = UUID.randomUUID().toString();
        user.setPassword(null);
        jedisClient.set(SESSION_PREFIX+token, JsonUtils.objectToJson(user));
        jedisClient.expire(SESSION_PREFIX+token, SESSION_EXPIRE);

        // 将token写回controller，方便写入cookie
        return E3Result.build(200,"登录成功",token);
    }
}
