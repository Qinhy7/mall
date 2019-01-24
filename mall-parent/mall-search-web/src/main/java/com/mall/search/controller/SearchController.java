package com.mall.search.controller;

import com.mall.common.pojo.SearchResult;
import com.mall.search.service.SearchItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class SearchController {

    @Value("${SEARCH_RESULT_ROWS}")
    private Integer SEARCH_RESULT_ROWS;
    @Autowired
    private SearchItemService searchItemService;

    @RequestMapping("/search")
    public String getItemList(String keyword,
                              @RequestParam(defaultValue="1") Integer page, Model model)
    throws Exception{

        keyword = new String(keyword.getBytes("iso-8859-1"), "utf-8");

        //查询商品列表
        SearchResult searchResult = searchItemService.getItemList(keyword,
                page, SEARCH_RESULT_ROWS);
        //把结果传递给页面
        model.addAttribute("query", keyword);
        model.addAttribute("totalPages", searchResult.getTotalPages());
        model.addAttribute("page", page);
        model.addAttribute("recourdCount", searchResult.getRecordCount());
        model.addAttribute("itemList", searchResult.getItemList());



        return "search";
    }

}
