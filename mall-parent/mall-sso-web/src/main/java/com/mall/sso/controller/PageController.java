package com.mall.sso.controller;


import com.mall.common.pojo.E3Result;
import com.mall.pojo.TbUser;
import com.mall.sso.service.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PageController {

    @Autowired
    private RegisterService registerService;

    /**
     * 进入注册页面
     * @return
     */
    @RequestMapping("/page/resgiter")
    public String register(){
        return "register";
    }

    /**
     * 校验数据
     *  type ： 1 校验用户名 2 校验手机号
     * @return
     */
    @RequestMapping("/user/check/{value}/{type}")
    @ResponseBody
    public E3Result check(@PathVariable String value, @PathVariable int type){
        E3Result result = registerService.check(value, type);
        return result;
    }

    /**
     * 注册用户
     * @param user
     * @return
     */
    @RequestMapping("/user/register")
    @ResponseBody
    public E3Result register(TbUser user){
        E3Result result = registerService.register(user);
        return result;
    }

    @RequestMapping("/page/login")
    public String login(@RequestParam("redirect") String redirect, Model model){
        model.addAttribute("redirect",redirect);
        return "login";
    }

}
