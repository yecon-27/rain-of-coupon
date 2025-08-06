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
import com.ruoyi.redpacket.domain.RedpacketUserPrizeLog;
import com.ruoyi.redpacket.service.IRedpacketUserPrizeLogService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 用户抽奖记录Controller
 * 
 * @author ruoyi
 * @date 2025-08-06
 */
@RestController
@RequestMapping("/redpacket/log")
public class RedpacketUserPrizeLogController extends BaseController
{
    @Autowired
    private IRedpacketUserPrizeLogService redpacketUserPrizeLogService;

    /**
     * 查询用户抽奖记录列表
     */
    @PreAuthorize("@ss.hasPermi('redpacket:log:list')")
    @GetMapping("/list")
    public TableDataInfo list(RedpacketUserPrizeLog redpacketUserPrizeLog)
    {
        startPage();
        List<RedpacketUserPrizeLog> list = redpacketUserPrizeLogService.selectRedpacketUserPrizeLogList(redpacketUserPrizeLog);
        return getDataTable(list);
    }

    /**
     * 导出用户抽奖记录列表
     */
    @PreAuthorize("@ss.hasPermi('redpacket:log:export')")
    @Log(title = "用户抽奖记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, RedpacketUserPrizeLog redpacketUserPrizeLog)
    {
        List<RedpacketUserPrizeLog> list = redpacketUserPrizeLogService.selectRedpacketUserPrizeLogList(redpacketUserPrizeLog);
        ExcelUtil<RedpacketUserPrizeLog> util = new ExcelUtil<RedpacketUserPrizeLog>(RedpacketUserPrizeLog.class);
        util.exportExcel(response, list, "用户抽奖记录数据");
    }

    /**
     * 获取用户抽奖记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('redpacket:log:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(redpacketUserPrizeLogService.selectRedpacketUserPrizeLogById(id));
    }

    /**
     * 新增用户抽奖记录
     */
    @PreAuthorize("@ss.hasPermi('redpacket:log:add')")
    @Log(title = "用户抽奖记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody RedpacketUserPrizeLog redpacketUserPrizeLog)
    {
        return toAjax(redpacketUserPrizeLogService.insertRedpacketUserPrizeLog(redpacketUserPrizeLog));
    }

    /**
     * 修改用户抽奖记录
     */
    @PreAuthorize("@ss.hasPermi('redpacket:log:edit')")
    @Log(title = "用户抽奖记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody RedpacketUserPrizeLog redpacketUserPrizeLog)
    {
        return toAjax(redpacketUserPrizeLogService.updateRedpacketUserPrizeLog(redpacketUserPrizeLog));
    }

    /**
     * 删除用户抽奖记录
     */
    @PreAuthorize("@ss.hasPermi('redpacket:log:remove')")
    @Log(title = "用户抽奖记录", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(redpacketUserPrizeLogService.deleteRedpacketUserPrizeLogByIds(ids));
    }
}
