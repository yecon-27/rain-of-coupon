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
import com.ruoyi.redpacket.domain.RedpacketTownSpecialtyFood;
import com.ruoyi.redpacket.service.IRedpacketTownSpecialtyFoodService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * "一镇一品"特色菜Controller
 * 
 * @author ruoyi
 * @date 2025-08-06
 */
@RestController
@RequestMapping("/redpacket/townFood")
public class RedpacketTownSpecialtyFoodController extends BaseController
{
    @Autowired
    private IRedpacketTownSpecialtyFoodService redpacketTownSpecialtyFoodService;

    /**
     * 查询"一镇一品"特色菜列表
     */
    @PreAuthorize("@ss.hasPermi('redpacket:townFood:list')")
    @GetMapping("/list")
    public TableDataInfo list(RedpacketTownSpecialtyFood redpacketTownSpecialtyFood)
    {
        startPage();
        List<RedpacketTownSpecialtyFood> list = redpacketTownSpecialtyFoodService.selectRedpacketTownSpecialtyFoodList(redpacketTownSpecialtyFood);
        return getDataTable(list);
    }

    /**
     * 导出"一镇一品"特色菜列表
     */
    @PreAuthorize("@ss.hasPermi('redpacket:townFood:export')")
    @Log(title = "'一镇一品'特色菜", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, RedpacketTownSpecialtyFood redpacketTownSpecialtyFood)
    {
        List<RedpacketTownSpecialtyFood> list = redpacketTownSpecialtyFoodService.selectRedpacketTownSpecialtyFoodList(redpacketTownSpecialtyFood);
        ExcelUtil<RedpacketTownSpecialtyFood> util = new ExcelUtil<RedpacketTownSpecialtyFood>(RedpacketTownSpecialtyFood.class);
        util.exportExcel(response, list, "'一镇一品'特色菜数据");
    }

    /**
     * 获取"一镇一品"特色菜详细信息
     */
    @PreAuthorize("@ss.hasPermi('redpacket:townFood:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(redpacketTownSpecialtyFoodService.selectRedpacketTownSpecialtyFoodById(id));
    }

    /**
     * 新增"一镇一品"特色菜
     */
    @PreAuthorize("@ss.hasPermi('redpacket:townFood:add')")
    @Log(title = "'一镇一品'特色菜", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody RedpacketTownSpecialtyFood redpacketTownSpecialtyFood)
    {
        return toAjax(redpacketTownSpecialtyFoodService.insertRedpacketTownSpecialtyFood(redpacketTownSpecialtyFood));
    }

    /**
     * 修改"一镇一品"特色菜
     */
    @PreAuthorize("@ss.hasPermi('redpacket:townFood:edit')")
    @Log(title = "'一镇一品'特色菜", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody RedpacketTownSpecialtyFood redpacketTownSpecialtyFood)
    {
        return toAjax(redpacketTownSpecialtyFoodService.updateRedpacketTownSpecialtyFood(redpacketTownSpecialtyFood));
    }

    /**
     * 删除"一镇一品"特色菜
     */
    @PreAuthorize("@ss.hasPermi('redpacket:townFood:remove')")
    @Log(title = "'一镇一品'特色菜", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(redpacketTownSpecialtyFoodService.deleteRedpacketTownSpecialtyFoodByIds(ids));
    }
}
