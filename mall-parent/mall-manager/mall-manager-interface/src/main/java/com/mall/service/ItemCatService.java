package com.mall.service;

import com.mall.common.pojo.EasyUiTreeNodeResult;

import java.util.List;

public interface ItemCatService {

    List<EasyUiTreeNodeResult> getItemCat(long parentId);

}
