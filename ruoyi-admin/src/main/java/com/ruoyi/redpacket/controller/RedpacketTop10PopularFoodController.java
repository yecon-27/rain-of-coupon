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
import com.ruoyi.redpacket.domain.RedpacketTop10PopularFood;
import com.ruoyi.redpacket.service.IRedpacketTop10PopularFoodService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * TOP10网络人气特色美食Controller
 * 
 * @author ruoyi
 * @date 2025-08-06
 */
@RestController
@RequestMapping("/redpacket/top10Food")
public class RedpacketTop10PopularFoodController extends BaseController
{
    @Autowired
    private IRedpacketTop10PopularFoodService redpacketTop10PopularFoodService;

    /**
     * 查询TOP10网络人气特色美食列表
     */
    @PreAuthorize("@ss.hasPermi('redpacket:top10Food:list')")
    @GetMapping("/list")
    public TableDataInfo list(RedpacketTop10PopularFood redpacketTop10PopularFood)
    {
        startPage();
        List<RedpacketTop10PopularFood> list = redpacketTop10PopularFoodService.selectRedpacketTop10PopularFoodList(redpacketTop10PopularFood);
        return getDataTable(list);
    }

    /**
     * 导出TOP10网络人气特色美食列表
     */
    @PreAuthorize("@ss.hasPermi('redpacket:top10Food:export')")
    @Log(title = "TOP10网络人气特色美食", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, RedpacketTop10PopularFood redpacketTop10PopularFood)
    {
        List<RedpacketTop10PopularFood> list = redpacketTop10PopularFoodService.selectRedpacketTop10PopularFoodList(redpacketTop10PopularFood);
        ExcelUtil<RedpacketTop10PopularFood> util = new ExcelUtil<RedpacketTop10PopularFood>(RedpacketTop10PopularFood.class);
        util.exportExcel(response, list, "TOP10网络人气特色美食数据");
    }

    /**
     * 获取TOP10网络人气特色美食详细信息
     */
    @PreAuthorize("@ss.hasPermi('redpacket:top10Food:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(redpacketTop10PopularFoodService.selectRedpacketTop10PopularFoodById(id));
    }

    /**
     * 新增TOP10网络人气特色美食
     */
    @PreAuthorize("@ss.hasPermi('redpacket:top10Food:add')")
    @Log(title = "TOP10网络人气特色美食", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody RedpacketTop10PopularFood redpacketTop10PopularFood)
    {
        return toAjax(redpacketTop10PopularFoodService.insertRedpacketTop10PopularFood(redpacketTop10PopularFood));
    }

    /**
     * 修改TOP10网络人气特色美食
     */
    @PreAuthorize("@ss.hasPermi('redpacket:top10Food:edit')")
    @Log(title = "TOP10网络人气特色美食", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody RedpacketTop10PopularFood redpacketTop10PopularFood)
    {
        return toAjax(redpacketTop10PopularFoodService.updateRedpacketTop10PopularFood(redpacketTop10PopularFood));
    }

    /**
     * 删除TOP10网络人气特色美食
     */
    @PreAuthorize("@ss.hasPermi('redpacket:top10Food:remove')")
    @Log(title = "TOP10网络人气特色美食", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(redpacketTop10PopularFoodService.deleteRedpacketTop10PopularFoodByIds(ids));
    }
}
