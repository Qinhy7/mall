package com.mall.sso.controller;

import com.mall.common.pojo.E3Result;
import com.mall.common.utils.JsonUtils;
import com.mall.sso.service.TokenService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TokenController {

    @Autowired
    private TokenService tokenService;

    /**
     * 使用token获取用户信息
     * @param token
     * @return
     */
    @RequestMapping(value = "/user/token/{token}",produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getUser(@PathVariable String token, String callback){
        E3Result result = tokenService.getUserForToken(token);

        // 注意js的跨域
        /*
        1、接收callback参数，取回调的js的方法名。
        2、业务逻辑处理。
        3、响应结果，拼接一个js语句。
         */

        if(StringUtils.isNoneBlank(callback)){
            String s = callback + "(" + JsonUtils.objectToJson(result) + ");";
            return s;
        }

        return JsonUtils.objectToJson(result);
    }

}
