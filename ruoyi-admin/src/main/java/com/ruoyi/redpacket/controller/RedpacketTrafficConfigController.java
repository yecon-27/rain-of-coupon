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
import com.ruoyi.redpacket.domain.RedpacketTrafficConfig;
import com.ruoyi.redpacket.service.IRedpacketTrafficConfigService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 红包流量控制配置Controller
 * 
 * @author ruoyi
 * @date 2025-08-14
 */
@RestController
@RequestMapping("/redpacket/trafficConfig")
public class RedpacketTrafficConfigController extends BaseController
{
    @Autowired
    private IRedpacketTrafficConfigService redpacketTrafficConfigService;

    /**
     * 查询红包流量控制配置列表
     */
    @PreAuthorize("@ss.hasPermi('redpacket:trafficConfig:list')")
    @GetMapping("/list")
    public TableDataInfo list(RedpacketTrafficConfig redpacketTrafficConfig)
    {
        startPage();
        List<RedpacketTrafficConfig> list = redpacketTrafficConfigService.selectRedpacketTrafficConfigList(redpacketTrafficConfig);
        return getDataTable(list);
    }

    /**
     * 导出红包流量控制配置列表
     */
    @PreAuthorize("@ss.hasPermi('redpacket:trafficConfig:export')")
    @Log(title = "红包流量控制配置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, RedpacketTrafficConfig redpacketTrafficConfig)
    {
        List<RedpacketTrafficConfig> list = redpacketTrafficConfigService.selectRedpacketTrafficConfigList(redpacketTrafficConfig);
        ExcelUtil<RedpacketTrafficConfig> util = new ExcelUtil<RedpacketTrafficConfig>(RedpacketTrafficConfig.class);
        util.exportExcel(response, list, "红包流量控制配置数据");
    }

    /**
     * 获取红包流量控制配置详细信息
     */
    @PreAuthorize("@ss.hasPermi('redpacket:trafficConfig:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(redpacketTrafficConfigService.selectRedpacketTrafficConfigById(id));
    }

    /**
     * 新增红包流量控制配置
     */
    @PreAuthorize("@ss.hasPermi('redpacket:trafficConfig:add')")
    @Log(title = "红包流量控制配置", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody RedpacketTrafficConfig redpacketTrafficConfig)
    {
        return toAjax(redpacketTrafficConfigService.insertRedpacketTrafficConfig(redpacketTrafficConfig));
    }

    /**
     * 修改红包流量控制配置
     */
    @PreAuthorize("@ss.hasPermi('redpacket:trafficConfig:edit')")
    @Log(title = "红包流量控制配置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody RedpacketTrafficConfig redpacketTrafficConfig)
    {
        return toAjax(redpacketTrafficConfigService.updateRedpacketTrafficConfig(redpacketTrafficConfig));
    }

    /**
     * 删除红包流量控制配置
     */
    @PreAuthorize("@ss.hasPermi('redpacket:trafficConfig:remove')")
    @Log(title = "红包流量控制配置", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(redpacketTrafficConfigService.deleteRedpacketTrafficConfigByIds(ids));
    }
}
