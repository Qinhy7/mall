package com.mall.item.controller;

import com.mall.item.pojo.Item;
import com.mall.pojo.TbItem;
import com.mall.pojo.TbItemDesc;
import com.mall.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class ItemController {

    @Autowired
    private ItemService itemServiceImpl;

    /**
     * 获取商品信息
     * @param itemId
     * @return
     */
    @RequestMapping("/item/{itemId}")
    public ModelAndView getItemInfo(@PathVariable("itemId") long itemId){
        // 获取商品信息和商品描述信息
        TbItem tbItem = itemServiceImpl.getById(itemId);
        TbItemDesc tbItemDesc = itemServiceImpl.getItemDescById(itemId);
        Item item = new Item(tbItem);
        // 返回结果给页面
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("item");
        modelAndView.addObject("item", item);
        modelAndView.addObject("itemDesc", tbItemDesc);

        return modelAndView;
    }

}
