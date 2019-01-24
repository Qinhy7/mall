package com.mall.search.service.impl;

import com.mall.common.pojo.E3Result;
import com.mall.common.pojo.SearchItem;
import com.mall.search.mapper.ItemMapper;
import com.mall.search.service.SearchService;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    SolrServer solrServer;
    @Autowired
    ItemMapper itemMapper; // 这里需要添加配置，将mapper文件和接口加入到资源路径下

    /**
     * 导入所有的索引
     * @return
     */
    @Override
    public E3Result importIndex() {
        try {

            // 先把索引数据从数据库中查询出来
            List<SearchItem> itemList = itemMapper.getItemList();
            // 然后将索引数据加入索引库中
            for (SearchItem item : itemList) {
                SolrInputDocument document = new SolrInputDocument();
                document.addField("id", item.getId());
                document.addField("item_title", item.getTitle());
                document.addField("item_sell_point", item.getSell_point());
                document.addField("item_price", item.getPrice());
                document.addField("item_image", item.getImage());
                document.addField("item_category_name", item.getCategory_name());

                // 写入索引库
                solrServer.add(document);
            }
            // 提交
            solrServer.commit();

            // 返回结果
            return E3Result.ok();
        }catch (Exception e){
            e.printStackTrace();
            return E3Result.build(500,"插入索引库失败");
        }
    }
}
