
## 1. 使用若依代码生成器
1. 在若依后台 -> 系统工具 -> 代码生成
2. 导入 `redpacket_rules` 表
3. 修改生成信息：
   - 生成模块名：redpacket
   - 生成包路径：com.ruoyi.redpacket
   - 生成功能名：规则管理
   - 作者：你的名字

## 2. 生成后需要修改的文件

### 2.1 Controller层 (RedpacketRulesController.java)
```java
// 添加获取前端规则显示的专用接口
@GetMapping("/display")
public AjaxResult getRulesForDisplay() {
    // 按rule_type分组返回规则
}

@GetMapping("/type/{ruleType}")
public AjaxResult getRulesByType(@PathVariable String ruleType) {
    // 根据规则类型获取规则
}
```

### 2.2 Service层 (IRedpacketRulesService.java)
```java
// 添加按类型查询的方法
List<RedpacketRules> selectRulesByType(String ruleType);

// 添加获取显示用规则的方法
Map<String, List<RedpacketRules>> selectRulesForDisplay();
```

### 2.3 Mapper层 (RedpacketRulesMapper.xml)
```xml
<!-- 按类型查询规则 -->
<select id="selectRulesByType" parameterType="String" resultMap="RedpacketRulesResult">
    <include refid="selectRedpacketRulesVo"/>
    where rule_type = #{ruleType} and status = '0'
    order by rule_order asc
</select>
```

## 3. 清理旧的配置表
1. 检查 `redpacket_event_config` 表的使用情况
2. 如果只用于流量控制，删除冲突字段
3. 如果完全不用，可以删除整个表和相关代码