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
import com.ruoyi.redpacket.domain.RedpacketTrafficStats;
import com.ruoyi.redpacket.service.IRedpacketTrafficStatsService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 红包流量统计Controller
 * 
 * @author ruoyi
 * @date 2025-08-14
 */
@RestController
@RequestMapping("/redpacket/trafficStats")
public class RedpacketTrafficStatsController extends BaseController
{
    @Autowired
    private IRedpacketTrafficStatsService redpacketTrafficStatsService;

    /**
     * 查询红包流量统计列表
     */
    @PreAuthorize("@ss.hasPermi('redpacket:trafficStats:list')")
    @GetMapping("/list")
    public TableDataInfo list(RedpacketTrafficStats redpacketTrafficStats)
    {
        startPage();
        List<RedpacketTrafficStats> list = redpacketTrafficStatsService.selectRedpacketTrafficStatsList(redpacketTrafficStats);
        return getDataTable(list);
    }

    /**
     * 导出红包流量统计列表
     */
    @PreAuthorize("@ss.hasPermi('redpacket:trafficStats:export')")
    @Log(title = "红包流量统计", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, RedpacketTrafficStats redpacketTrafficStats)
    {
        List<RedpacketTrafficStats> list = redpacketTrafficStatsService.selectRedpacketTrafficStatsList(redpacketTrafficStats);
        ExcelUtil<RedpacketTrafficStats> util = new ExcelUtil<RedpacketTrafficStats>(RedpacketTrafficStats.class);
        util.exportExcel(response, list, "红包流量统计数据");
    }

    /**
     * 获取红包流量统计详细信息
     */
    @PreAuthorize("@ss.hasPermi('redpacket:trafficStats:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(redpacketTrafficStatsService.selectRedpacketTrafficStatsById(id));
    }

    /**
     * 新增红包流量统计
     */
    @PreAuthorize("@ss.hasPermi('redpacket:trafficStats:add')")
    @Log(title = "红包流量统计", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody RedpacketTrafficStats redpacketTrafficStats)
    {
        return toAjax(redpacketTrafficStatsService.insertRedpacketTrafficStats(redpacketTrafficStats));
    }

    /**
     * 修改红包流量统计
     */
    @PreAuthorize("@ss.hasPermi('redpacket:trafficStats:edit')")
    @Log(title = "红包流量统计", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody RedpacketTrafficStats redpacketTrafficStats)
    {
        return toAjax(redpacketTrafficStatsService.updateRedpacketTrafficStats(redpacketTrafficStats));
    }

    /**
     * 删除红包流量统计
     */
    @PreAuthorize("@ss.hasPermi('redpacket:trafficStats:remove')")
    @Log(title = "红包流量统计", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(redpacketTrafficStatsService.deleteRedpacketTrafficStatsByIds(ids));
    }
}
