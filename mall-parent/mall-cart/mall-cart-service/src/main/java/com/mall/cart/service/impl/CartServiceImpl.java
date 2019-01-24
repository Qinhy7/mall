package com.mall.cart.service.impl;

import com.mall.cart.service.CartService;
import com.mall.common.pojo.E3Result;
import com.mall.common.redis.JedisClient;
import com.mall.common.utils.JsonUtils;
import com.mall.mapper.TbItemMapper;
import com.mall.pojo.TbItem;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CartServiceImpl implements CartService {
    @Value("${CART_REDIS_PRE}")
    private String CART_REDIS_PRE;
    
    @Autowired
    private JedisClient jedisClient;
    @Autowired
    private TbItemMapper itemMapper;

    @Override
    public E3Result deleteCartById(long userId) {
//        jedisClient.del(CART_REDIS_PRE + ":" + userId);
        return E3Result.ok();
    }

    @Override
    public E3Result addCartItem(String userId, Long itemId, int num) {
        // 判断redis中是否有hash key ，有的话修改数量，没有 新添
        String hget = jedisClient.hget(CART_REDIS_PRE + ":" + userId, itemId.toString());
        if(StringUtils.isNoneBlank(hget)){ // 不为空
            TbItem tbItem = JsonUtils.jsonToPojo(hget, TbItem.class);
            tbItem.setNum(num);
            // 写回redis
            jedisClient.hset(CART_REDIS_PRE + ":" + userId, itemId.toString(),JsonUtils.objectToJson(tbItem));
            // 返回结果
            return E3Result.ok();
        }

        // 新添
        TbItem tbItem = itemMapper.selectByPrimaryKey(itemId);
        tbItem.setNum(num);
        if(StringUtils.isNoneBlank(tbItem.getImage())){
            tbItem.setImage(tbItem.getImage().split(",")[0]);
        }
        // 写回redis
        jedisClient.hset(CART_REDIS_PRE + ":" + userId, itemId.toString(),JsonUtils.objectToJson(tbItem));

        return E3Result.ok();
    }

    @Override
    public List<TbItem> getCartList(String userId) {
        String s = jedisClient.get(CART_REDIS_PRE + ":" + userId);
        List<TbItem> tbItems = JsonUtils.jsonToList(s, TbItem.class);
        return tbItems;
    }

    @Override
    public E3Result deleteItemById(String userId, Long itemId) {
        jedisClient.hdel(CART_REDIS_PRE + ":" + userId, itemId.toString());
        return E3Result.ok();
    }

    @Override
    public E3Result updateItemNum(String userId, Long itemId, int num) {
        String hget = jedisClient.hget(CART_REDIS_PRE + ":" + userId, itemId.toString());
        if(StringUtils.isNoneBlank(hget)){
            TbItem tbItem = JsonUtils.jsonToPojo(hget, TbItem.class);
            tbItem.setNum(num);
            jedisClient.hset(CART_REDIS_PRE + ":" + userId, itemId.toString(), JsonUtils.objectToJson(tbItem));
        }
        return null;
    }

    @Override
    public E3Result margen(List<TbItem> cookieVaules, String userId) {
        // 如果 redis中有，合并数量，没有新添
        for (TbItem tbItem : cookieVaules) {
            Long id = tbItem.getId();
            String hget = jedisClient.hget(CART_REDIS_PRE + ":" + userId, id.toString());
            if(StringUtils.isNoneBlank(hget)){
                TbItem item = JsonUtils.jsonToPojo(hget, TbItem.class);
                // 修改数量
                item.setNum(item.getNum() + tbItem.getNum());
                jedisClient.hset(CART_REDIS_PRE+":"+userId,id.toString(), JsonUtils.objectToJson(item));
            }else {
                // 新添商品
                jedisClient.hset(CART_REDIS_PRE+":"+userId, id.toString(),JsonUtils.objectToJson(tbItem));
            }
        }

        return E3Result.ok();
    }
}
