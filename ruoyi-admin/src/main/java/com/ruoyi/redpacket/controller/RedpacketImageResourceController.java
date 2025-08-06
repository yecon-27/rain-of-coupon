package com.ruoyi.redpacket.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.redpacket.domain.RedpacketImageResource;
import com.ruoyi.redpacket.service.IRedpacketImageResourceService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 图片资源Controller
 * 
 * @author ruoyi
 * @date 2025-08-06
 */
@RestController
@RequestMapping("/redpacket/resource")
public class RedpacketImageResourceController extends BaseController
{
    @Autowired
    private IRedpacketImageResourceService redpacketImageResourceService;

    /**
     * 查询图片资源列表
     */
    @PreAuthorize("@ss.hasPermi('redpacket:resource:list')")
    @GetMapping("/list")
    public TableDataInfo list(RedpacketImageResource redpacketImageResource)
    {
        startPage();
        List<RedpacketImageResource> list = redpacketImageResourceService.selectRedpacketImageResourceList(redpacketImageResource);
        return getDataTable(list);
    }

    /**
     * 导出图片资源列表
     */
    @PreAuthorize("@ss.hasPermi('redpacket:resource:export')")
    @Log(title = "图片资源", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, RedpacketImageResource redpacketImageResource)
    {
        List<RedpacketImageResource> list = redpacketImageResourceService.selectRedpacketImageResourceList(redpacketImageResource);
        ExcelUtil<RedpacketImageResource> util = new ExcelUtil<RedpacketImageResource>(RedpacketImageResource.class);
        util.exportExcel(response, list, "图片资源数据");
    }

    /**
     * 获取图片资源详细信息
     */
    @PreAuthorize("@ss.hasPermi('redpacket:resource:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(redpacketImageResourceService.selectRedpacketImageResourceById(id));
    }

    /**
     * 新增图片资源
     */
    @PreAuthorize("@ss.hasPermi('redpacket:resource:add')")
    @Log(title = "图片资源", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody RedpacketImageResource redpacketImageResource)
    {
        return toAjax(redpacketImageResourceService.insertRedpacketImageResource(redpacketImageResource));
    }

    /**
     * 修改图片资源
     */
    @PreAuthorize("@ss.hasPermi('redpacket:resource:edit')")
    @Log(title = "图片资源", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody RedpacketImageResource redpacketImageResource)
    {
        return toAjax(redpacketImageResourceService.updateRedpacketImageResource(redpacketImageResource));
    }

    /**
     * 删除图片资源
     */
    @PreAuthorize("@ss.hasPermi('redpacket:resource:remove')")
    @Log(title = "图片资源", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(redpacketImageResourceService.deleteRedpacketImageResourceByIds(ids));
    }
}
