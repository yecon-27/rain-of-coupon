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
import com.ruoyi.redpacket.domain.RedpacketPrize;
import com.ruoyi.redpacket.service.IRedpacketPrizeService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 奖品配置Controller
 * 
 * @author ruoyi
 * @date 2025-08-06
 */
@RestController
@RequestMapping("/redpacket/prize")
public class RedpacketPrizeController extends BaseController
{
    @Autowired
    private IRedpacketPrizeService redpacketPrizeService;

    /**
     * 查询奖品配置列表
     */
    @PreAuthorize("@ss.hasPermi('redpacket:prize:list')")
    @GetMapping("/list")
    public TableDataInfo list(RedpacketPrize redpacketPrize)
    {
        startPage();
        List<RedpacketPrize> list = redpacketPrizeService.selectRedpacketPrizeList(redpacketPrize);
        return getDataTable(list);
    }

    /**
     * 导出奖品配置列表
     */
    @PreAuthorize("@ss.hasPermi('redpacket:prize:export')")
    @Log(title = "奖品配置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, RedpacketPrize redpacketPrize)
    {
        List<RedpacketPrize> list = redpacketPrizeService.selectRedpacketPrizeList(redpacketPrize);
        ExcelUtil<RedpacketPrize> util = new ExcelUtil<RedpacketPrize>(RedpacketPrize.class);
        util.exportExcel(response, list, "奖品配置数据");
    }

    /**
     * 获取奖品配置详细信息
     */
    @PreAuthorize("@ss.hasPermi('redpacket:prize:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(redpacketPrizeService.selectRedpacketPrizeById(id));
    }

    /**
     * 新增奖品配置
     */
    @PreAuthorize("@ss.hasPermi('redpacket:prize:add')")
    @Log(title = "奖品配置", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody RedpacketPrize redpacketPrize)
    {
        return toAjax(redpacketPrizeService.insertRedpacketPrize(redpacketPrize));
    }

    /**
     * 修改奖品配置
     */
    @PreAuthorize("@ss.hasPermi('redpacket:prize:edit')")
    @Log(title = "奖品配置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody RedpacketPrize redpacketPrize)
    {
        return toAjax(redpacketPrizeService.updateRedpacketPrize(redpacketPrize));
    }

    /**
     * 删除奖品配置
     */
    @PreAuthorize("@ss.hasPermi('redpacket:prize:remove')")
    @Log(title = "奖品配置", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(redpacketPrizeService.deleteRedpacketPrizeByIds(ids));
    }
}
