package com.mall.sso.service.impl;

import com.mall.common.pojo.E3Result;
import com.mall.common.redis.JedisClient;
import com.mall.common.utils.JsonUtils;
import com.mall.pojo.TbUser;
import com.mall.sso.service.TokenService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TokenServiceImpl implements TokenService {

    @Autowired
    private JedisClient jedisClient;
    @Value("${SESSION_PREFIX}")
    private String SESSION_PREFIX;
    @Value("${SESSION_EXPIRE}")
    private Integer SESSION_EXPIRE;

    @Override
    public E3Result getUserForToken(String token) {
        // 从redis中获取用户信息，判断是否存在、过期
        String userJson = jedisClient.get(SESSION_PREFIX + token);
        if(StringUtils.isBlank(userJson)){
            return E3Result.build(500,"用户登录过期");
        }
        // 重置过期时间
        jedisClient.expire(SESSION_PREFIX+token, SESSION_EXPIRE);

        return E3Result.build(200,"登录成功", JsonUtils.jsonToPojo(userJson, TbUser.class));
    }
}
