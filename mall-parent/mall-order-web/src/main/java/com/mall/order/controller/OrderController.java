package com.mall.order.controller;

import com.mall.cart.service.CartService;
import com.mall.common.pojo.E3Result;
import com.mall.order.pojo.OrderInfo;
import com.mall.order.service.OrderService;
import com.mall.pojo.TbItem;
import com.mall.pojo.TbUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class OrderController {

    @Autowired
    private CartService cartService;
    @Autowired
    private OrderService orderService;

    /**
     * 创建订单
     * @param request
     * @param model
     * @return
     */
    @RequestMapping("/order/order-cart")
    public String order(HttpServletRequest request, Model model){
        // 从请求域中获取user
        TbUser user = (TbUser) request.getAttribute("user");
        // 根据userid 获取购物车列表
        List<TbItem> cartList = cartService.getCartList(user.getId().toString());

        model.addAttribute("cartList", cartList);
        return "order-cart";
    }

    /**
     * 支付
     * @return
     */
    @RequestMapping(value = "/order/create",method = RequestMethod.POST)
    public String pay(OrderInfo orderInfo, HttpServletRequest request){
        TbUser user = (TbUser) request.getAttribute("user");
        // 补充部分信息
        orderInfo.setUserId(user.getId());
        // 调用服务插入数据
        E3Result e3Result = orderService.addOrder(orderInfo);
        if(e3Result.getStatus() == 200){
            // 插入数据后清空购物车
            cartService.deleteCartById(user.getId());
        }

        //把订单号传递给页面
        request.setAttribute("orderId", e3Result.getData());
        request.setAttribute("payment", orderInfo.getPayment());

        return "success";
    }

}
