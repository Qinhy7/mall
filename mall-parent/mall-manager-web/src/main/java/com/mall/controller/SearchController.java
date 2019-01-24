package com.mall.controller;


import com.mall.common.pojo.E3Result;
import com.mall.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SearchController {

    @Autowired
    private SearchService searchService;

    /**
     * 导入索引库
     * @return
     */
    @RequestMapping("/index/item/import")
    public E3Result importIndex(){
        return searchService.importIndex();
    }

}
