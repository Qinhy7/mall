package com.mall.portal.service;

import com.mall.common.pojo.E3Result;
import com.mall.pojo.TbContent;

import java.util.List;

public interface ContentService {

    List<TbContent> getListByCategoryId(Long categoryId, Integer page, Integer rows);

    E3Result save(TbContent tbContent);

    List<TbContent> getListByCategoryId(Long categoryId);
}
