package com.mall.portal.service.impl;

import com.mall.common.pojo.EasyUiTreeNodeResult;
import com.mall.mapper.TbContentCategoryMapper;
import com.mall.pojo.TbContentCategory;
import com.mall.pojo.TbContentCategoryExample;
import com.mall.portal.service.ContentCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class ContentCategoryServiceImpl implements ContentCategoryService {

    @Autowired
    private TbContentCategoryMapper mapper;

    @Override
    public List<EasyUiTreeNodeResult> getCatList(long parentId) {
        // 查询parentId下的所有分类
        TbContentCategoryExample example = new TbContentCategoryExample();
        TbContentCategoryExample.Criteria criteria = example.createCriteria();
        criteria.andParentIdEqualTo(parentId);
        List<TbContentCategory> tbContentCategories = mapper.selectByExample(example);

        // 封装返回结果
        List<EasyUiTreeNodeResult> results = new LinkedList<>();
        for (TbContentCategory category : tbContentCategories) {
            EasyUiTreeNodeResult tmp = new EasyUiTreeNodeResult();
            tmp.setText(category.getName());
            tmp.setId(category.getId());
            tmp.setState(category.getIsParent()?"closed":"open");

            results.add(tmp);
        }

        return results;
    }
}
