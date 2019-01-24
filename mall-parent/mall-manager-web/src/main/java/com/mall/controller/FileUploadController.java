package com.mall.controller;

import com.mall.common.utils.FastDFSClient;
import com.mall.common.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * 图片上传
 */
@Controller
public class FileUploadController {

    @Value("${UPFILE_PATH}")
    private String UPFILE_PATH;


    /**
     *
     * @param uploadFile
     * @return 返回string 类型为了处理浏览器的兼容性问题
     *  string类型，响应类型为 text/plain ，这个类型的兼容性 比 application/json 的兼容性强
     */
    @RequestMapping("/pic/upload")
    @ResponseBody
    public String upload(MultipartFile uploadFile){
        try {
            // 调用fastdfs上传图片
            FastDFSClient fastDFSClient = new FastDFSClient("classpath:/fastdfs.conf");
            String originalFilename = uploadFile.getOriginalFilename();
            String extName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            String path = fastDFSClient.uploadFile(uploadFile.getBytes(), extName);

            // 拼装返回结果，要符合富文本编辑器 KinderEditor 的要求
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("error",0);
            map.put("url",UPFILE_PATH+path);
            return JsonUtils.objectToJson(map);
        } catch (Exception e){
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("error",1);
            map.put("message","上传失败");
            return JsonUtils.objectToJson(map);
        }
    }

}
