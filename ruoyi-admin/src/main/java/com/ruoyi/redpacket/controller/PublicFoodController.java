package com.ruoyi.redpacket.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.redpacket.domain.RedpacketTop10PopularFood;
import com.ruoyi.redpacket.domain.RedpacketTownSpecialtyFood;
import com.ruoyi.redpacket.service.IRedpacketTop10PopularFoodService;
import com.ruoyi.redpacket.service.IRedpacketTownSpecialtyFoodService;

/**
 * 公开美食接口Controller（无需权限验证）
 */
@Anonymous
@RestController
@RequestMapping("/public/redpacket")
public class PublicFoodController extends BaseController
{
    @Autowired
    private IRedpacketTop10PopularFoodService redpacketTop10PopularFoodService;
    
    @Autowired
    private IRedpacketTownSpecialtyFoodService redpacketTownSpecialtyFoodService;

    /**
     * 查询TOP10网络人气特色美食列表（公开接口）
     */
    @GetMapping("/top10Food/list")
    public TableDataInfo getTop10FoodList()
    {
        startPage();
        List<RedpacketTop10PopularFood> list = redpacketTop10PopularFoodService.selectRedpacketTop10PopularFoodList(new RedpacketTop10PopularFood());
        return getDataTable(list);
    }

    /**
     * 查询"一镇一品"特色菜列表（公开接口）
     */
    @GetMapping("/townFood/list")
    public TableDataInfo getTownFoodList()
    {
        startPage();
        List<RedpacketTownSpecialtyFood> list = redpacketTownSpecialtyFoodService.selectRedpacketTownSpecialtyFoodList(new RedpacketTownSpecialtyFood());
        return getDataTable(list);
    }
}