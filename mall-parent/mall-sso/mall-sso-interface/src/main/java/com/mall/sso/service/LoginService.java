package com.mall.sso.service;

import com.mall.common.pojo.E3Result;

public interface LoginService {

    E3Result login(String name, String pass);

}
