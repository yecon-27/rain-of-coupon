package com.ruoyi.redpacket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.redpacket.domain.RedpacketImageResource;
import com.ruoyi.redpacket.service.IRedpacketImageResourceService;
import java.util.List;

/**
 * 公开图片资源Controller（无需认证）
 * 
 * @author ruoyi
 * @date 2025-08-24
 */
@Anonymous
@RestController
@RequestMapping("/public/redpacket/image")
public class PublicImageResourceController extends BaseController {
    
    @Autowired
    private IRedpacketImageResourceService imageResourceService;
    
    /**
     * 根据资源标识获取图片信息
     */
    @GetMapping("/resource/{resourceKey}")
    public AjaxResult getImageByKey(@PathVariable String resourceKey) {
        try {
            RedpacketImageResource query = new RedpacketImageResource();
            query.setResourceKey(resourceKey);
            
            List<RedpacketImageResource> list = imageResourceService.selectRedpacketImageResourceList(query);
            if (list.isEmpty()) {
                return error("图片资源不存在: " + resourceKey);
            }
            
            return success(list.get(0));
        } catch (Exception e) {
            logger.error("获取图片资源失败", e);
            return error("获取图片资源失败");
        }
    }
    
    /**
     * 根据使用场景获取图片列表
     */
    @GetMapping("/scene/{scene}")
    public AjaxResult getImagesByScene(@PathVariable String scene) {
        try {
            RedpacketImageResource query = new RedpacketImageResource();
            query.setUsageScene(scene);
            
            List<RedpacketImageResource> list = imageResourceService.selectRedpacketImageResourceList(query);
            return success(list);
        } catch (Exception e) {
            logger.error("根据场景获取图片资源失败", e);
            return error("获取图片资源失败");
        }
    }
    
    /**
     * 获取所有可用的图片资源
     */
    @GetMapping("/all")
    public AjaxResult getAllImages() {
        try {
            List<RedpacketImageResource> list = imageResourceService.selectRedpacketImageResourceList(new RedpacketImageResource());
            return success(list);
        } catch (Exception e) {
            logger.error("获取所有图片资源失败", e);
            return error("获取图片资源失败");
        }
    }
}