package com.mall.cart.intercepter;

import com.mall.common.pojo.E3Result;
import com.mall.common.utils.CookieUtils;
import com.mall.pojo.TbUser;
import com.mall.sso.service.TokenService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class LoginIntercepter implements HandlerInterceptor {

    @Autowired
    private TokenService tokenService;

    @Value("${CART_COOKIE_NAME}")
    private String CART_COOKIE_NAME;

    // 在执行handler方法之前
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        // 判断用户是否登录

        // 从cookie中取出token，判断token
        String cookieValue = CookieUtils.getCookieValue(httpServletRequest, "token");
        // token不存在，表示没有登录，放行
        if(StringUtils.isBlank(cookieValue)){
            return true;
        }
        // token存在，调用sso服务，返回token是否过期
        E3Result e3Result = tokenService.getUserForToken(cookieValue);
        // 过期，也是没有登录，放行
        if (e3Result.getStatus() != 200){
            return true;
        }
        // 没有过期，保存redis中获取的sessionId到request中，然后放行
        TbUser user  = (TbUser) e3Result.getData();
        httpServletRequest.setAttribute("user",user);
        return false;
    }

    // 执行handler之后，返回逻辑视图之前
    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    // 返回逻辑视图之后
    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
