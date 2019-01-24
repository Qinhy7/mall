package com.mall.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PageController {

    @RequestMapping("/")
    public String index(){
        // 访问项目，调转到index页面
        return "index";
    }

    // 负责页面的跳转
    @RequestMapping("/{page}")
    public String page(@PathVariable String page){
        return page;
    }



}
