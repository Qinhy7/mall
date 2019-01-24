package com.mall.controller;

import com.mall.common.pojo.E3Result;
import com.mall.common.pojo.EasyUiDateResult;
import com.mall.common.pojo.EasyUiTreeNodeResult;
import com.mall.pojo.TbItem;
import com.mall.service.ItemCatService;
import com.mall.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.jms.*;
import java.util.List;

@RestController
public class ItemController {

    @Autowired
    private ItemService itemService;
    @Autowired
    private ItemCatService itemCatService;
    @Autowired
    private JmsTemplate jmsTemplate;
    @Resource // 根据id获取bean
    private Destination topicDestination;

    /**
     * 获取指定商品信息
     * @param id
     * @return
     */
    @RequestMapping(value = "/item/{id}",method = RequestMethod.GET)
    public TbItem getById(@PathVariable Long id){
        TbItem item = itemService.getById(id);

        return item;
    }

    /**
     * 获取商品列表
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping(value = "/item/list",method = RequestMethod.GET)
    public EasyUiDateResult list(@RequestParam("page") Integer page, @RequestParam("rows") Integer rows){
        EasyUiDateResult result = itemService.getList(page, rows);
        return result;
    }

    /**
     * 获取商品分类列表
     * @param parentId
     * @return
     */
    @RequestMapping("/item/cat/list")
    public List<EasyUiTreeNodeResult> getCategory(@RequestParam(value = "id",defaultValue = "0") Long parentId){
        List<EasyUiTreeNodeResult> results = itemCatService.getItemCat(parentId);
        return results;
    }

    /**
     * 商品添加
     * @param tbItem
     * @param desc
     * @return
     */
    @RequestMapping(value = "/item/save",method = RequestMethod.POST)
    public E3Result save(TbItem tbItem, String desc){
        E3Result result = itemService.save(tbItem, desc);

        // 这里需要在添加商品完成后，发送一个消息
        // 为什么在这里添加？ 不在商品添加服务中发送消息？
        // 我觉得  在manager中发送消息，search收到消息时，manager的事物可能还没有提交呢
        // 虽然在 search中sleep，但是感觉不太合适

        try {
            jmsTemplate.send(topicDestination, new MessageCreator() {
                @Override
                public Message createMessage(Session session) throws JMSException {
                    TextMessage textMessage = session.createTextMessage(result.getData().toString());
                    return textMessage;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

        return result;
    }

}
