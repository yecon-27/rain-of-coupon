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
import com.ruoyi.redpacket.domain.RedpacketRules;
import com.ruoyi.redpacket.service.IRedpacketRulesService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 红包活动规则Controller
 * 
 * @author ruoyi
 * @date 2025-08-15
 */
@RestController
@RequestMapping("/redpacket/rules")
public class RedpacketRulesController extends BaseController
{
    @Autowired
    private IRedpacketRulesService redpacketRulesService;

    /**
     * 查询红包活动规则列表
     */
    @PreAuthorize("@ss.hasPermi('redpacket:rules:list')")
    @GetMapping("/list")
    public TableDataInfo list(RedpacketRules redpacketRules)
    {
        startPage();
        List<RedpacketRules> list = redpacketRulesService.selectRedpacketRulesList(redpacketRules);
        return getDataTable(list);
    }

    /**
     * 导出红包活动规则列表
     */
    @PreAuthorize("@ss.hasPermi('redpacket:rules:export')")
    @Log(title = "红包活动规则", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, RedpacketRules redpacketRules)
    {
        List<RedpacketRules> list = redpacketRulesService.selectRedpacketRulesList(redpacketRules);
        ExcelUtil<RedpacketRules> util = new ExcelUtil<RedpacketRules>(RedpacketRules.class);
        util.exportExcel(response, list, "红包活动规则数据");
    }

    /**
     * 获取红包活动规则详细信息
     */
    @PreAuthorize("@ss.hasPermi('redpacket:rules:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(redpacketRulesService.selectRedpacketRulesById(id));
    }

    /**
     * 新增红包活动规则
     */
    @PreAuthorize("@ss.hasPermi('redpacket:rules:add')")
    @Log(title = "红包活动规则", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody RedpacketRules redpacketRules)
    {
        return toAjax(redpacketRulesService.insertRedpacketRules(redpacketRules));
    }

    /**
     * 修改红包活动规则
     */
    @PreAuthorize("@ss.hasPermi('redpacket:rules:edit')")
    @Log(title = "红包活动规则", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody RedpacketRules redpacketRules)
    {
        return toAjax(redpacketRulesService.updateRedpacketRules(redpacketRules));
    }

    /**
     * 删除红包活动规则
     */
    @PreAuthorize("@ss.hasPermi('redpacket:rules:remove')")
    @Log(title = "红包活动规则", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(redpacketRulesService.deleteRedpacketRulesByIds(ids));
    }
}
