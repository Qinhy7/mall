package com.mall.item.listener;

import com.mall.item.pojo.Item;
import com.mall.pojo.TbItem;
import com.mall.pojo.TbItemDesc;
import com.mall.service.ItemService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

@Component
public class HtmlGenListenner implements MessageListener {

    @Autowired
    private ItemService itemServiceImpl;
    @Autowired
    private FreeMarkerConfig freeMarkerConfigurer;
    @Value("${FREEMARKER_HTML}")
    private String FREEMARKER_HTML;


    @Override
    public void onMessage(Message message) {

        try {
            // 获得发布的消息
            TextMessage textMessage = (TextMessage) message;
            String text = textMessage.getText();
            Long itemId = new Long(text);
            // 根据id查询数据，然后将数据结合freemarker生成 静态页面

            Configuration configuration = freeMarkerConfigurer.getConfiguration();
            Template template = configuration.getTemplate("item.ftl");

            // 获取商品信息和商品描述信息
            TbItem tbItem = itemServiceImpl.getById(itemId);
            TbItemDesc tbItemDesc = itemServiceImpl.getItemDescById(itemId);
            Item item = new Item(tbItem);

            Map map = new HashMap();
            map.put("item", item);
            map.put("itemDesc", tbItemDesc);

            // 获取输出流
            Writer out = new FileWriter(
                    new File(FREEMARKER_HTML+itemId.toString()+".html"));
            // 调用模板对象的process方法，生成文件。
            template.process(map, out);
            // 7、关闭流。
            out.close();

        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
