package com.mall.search.service;

import com.mall.common.pojo.SearchResult;

public interface SearchItemService {

    SearchResult getItemList(String keywords, int page, int rows) throws Exception;

}
