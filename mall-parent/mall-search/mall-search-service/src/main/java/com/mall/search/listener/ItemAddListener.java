package com.mall.search.listener;

import com.mall.common.pojo.SearchItem;
import com.mall.search.mapper.ItemMapper;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 * 商品添加的消息处理监听器
 */
@Component
public class ItemAddListener implements MessageListener {

    @Resource
    private Destination topicDestination;
    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private SolrServer solrServer;


    @Override
    public void onMessage(Message message) {
        try {
            // 获取发送的消息， 这里只有一个商品的id
            TextMessage textMessage = (TextMessage) message;
            String id = textMessage.getText();

            if(id != null && !"".equals(id)){

                // 根据id查询数据库，并将结果写入到索引库中
                SearchItem searchItem = itemMapper.getItemById(new Long(id));

                SolrInputDocument document = new SolrInputDocument();
                document.addField("id", searchItem.getId());
                document.addField("item_title", searchItem.getTitle());
                document.addField("item_sell_point", searchItem.getSell_point());
                document.addField("item_price", searchItem.getPrice());
                document.addField("item_image", searchItem.getImage());
                document.addField("item_category_name", searchItem.getCategory_name());

                // 写入索引库
                solrServer.add(document);

                // 提交
                solrServer.commit();
            }

        } catch (Exception e){
            e.printStackTrace();
        }



    }
}
