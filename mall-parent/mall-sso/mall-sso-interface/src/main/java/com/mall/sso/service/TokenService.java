package com.mall.sso.service;

import com.mall.common.pojo.E3Result;

public interface TokenService {

    /**
     * 获取用户信息
     * @param token
     * @return
     */
    E3Result getUserForToken(String token);

}
