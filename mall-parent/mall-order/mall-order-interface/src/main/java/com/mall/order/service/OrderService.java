package com.mall.order.service;

import com.mall.common.pojo.E3Result;
import com.mall.order.pojo.OrderInfo;

public interface OrderService {

    /**
     * 插入订单表
     * @param orderInfo
     * @return
     */
    E3Result addOrder(OrderInfo orderInfo);

}
