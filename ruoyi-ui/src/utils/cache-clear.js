/**
 * 缓存清理工具
 */

// 应用版本号，每次更新时修改此版本号
const APP_VERSION = '1.0.0'
const VERSION_KEY = 'app_version'

/**
 * 检查版本并清除缓存
 */
export function checkVersionAndClearCache() {
  const currentVersion = localStorage.getItem(VERSION_KEY)
  
  if (currentVersion !== APP_VERSION) {
    console.log('检测到版本更新，清除缓存...')
    clearAllCache()
    localStorage.setItem(VERSION_KEY, APP_VERSION)
  }
}

/**
 * 清除所有缓存
 */
export function clearAllCache() {
  try {
    // 清除 localStorage
    const preserveKeys = [VERSION_KEY] // 保留的key
    const keys = Object.keys(localStorage)
    keys.forEach(key => {
      if (!preserveKeys.includes(key)) {
        localStorage.removeItem(key)
      }
    })
    
    // 清除 sessionStorage
    sessionStorage.clear()
    
    // 清除 cookies
    clearCookies()
    
    console.log('缓存清理完成')
  } catch (error) {
    console.error('清除缓存失败:', error)
  }
}

/**
 * 清除所有cookies
 */
export function clearCookies() {
  const cookies = document.cookie.split(";")
  
  for (let cookie of cookies) {
    const eqPos = cookie.indexOf("=")
    const name = eqPos > -1 ? cookie.substr(0, eqPos).trim() : cookie.trim()
    
    // 删除cookie
    document.cookie = `${name}=;expires=Thu, 01 Jan 1970 00:00:00 GMT;path=/`
    document.cookie = `${name}=;expires=Thu, 01 Jan 1970 00:00:00 GMT;path=/;domain=${window.location.hostname}`
    document.cookie = `${name}=;expires=Thu, 01 Jan 1970 00:00:00 GMT;path=/;domain=.${window.location.hostname}`
  }
}

/**
 * 开发环境自动清除缓存
 */
export function devClearCache() {
  if (process.env.NODE_ENV === 'development') {
    const lastClearTime = localStorage.getItem('last_clear_time')
    const now = Date.now()
    const oneHour = 60 * 60 * 1000 // 1小时
    
    // 每小时自动清除一次缓存
    if (!lastClearTime || (now - parseInt(lastClearTime)) > oneHour) {
      console.log('开发环境自动清除缓存...')
      clearAllCache()
      localStorage.setItem('last_clear_time', now.toString())
    }
  }
}

/**
 * 手动清除缓存并刷新页面
 */
export function manualClearCacheAndReload() {
  clearAllCache()
  window.location.reload(true) // 强制刷新
}