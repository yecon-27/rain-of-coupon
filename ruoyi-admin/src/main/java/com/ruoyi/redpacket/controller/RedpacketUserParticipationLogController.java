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
import com.ruoyi.redpacket.domain.RedpacketUserParticipationLog;
import com.ruoyi.redpacket.service.IRedpacketUserParticipationLogService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 用户参与记录（记录所有参与行为）Controller
 * 
 * @author ruoyi
 * @date 2025-08-07
 */
@RestController
@RequestMapping("/redpacket/participationLog")
public class RedpacketUserParticipationLogController extends BaseController
{
    @Autowired
    private IRedpacketUserParticipationLogService redpacketUserParticipationLogService;

    /**
     * 查询用户参与记录（记录所有参与行为）列表
     */
    @PreAuthorize("@ss.hasPermi('redpacket:participationLog:list')")
    @GetMapping("/list")
    public TableDataInfo list(RedpacketUserParticipationLog redpacketUserParticipationLog)
    {
        startPage();
        List<RedpacketUserParticipationLog> list = redpacketUserParticipationLogService.selectRedpacketUserParticipationLogList(redpacketUserParticipationLog);
        return getDataTable(list);
    }

    /**
     * 导出用户参与记录（记录所有参与行为）列表
     */
    @PreAuthorize("@ss.hasPermi('redpacket:participationLog:export')")
    @Log(title = "用户参与记录（记录所有参与行为）", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, RedpacketUserParticipationLog redpacketUserParticipationLog)
    {
        List<RedpacketUserParticipationLog> list = redpacketUserParticipationLogService.selectRedpacketUserParticipationLogList(redpacketUserParticipationLog);
        ExcelUtil<RedpacketUserParticipationLog> util = new ExcelUtil<RedpacketUserParticipationLog>(RedpacketUserParticipationLog.class);
        util.exportExcel(response, list, "用户参与记录（记录所有参与行为）数据");
    }

    /**
     * 获取用户参与记录（记录所有参与行为）详细信息
     */
    @PreAuthorize("@ss.hasPermi('redpacket:participationLog:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(redpacketUserParticipationLogService.selectRedpacketUserParticipationLogById(id));
    }

    /**
     * 新增用户参与记录（记录所有参与行为）
     */
    @PreAuthorize("@ss.hasPermi('redpacket:participationLog:add')")
    @Log(title = "用户参与记录（记录所有参与行为）", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody RedpacketUserParticipationLog redpacketUserParticipationLog)
    {
        return toAjax(redpacketUserParticipationLogService.insertRedpacketUserParticipationLog(redpacketUserParticipationLog));
    }

    /**
     * 修改用户参与记录（记录所有参与行为）
     */
    @PreAuthorize("@ss.hasPermi('redpacket:participationLog:edit')")
    @Log(title = "用户参与记录（记录所有参与行为）", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody RedpacketUserParticipationLog redpacketUserParticipationLog)
    {
        return toAjax(redpacketUserParticipationLogService.updateRedpacketUserParticipationLog(redpacketUserParticipationLog));
    }

    /**
     * 删除用户参与记录（记录所有参与行为）
     */
    @PreAuthorize("@ss.hasPermi('redpacket:participationLog:remove')")
    @Log(title = "用户参与记录（记录所有参与行为）", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(redpacketUserParticipationLogService.deleteRedpacketUserParticipationLogByIds(ids));
    }
}
