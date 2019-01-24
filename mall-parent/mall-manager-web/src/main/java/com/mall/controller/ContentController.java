package com.mall.controller;

import com.mall.common.pojo.E3Result;
import com.mall.common.pojo.EasyUiTreeNodeResult;
import com.mall.pojo.TbContent;
import com.mall.portal.service.ContentCategoryService;
import com.mall.portal.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 内容管理controller
 */
@RestController
public class ContentController {

    @Autowired
    private ContentCategoryService contentCategoryService;
    @Autowired
    private ContentService contentService;

    /**
     * 获取内容内类列表
     * @return
     */
    @RequestMapping("/content/category/list")
    public List<EasyUiTreeNodeResult> getList(@RequestParam(value = "id", defaultValue = "0 ") Long parentId){
        return contentCategoryService.getCatList(parentId);
    }

    /**
     * 获取指定分类的列表
     * @return
     */
    @RequestMapping("/content/query/list")
    public List<TbContent> getListByCategoryId(Long categoryId, Integer page, Integer rows){
        return contentService.getListByCategoryId(categoryId,page,rows);
    }

    /**
     * 添加分类
     * @param tbContent
     * @return
     */
    @RequestMapping(value = "/content/save",method = RequestMethod.POST)
    public E3Result save(TbContent tbContent){
        return contentService.save(tbContent);
    }

}
