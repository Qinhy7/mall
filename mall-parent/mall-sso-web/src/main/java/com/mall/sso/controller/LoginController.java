package com.mall.sso.controller;

import com.mall.common.pojo.E3Result;
import com.mall.common.utils.CookieUtils;
import com.mall.sso.service.LoginService;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class LoginController {

    @Autowired
    private LoginService loginService;

    /**
     * 用户登录校验
     */
    @RequestMapping("/user/login")
    @ResponseBody
    public E3Result login(String username, String password, HttpServletRequest request,
                          HttpServletResponse response){
        E3Result result = loginService.login(username, password);

        // 判断登录成功状态，获取token返回cookie
        if (result.getStatus() == 200){
            Object data = result.getData();
            CookieUtils.setCookie(request,response,"token",data.toString());
        }

        return result;
    }

}
