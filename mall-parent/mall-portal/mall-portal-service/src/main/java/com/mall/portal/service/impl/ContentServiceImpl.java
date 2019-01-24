package com.mall.portal.service.impl;

import com.github.pagehelper.PageHelper;
import com.mall.common.pojo.E3Result;
import com.mall.common.redis.JedisClient;
import com.mall.common.utils.JsonUtils;
import com.mall.mapper.TbContentMapper;
import com.mall.pojo.TbContent;
import com.mall.pojo.TbContentExample;
import com.mall.portal.service.ContentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ContentServiceImpl implements ContentService {

    @Autowired
    private TbContentMapper mapper;
    @Autowired
    private JedisClient jedisClient;
    @Value("${CONTENT_CACHE_PREFIX}")
    private String CONTENT_CACHE;

    @Override
    public List<TbContent> getListByCategoryId(Long categoryId, Integer page, Integer rows) {
        // 使用pagehalper进行分页
        PageHelper.startPage(page, rows);


        TbContentExample example = new TbContentExample();
        TbContentExample.Criteria criteria = example.createCriteria();
        criteria.andCategoryIdEqualTo(categoryId);
        List<TbContent> tbContents = mapper.selectByExample(example);

        return tbContents;
    }

    @Override
    public E3Result save(TbContent tbContent) {
        /*
            每次在新添加内容时，都使缓存中对应分类的缓存失效。
         */

        // 先补全属性，然后在插入
        tbContent.setCreated(new Date());
        tbContent.setUpdated(new Date());

        mapper.insert(tbContent);

        // 清楚对应的缓存
        if(jedisClient.exists(CONTENT_CACHE)){
            String hget = jedisClient.hget(CONTENT_CACHE, tbContent.getCategoryId().toString());
            if(StringUtils.isNoneBlank(hget)){
                jedisClient.hdel(CONTENT_CACHE, tbContent.getCategoryId().toString());
            }
        }

        return E3Result.ok();
    }

    @Override
    public List<TbContent> getListByCategoryId(Long categoryId) {

        /*
            这里需要对业务进行修改，在查询时需要先查询缓存
            缓存没有的话在去查询数据库，然后在将查询的数据放到缓存中

            将读取到的数据以hash类型存到redis中
            hash名 content_cache
                filed 为每个内容的分类id
                value 为读取到的数据

            为了不影响业务需要将缓存相关的用try
         */

        // 查询缓存
        try {
            String caches = jedisClient.hget(CONTENT_CACHE, categoryId.toString());
            if(StringUtils.isNoneBlank(caches)){
                List<TbContent> tbContents = JsonUtils.jsonToList(caches, TbContent.class);
                return tbContents;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        // 查询数据库
        TbContentExample example = new TbContentExample();
        TbContentExample.Criteria criteria = example.createCriteria();
        criteria.andCategoryIdEqualTo(categoryId);
        List<TbContent> tbContents = mapper.selectByExample(example);

        // 将查到的结果放到redis中
        try {
            jedisClient.hset(CONTENT_CACHE,categoryId.toString(),JsonUtils.objectToJson(tbContents));
        }catch (Exception e){
            e.printStackTrace();
        }

        return tbContents;
    }
}
