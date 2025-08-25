// 在浏览器控制台中运行这个脚本来调试图像管理器
// 复制粘贴到浏览器开发者工具的控制台中

console.log('🔍 开始调试图像管理器...');

// 检查当前状态
if (window.imageManager) {
  console.log('📊 当前imageManager状态:', window.imageManager.getStatus());
  
  // 清除缓存
  console.log('🧹 清除缓存...');
  window.imageManager.clearCache();
  
  // 强制启用本地降级模式
  console.log('🔄 启用本地降级模式...');
  window.imageManager.enableLocalFallback();
  
  // 测试获取图片URL
  console.log('🖼️ 测试获取图片URL...');
  
  Promise.all([
    window.imageManager.getImageUrl('coupon_188'),
    window.imageManager.getImageUrl('coupon_588'),
    window.imageManager.getImageUrl('coupon_888')
  ]).then(urls => {
    console.log('✅ 获取到的图片URLs:');
    console.log('188元:', urls[0]);
    console.log('588元:', urls[1]);
    console.log('888元:', urls[2]);
  }).catch(error => {
    console.error('❌ 获取图片URL失败:', error);
  });
  
} else {
  console.error('❌ imageManager未找到，可能需要先导入');
  console.log('💡 尝试从gameStore获取:');
  
  if (window.gameStore) {
    console.log('📊 gameStore存在');
    // 可以尝试重新加载奖品记录
    window.gameStore.loadPrizeRecord().then(() => {
      console.log('✅ 重新加载奖品记录完成');
      console.log('🏆 当前奖品记录:', window.gameStore.prizeRecord);
    });
  }
}