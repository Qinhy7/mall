package com.mall.sso.service;

import com.mall.common.pojo.E3Result;
import com.mall.pojo.TbUser;

public interface RegisterService {

    /**
     * 校验数据
     * @param value 校验的数据
     * @param type 1 校验用户名，2 校验手机号
     * @return
     */
    E3Result check(String value, int type);

    /**
     * 注册用户
     * @param user
     * @return
     */
    E3Result register(TbUser user);

}
