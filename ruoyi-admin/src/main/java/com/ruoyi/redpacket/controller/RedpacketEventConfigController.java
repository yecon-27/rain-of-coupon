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
import com.ruoyi.redpacket.domain.RedpacketEventConfig;
import com.ruoyi.redpacket.service.IRedpacketEventConfigService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 活动配置Controller
 * 
 * @author ruoyi
 * @date 2025-08-06
 */
@RestController
@RequestMapping("/redpacket/config")
public class RedpacketEventConfigController extends BaseController
{
    @Autowired
    private IRedpacketEventConfigService redpacketEventConfigService;

    /**
     * 查询活动配置列表
     */
    @PreAuthorize("@ss.hasPermi('redpacket:config:list')")
    @GetMapping("/list")
    public TableDataInfo list(RedpacketEventConfig redpacketEventConfig)
    {
        startPage();
        List<RedpacketEventConfig> list = redpacketEventConfigService.selectRedpacketEventConfigList(redpacketEventConfig);
        return getDataTable(list);
    }

    /**
     * 导出活动配置列表
     */
    @PreAuthorize("@ss.hasPermi('redpacket:config:export')")
    @Log(title = "活动配置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, RedpacketEventConfig redpacketEventConfig)
    {
        List<RedpacketEventConfig> list = redpacketEventConfigService.selectRedpacketEventConfigList(redpacketEventConfig);
        ExcelUtil<RedpacketEventConfig> util = new ExcelUtil<RedpacketEventConfig>(RedpacketEventConfig.class);
        util.exportExcel(response, list, "活动配置数据");
    }

    /**
     * 获取活动配置详细信息
     */
    @PreAuthorize("@ss.hasPermi('redpacket:config:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(redpacketEventConfigService.selectRedpacketEventConfigById(id));
    }

    /**
     * 新增活动配置
     */
    @PreAuthorize("@ss.hasPermi('redpacket:config:add')")
    @Log(title = "活动配置", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody RedpacketEventConfig redpacketEventConfig)
    {
        return toAjax(redpacketEventConfigService.insertRedpacketEventConfig(redpacketEventConfig));
    }

    /**
     * 修改活动配置
     */
    @PreAuthorize("@ss.hasPermi('redpacket:config:edit')")
    @Log(title = "活动配置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody RedpacketEventConfig redpacketEventConfig)
    {
        return toAjax(redpacketEventConfigService.updateRedpacketEventConfig(redpacketEventConfig));
    }

    /**
     * 删除活动配置
     */
    @PreAuthorize("@ss.hasPermi('redpacket:config:remove')")
    @Log(title = "活动配置", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(redpacketEventConfigService.deleteRedpacketEventConfigByIds(ids));
    }
}
