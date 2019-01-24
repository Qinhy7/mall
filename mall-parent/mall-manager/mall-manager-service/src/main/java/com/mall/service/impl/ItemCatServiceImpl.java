package com.mall.service.impl;

import com.mall.common.pojo.EasyUiTreeNodeResult;
import com.mall.mapper.TbItemCatMapper;
import com.mall.pojo.TbItemCat;
import com.mall.pojo.TbItemCatExample;
import com.mall.service.ItemCatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class ItemCatServiceImpl implements ItemCatService {

    @Autowired
    private TbItemCatMapper mapper;

    @Override
    public List<EasyUiTreeNodeResult> getItemCat(long parentId) {
        // 使用mapper 查询 分类，然后拼装返回值
        TbItemCatExample example = new TbItemCatExample();
        TbItemCatExample.Criteria criteria = example.createCriteria();
        criteria.andParentIdEqualTo(parentId);
        List<TbItemCat> tbItemCats = mapper.selectByExample(example);

        LinkedList<EasyUiTreeNodeResult> results = new LinkedList<>();
        for (TbItemCat tbItemCat : tbItemCats) {
            EasyUiTreeNodeResult tmp = new EasyUiTreeNodeResult();
            tmp.setId(tbItemCat.getId());
            tmp.setState(tbItemCat.getIsParent()?"closed":"poen");
            tmp.setText(tbItemCat.getName());

            results.add(tmp);
        }

        return results;
    }
}
