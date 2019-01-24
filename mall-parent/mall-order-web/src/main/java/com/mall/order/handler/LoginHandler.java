package com.mall.order.handler;

import com.mall.cart.service.CartService;
import com.mall.common.pojo.E3Result;
import com.mall.common.utils.CookieUtils;
import com.mall.common.utils.JsonUtils;
import com.mall.pojo.TbItem;
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

/**
 * 访问任何与order相关的，都需要经过这个拦截器，判断用户是否登录
 */
@Component
public class LoginHandler implements HandlerInterceptor {

    @Autowired
    private TokenService tokenService;
    @Autowired
    private CartService cartService;

    @Value("${SSO_LOGIN}")
    private String SSO_LOGIN;
    @Value("${CART_COOKIE_NAME}")
    private String CART_COOKIE_NAME;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        // 从cookie中获取token
        String token = CookieUtils.getCookieValue(httpServletRequest, "token");
        // 不存在，跳转到登录页面，并且还要跳转回来
        if(StringUtils.isBlank(token)){
            // 附加上 登录成功后要跳转的地址
            httpServletResponse.sendRedirect(SSO_LOGIN+"/page/login?redirect="+httpServletRequest.getRequestURL());
            return false;
        }
        // 存在，将获取的token调用sso的服务判断用户登录是否过期
        E3Result result = tokenService.getUserForToken(token);
        // 过期，跳转到登录页面，并且还要跳转回来
        if(result.getStatus() != 200){
            httpServletResponse.sendRedirect(SSO_LOGIN+"/page/login?redirect="+httpServletRequest.getRequestURL());
            return false;
        }
        // 不过期，合并cookie的购物车和redis中的购物车信息，然后放行
        TbUser user = (TbUser) result.getData();
        String cookieValue = CookieUtils.getCookieValue(httpServletRequest, CART_COOKIE_NAME, true);
        if(StringUtils.isNoneBlank(cookieValue)){
            cartService.margen(JsonUtils.jsonToList(cookieValue, TbItem.class),user.getId().toString());
        }
        httpServletRequest.setAttribute("user",user);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
