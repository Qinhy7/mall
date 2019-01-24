package com.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mall.common.pojo.E3Result;
import com.mall.common.pojo.EasyUiDateResult;
import com.mall.common.redis.JedisClient;
import com.mall.common.utils.IDUtils;
import com.mall.common.utils.JsonUtils;
import com.mall.mapper.TbItemDescMapper;
import com.mall.mapper.TbItemMapper;
import com.mall.pojo.TbItem;
import com.mall.pojo.TbItemDesc;
import com.mall.pojo.TbItemExample;
import com.mall.service.ItemService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisCluster;

import java.util.Date;
import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private TbItemMapper itemMapper;
    @Autowired
    private TbItemDescMapper descMapper;
    @Autowired
    private JedisClient jedisClient;

    @Value("${ITEM_REDIS_PREFIX}")
    private String ITEM_REDIS_PREFIX;
    @Value("${ITEM_REDIS_END}")
    private String ITEM_REDIS_END;
    @Value("${ITEM_CACHE_TIME}")
    private Integer ITEM_CACHE_TIME;


    @Override
    public TbItem getById(Long id) {

        // 查询缓存
        try {

            String itemStr = jedisClient.get(ITEM_REDIS_PREFIX + id + ITEM_REDIS_END);
            if(StringUtils.isNoneBlank(itemStr)){
                TbItem tbItem = JsonUtils.jsonToPojo(itemStr, TbItem.class);
                return tbItem;
            }

        }catch (Exception e){
            e.printStackTrace();
        }


        TbItem tbItem = itemMapper.selectByPrimaryKey(id);

        // 将内容放入缓存
        try {
            // 以 item_prefix:商品di:item_end 的形式存入 redis
            jedisClient.set(ITEM_REDIS_PREFIX+id+ITEM_REDIS_END, JsonUtils.objectToJson(tbItem));
            jedisClient.expire(ITEM_REDIS_PREFIX+id+ITEM_REDIS_END,ITEM_CACHE_TIME);
        } catch (Exception e){
            e.printStackTrace();
        }


        return tbItem;
    }

    @Override
    public EasyUiDateResult getList(int page, int rows) {
        // 先在mybatis的配置文件中设置 plugs 插件
        // 使用pagehelper插件来查询，需要在查询前设置分页信息
        PageHelper.startPage(page,rows);

        TbItemExample example = new TbItemExample();
        List<TbItem> tbItems = itemMapper.selectByExample(example);

        // 包装返回参数
        EasyUiDateResult result = new EasyUiDateResult();
        result.setRows(tbItems); // rows 是当前页的数据，total 是所有数据的个数
        PageInfo pageInfo = new PageInfo(tbItems);
        result.setTotal(pageInfo.getTotal());

        return result;
    }

    @Override
    public E3Result save(TbItem tbItem, String tbItemDesc) {

        // 新添的商品没有id，status，updateTime，createTime
        long itemId = IDUtils.genItemId();
        tbItem.setId(itemId);
        // 商品状态，1-正常，2-下架，3-删除
        tbItem.setStatus((byte) 1);
        tbItem.setCreated(new Date());
        tbItem.setUpdated(new Date());

        // 创建TbItemDesc对象，并填写信息
        TbItemDesc itemDesc = new TbItemDesc();
        itemDesc.setCreated(new Date());
        itemDesc.setItemDesc(tbItemDesc);
        itemDesc.setItemId(itemId);
        itemDesc.setUpdated(new Date());

        // 保存，返回结果
        itemMapper.insert(tbItem);
        descMapper.insert(itemDesc);

        // 将内容放入缓存
        try {
            // 以 item_prefix:商品di:item_end 的形式存入 redis
            jedisClient.set(ITEM_REDIS_PREFIX+itemId+ITEM_REDIS_END, JsonUtils.objectToJson(tbItem));
            jedisClient.expire(ITEM_REDIS_PREFIX+itemId+ITEM_REDIS_END,ITEM_CACHE_TIME);
        } catch (Exception e){
            e.printStackTrace();
        }

        // 将id返回给controller，用于发送消息
        return E3Result.ok(itemId);
    }

    @Override
    public TbItemDesc getItemDescById(long itemId) {
        // 查询缓存
        try {

            String itemDescStr = jedisClient.get(ITEM_REDIS_PREFIX + itemId + ITEM_REDIS_END);
            if(StringUtils.isNoneBlank(itemDescStr)){
                TbItemDesc tbItemDesc = JsonUtils.jsonToPojo(itemDescStr, TbItemDesc.class);
                return tbItemDesc;
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        TbItemDesc tbItemDesc = descMapper.selectByPrimaryKey(itemId);


        // 将内容放入缓存
        try {
            // 以 item_prefix:商品di:item_end 的形式存入 redis
            jedisClient.set(ITEM_REDIS_PREFIX+itemId+ITEM_REDIS_END, JsonUtils.objectToJson(tbItemDesc));
            jedisClient.expire(ITEM_REDIS_PREFIX+itemId+ITEM_REDIS_END,ITEM_CACHE_TIME);
        } catch (Exception e){
            e.printStackTrace();
        }

        return tbItemDesc;
    }
}
