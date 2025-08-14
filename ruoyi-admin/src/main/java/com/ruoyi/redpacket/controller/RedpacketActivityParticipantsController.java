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
import com.ruoyi.redpacket.domain.RedpacketActivityParticipants;
import com.ruoyi.redpacket.service.IRedpacketActivityParticipantsService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 红包活动参与者记录Controller
 * 
 * @author ruoyi
 * @date 2025-08-14
 */
@RestController
@RequestMapping("/redpacket/participantsTraffic")
public class RedpacketActivityParticipantsController extends BaseController
{
    @Autowired
    private IRedpacketActivityParticipantsService redpacketActivityParticipantsService;

    /**
     * 查询红包活动参与者记录列表
     */
    @PreAuthorize("@ss.hasPermi('redpacket:participantsTraffic:list')")
    @GetMapping("/list")
    public TableDataInfo list(RedpacketActivityParticipants redpacketActivityParticipants)
    {
        startPage();
        List<RedpacketActivityParticipants> list = redpacketActivityParticipantsService.selectRedpacketActivityParticipantsList(redpacketActivityParticipants);
        return getDataTable(list);
    }

    /**
     * 导出红包活动参与者记录列表
     */
    @PreAuthorize("@ss.hasPermi('redpacket:participantsTraffic:export')")
    @Log(title = "红包活动参与者记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, RedpacketActivityParticipants redpacketActivityParticipants)
    {
        List<RedpacketActivityParticipants> list = redpacketActivityParticipantsService.selectRedpacketActivityParticipantsList(redpacketActivityParticipants);
        ExcelUtil<RedpacketActivityParticipants> util = new ExcelUtil<RedpacketActivityParticipants>(RedpacketActivityParticipants.class);
        util.exportExcel(response, list, "红包活动参与者记录数据");
    }

    /**
     * 获取红包活动参与者记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('redpacket:participantsTraffic:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(redpacketActivityParticipantsService.selectRedpacketActivityParticipantsById(id));
    }

    /**
     * 新增红包活动参与者记录
     */
    @PreAuthorize("@ss.hasPermi('redpacket:participantsTraffic:add')")
    @Log(title = "红包活动参与者记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody RedpacketActivityParticipants redpacketActivityParticipants)
    {
        return toAjax(redpacketActivityParticipantsService.insertRedpacketActivityParticipants(redpacketActivityParticipants));
    }

    /**
     * 修改红包活动参与者记录
     */
    @PreAuthorize("@ss.hasPermi('redpacket:participantsTraffic:edit')")
    @Log(title = "红包活动参与者记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody RedpacketActivityParticipants redpacketActivityParticipants)
    {
        return toAjax(redpacketActivityParticipantsService.updateRedpacketActivityParticipants(redpacketActivityParticipants));
    }

    /**
     * 删除红包活动参与者记录
     */
    @PreAuthorize("@ss.hasPermi('redpacket:participantsTraffic:remove')")
    @Log(title = "红包活动参与者记录", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(redpacketActivityParticipantsService.deleteRedpacketActivityParticipantsByIds(ids));
    }
}
