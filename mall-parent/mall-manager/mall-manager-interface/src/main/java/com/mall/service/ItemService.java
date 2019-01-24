package com.mall.service;

import com.mall.common.pojo.E3Result;
import com.mall.common.pojo.EasyUiDateResult;
import com.mall.pojo.TbItem;
import com.mall.pojo.TbItemDesc;

public interface ItemService {

    /**
     * 获取指定商品信息
     * @param id
     * @return
     */
    TbItem getById(Long id);

    /**
     * 获取商品列表
     * @param page
     * @param rows
     * @return
     */
    EasyUiDateResult getList(int page, int rows);

    /**
     * 新添商品
     * @param tbItem
     * @param tbItemDesc
     * @return
     */
    E3Result save(TbItem tbItem, String tbItemDesc);

    /**
     * 获取商品的描述信息
     * @param itemId
     * @return
     */
    TbItemDesc getItemDescById(long itemId);

}
