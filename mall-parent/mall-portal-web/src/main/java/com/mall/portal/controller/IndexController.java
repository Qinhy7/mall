package com.mall.portal.controller;

import com.mall.pojo.TbContent;
import com.mall.portal.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RestController
public class IndexController {

    @Value("${PICTURE_1}")
    private Long PICTURE_1;

    @Autowired
    private ContentService contentService;

    /**
     * 跳转到首页
     * @param modelAndView
     * @return
     */
    @RequestMapping("/")
    public ModelAndView index(ModelAndView modelAndView){
        modelAndView.setViewName("index");
        // 查询指定分类
        List<TbContent> ad1List = contentService.getListByCategoryId(PICTURE_1);
        modelAndView.addObject("ad1List",ad1List);

        return modelAndView;
    }

}
