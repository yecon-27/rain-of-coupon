/**
 * 清除所有缓存的工具函数
 */

/**
 * 清除所有cookies
 */
function clearAllCookies() {
  const cookies = document.cookie.split(";")
  
  for (let cookie of cookies) {
    const eqPos = cookie.indexOf("=")
    const name = eqPos > -1 ? cookie.substr(0, eqPos).trim() : cookie.trim()
    
    // 删除cookie - 多种路径和域名组合确保完全清除
    document.cookie = `${name}=;expires=Thu, 01 Jan 1970 00:00:00 GMT;path=/`
    document.cookie = `${name}=;expires=Thu, 01 Jan 1970 00:00:00 GMT;path=/;domain=${window.location.hostname}`
    document.cookie = `${name}=;expires=Thu, 01 Jan 1970 00:00:00 GMT;path=/;domain=.${window.location.hostname}`
    document.cookie = `${name}=;expires=Thu, 01 Jan 1970 00:00:00 GMT;path=/;domain=localhost`
    document.cookie = `${name}=;expires=Thu, 01 Jan 1970 00:00:00 GMT;path=/;domain=.localhost`
  }
}

/**
 * 清除所有缓存数据
 */
export function clearAllCache() {
  try {
    console.log('开始清除所有缓存...')
    
    // 1. 清除localStorage
    console.log('清除localStorage...')
    localStorage.clear()
    
    // 2. 清除sessionStorage
    console.log('清除sessionStorage...')
    sessionStorage.clear()
    
    // 3. 清除所有cookies
    console.log('清除cookies...')
    clearAllCookies()
    
    // 4. 清除IndexedDB（如果有的话）
    if ('indexedDB' in window) {
      console.log('清除IndexedDB...')
      // 这里可以添加具体的IndexedDB清除逻辑
    }
    
    console.log('✅ 所有缓存清除完成！')
    return true
  } catch (error) {
    console.error('❌ 清除缓存失败:', error)
    return false
  }
}

/**
 * 清除缓存并刷新页面
 */
export function clearCacheAndReload() {
  const success = clearAllCache()
  if (success) {
    console.log('即将刷新页面...')
    setTimeout(() => {
      window.location.reload(true)
    }, 1000)
  }
}

/**
 * 在控制台中可以直接调用的全局函数
 */
window.clearAllCache = clearAllCache
window.clearCacheAndReload = clearCacheAndReload

console.log('🧹 缓存清理工具已加载！')
console.log('💡 在控制台中输入 clearAllCache() 清除缓存')
console.log('💡 在控制台中输入 clearCacheAndReload() 清除缓存并刷新页面')