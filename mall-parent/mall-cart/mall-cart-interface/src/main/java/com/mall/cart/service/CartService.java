package com.mall.cart.service;

import com.mall.common.pojo.E3Result;
import com.mall.pojo.TbItem;

import java.util.List;

public interface CartService {

    /**
     * 清空购物车
     * @param userId
     * @return
     */
    E3Result deleteCartById(long userId);


    /**
     * 添加商品到购物车中
     * @param userId
     * @param itemId
     * @param num
     * @return
     */
    E3Result addCartItem(String userId, Long itemId, int num);

    /**
     * 获取用户的购物车
     * @param userId
     * @return
     */
    List<TbItem> getCartList(String userId);

    /**
     * 删除商品
     * @param userId
     * @param itemId
     * @return
     */
    E3Result deleteItemById(String userId, Long itemId);

    /**
     * 修改商品数量
     * @param userId
     * @param itemId
     * @param num
     * @return
     */
    E3Result updateItemNum(String userId, Long itemId, int num);

    /**
     * 合并cookie和redis的购物车信息
     * @param cookieVaules
     * @param userId
     * @return
     */
    E3Result margen(List<TbItem> cookieVaules, String userId);

}
