package com.mall.portal.service;

import com.mall.common.pojo.EasyUiTreeNodeResult;

import java.util.List;

public interface ContentCategoryService {

    List<EasyUiTreeNodeResult> getCatList(long parentId);

}
