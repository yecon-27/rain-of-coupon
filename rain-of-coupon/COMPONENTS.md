# 组件结构说明

## HomePage 组件拆分

原来的 `HomePage.vue` 已经被拆分成以下几个独立的组件：

### 1. ActivitySection.vue
- **功能**: 参与活动区域（第一部分）
- **包含**: 
  - 背景图片 (home.png)
  - 右侧按钮组（规则按钮、券包按钮）
  - 底部居中的立即挑战按钮（带呼吸动效）
- **事件**: 
  - `@show-rules` - 显示规则
  - `@my-coupons` - 我的优惠券
  - `@join-activity` - 加入活动

### 2. FoodDisplaySection.vue
- **功能**: 展示菜品区域（第二部分）
- **包含**: 
  - 展示菜品图片 (zscp.png)
- **特点**: 简单的图片展示组件

### 3. FoodListSection.vue
- **功能**: 美食列表区域（第三、四部分复用）
- **Props**:
  - `title` - 列表标题
  - `foodItems` - 美食数据数组
  - `sectionClass` - 样式类名（可选）
- **特点**: 
  - **左右两列网格布局** - 桌面端自动排列成两列
  - 第四部分（town-section）第一个菜品独占一行
  - 移动端自动切换为单列布局
  - 红色背景 + 米黄色内容区

### 4. HomePage.vue (重构后)
- **功能**: 主页面容器
- **包含**: 
  - 组合上述三个组件
  - 数据获取逻辑
  - 事件处理逻辑
  - 蒙版层
- **样式**: 只保留最基础的页面样式

## 布局特点

### 第三、四部分列表布局：
- **桌面端**: 自动左右两列排列
- **第四部分特殊**: 第一个菜品独占整行，其余菜品左右两列排列
- **移动端**: 自动切换为单列布局
- **响应式**: 完整的多断点适配

## 使用方式

```vue
<template>
  <div class="home-page">
    <ActivitySection 
      @show-rules="showRules"
      @my-coupons="myCoupons"
      @join-activity="joinActivity"
    />
    
    <FoodDisplaySection />
    
    <FoodListSection 
      title="2024年网络人气票选特色美食TOP10"
      :food-items="top10Foods"
      section-class="top10-section"
    />
    
    <FoodListSection 
      title="2024年网络人气票选"一镇一品"特色菜"
      :food-items="townFoods"
      section-class="town-section"
    />
  </div>
</template>
```