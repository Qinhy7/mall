package com.mall.cart.controller;

import com.mall.cart.service.CartService;
import com.mall.common.pojo.E3Result;
import com.mall.common.utils.CookieUtils;
import com.mall.common.utils.JsonUtils;
import com.mall.pojo.TbItem;
import com.mall.pojo.TbUser;
import com.mall.service.ItemService;
import com.sun.org.apache.regexp.internal.RE;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Controller
public class CartController {

    @Value("${CART_COOKIE_NAME}")
    private String CART_COOKIE_NAME;
    @Value("${CART_COOKIE_ACTIVE}")
    private Integer CART_COOKIE_ACTIVE;

    @Autowired
    private ItemService itemService;
    @Autowired
    private CartService cartService;

    /**
     * 删除购物车指定商品
     * @param itemId
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/cart/delete/{itemId}")
    public String deleteItem(@PathVariable Long itemId, HttpServletRequest request,
                             HttpServletResponse response){
        // 登录
        TbUser user = (TbUser) request.getAttribute("user");
        if(user != null) {
            // 删除商品
            cartService.deleteItemById(user.getId().toString(),itemId);
            // 返回逻辑视图
            return "redirect:/cart/cart.html";
        }


        // 未登录
        // 从cookie中获取购物车信息
        List<TbItem> tbItems = getCartListFormCookie(request);
        // 删除指定id
        for (TbItem tbItem : tbItems) {
            if (tbItem.getId() == itemId.longValue()){
                tbItems.remove(tbItem);
                break;
            }
        }
        // 写回cookie
        writeToCookie(request,response,tbItems);

        // 返回逻辑视图
        return "redirect:/cart/cart.html";
    }

    /**
     * 修改购物车数量
     * @param request
     * @param itemId
     * @param num
     * @return
     */
    @RequestMapping(value = "/cart/update/num/{itemId}/{num}",method = RequestMethod.POST)
    public String updateNum(HttpServletRequest request, @PathVariable() Long itemId,
                            @PathVariable int num, HttpServletResponse response){
        // 登录
        TbUser user = (TbUser) request.getAttribute("user");
        if(user != null) {
            // 修改指定数量
            cartService.updateItemNum(user.getId().toString(),itemId,num);
            // 返回逻辑视图
            return "redirect:/cart/cart.html";
        }


        // 未登录
        // 从cookie中获取信息
        List<TbItem> tbItems = getCartListFormCookie(request);
        // 遍历集合，修改指定商品的数目
        for (TbItem tbItem : tbItems) {
            if(tbItem.getId().longValue() == itemId){
                tbItem.setNum(num);
                break;
            }
        }
        writeToCookie(request,response,tbItems);

        // 返回逻辑视图
        return "redirect:/cart/cart.html";
    }

    /**
     * 跳转到购物车页面
     *  同样也要判断用户是否登录
     * @param request
     * @param model
     * @return
     */
    @RequestMapping("/cart/cart")
    public String cart(HttpServletRequest request, Model model, HttpServletResponse response){
        // 从cookie中获取信息
        List<TbItem> tbItems = getCartListFormCookie(request);
        // 登录状态
        TbUser user = (TbUser) request.getAttribute("user");
        if(user != null) {
            // 合并redis和cookie的信息
            cartService.margen(tbItems,user.getId().toString());
            // 获取购物车列表
            List<TbItem> cartList = cartService.getCartList(user.getId().toString());
            model.addAttribute("cartList", cartList);

            // 清空cookie的购物车信息
            CookieUtils.setCookie(request,response,CART_COOKIE_NAME,"",true);
            // 返回逻辑视图
            return "cart";
        }


        // 未登录状态
        // 将信息展示到页面
        model.addAttribute("cartList",tbItems);
        return "cart";
    }


    /**
     * 添加商品到购物车
     *  总体分为两种情况：1、用户没有登录，存储数据到cookie 2、 登录，存储到redis中
     */
    @RequestMapping("/cart/add/{itemId}")
    public String cartList(@PathVariable Long itemId,
                           @RequestParam(value = "num",defaultValue = "1") int num,
                           HttpServletRequest request, HttpServletResponse response){

        // 判断用户是否登录
        // 登录
        TbUser user = (TbUser) request.getAttribute("user");
        if(user != null) {
            // 将新加的商品保存到redis中
            E3Result e3Result = cartService.addCartItem(user.getId().toString(), itemId, num);
            // 返回逻辑视图
            return "cartSuccess";
        }


        // 没有登录
        // 从cookie中获取购物车信息
        List<TbItem> tbItems = getCartListFormCookie(request);
        // 遍历他，找到对应的商品id，修改其数量。没有的话就新加
        boolean flag = true;
        for (TbItem tbItem : tbItems) {
            if(tbItem.getId() == itemId.longValue()){
                tbItem.setNum(tbItem.getNum() + num); // 用库存来表示数量
                flag = false;
                break;
            }
        }
        if(flag){ // 表示商品不存在
            // 根据商品id查询商品信息
            TbItem tbItem = itemService.getById(itemId);
            tbItem.setNum(num);
            tbItems.add(tbItem);
            String image = tbItem.getImage();// 只保留一个图片
            if(StringUtils.isNoneBlank(image)){
                tbItem.setImage(image.split(",")[0]);
            }
        }
        // 将cookie写回
        writeToCookie(request,response,tbItems);

        // 返回逻辑视图
        return "cartSuccess";
    }


    /**
     * 将cookie写回浏览器
     * @param request
     * @param response
     * @param cookieValue
     */
    private void writeToCookie(HttpServletRequest request, HttpServletResponse response,
                               Object cookieValue){
        CookieUtils.setCookie(request, response, CART_COOKIE_NAME, JsonUtils.objectToJson(cookieValue),
                CART_COOKIE_ACTIVE,true);
    }

    /**
     * 从cookie中获取cart信息
     * @param request
     * @return
     */
    private List<TbItem> getCartListFormCookie(HttpServletRequest request){
        // 以 utf8 编码
        String cookieValue = CookieUtils.getCookieValue(request, CART_COOKIE_NAME, true);
        if(StringUtils.isNoneBlank(cookieValue)){
            List<TbItem> tbItems = JsonUtils.jsonToList(cookieValue, TbItem.class);
            return tbItems;
        }
        return new ArrayList<>();
    }

}
