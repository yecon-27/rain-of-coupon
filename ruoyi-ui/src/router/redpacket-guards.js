/**
 * 红包雨路由守卫
 * 按照用户流程设计的权限控制和状态检查
 */
import store from '@/store'
import { Message } from 'element-ui'

/**
 * 红包雨路由守卫配置
 */
export const redpacketGuards = {
  // 需要登录的路由
  requireAuth: [
    '/loading',
    '/countdown', 
    '/redpacket',
    '/coupon'
  ],
  
  // 需要检查活动状态的路由
  requireActivity: [
    '/loading',
    '/countdown',
    '/redpacket'
  ],
  
  // 公开访问的路由（无需登录）
  publicRoutes: [
    '/rule'
  ]
}

/**
 * 检查用户是否已登录
 */
export const checkUserLogin = () => {
  return store.getters.token && store.getters.roles.length > 0
}

/**
 * 检查活动状态
 */
export const checkActivityStatus = async () => {
  try {
    await store.dispatch('lottery/checkActivity')
    const status = store.getters.lotteryActivityStatus
    
    switch (status) {
      case 0:
        return { valid: false, message: '红包雨活动尚未开始' }
      case 1:
        return { valid: true, message: '红包雨活动进行中' }
      case 2:
        return { valid: false, message: '红包雨活动已结束' }
      default:
        return { valid: false, message: '活动状态异常' }
    }
  } catch (error) {
    console.error('检查活动状态失败:', error)
    return { valid: false, message: '无法获取活动状态' }
  }
}

/**
 * 加载页面守卫 - 登录成功后的第一个页面
 */
export const beforeEnterLoading = async (to, from, next) => {
  // 1. 必须已登录
  if (!checkUserLogin()) {
    Message.warning('请先登录')
    next('/login')
    return
  }
  
  // 2. 检查用户是否已中奖
  try {
    const userStatus = await store.dispatch('lottery/fetchUserStatus')
    
    // 如果已中奖，不需要进入加载页面，直接显示中奖弹窗
    if (userStatus.hasWon) {
      // 跳转到首页并显示中奖弹窗
      next('/?showPrize=true')
      return
    }
    
    // 检查活动状态
    const activityCheck = await checkActivityStatus()
    if (!activityCheck.valid) {
      Message.warning(activityCheck.message)
      next('/rule')
      return
    }
    
  } catch (error) {
    console.error('检查用户状态失败:', error)
    Message.error('数据加载失败')
    next('/')
    return
  }
  
  next()
}

/**
 * 倒计时页面守卫
 */
export const beforeEnterCountdown = async (to, from, next) => {
  // 1. 必须已登录
  if (!checkUserLogin()) {
    Message.warning('请先登录')
    next('/login')
    return
  }
  
  // 2. 必须从加载页面跳转而来
  if (from.path !== '/loading') {
    Message.warning('请按流程参与活动')
    next('/loading')
    return
  }
  
  // 3. 检查活动状态
  const activityCheck = await checkActivityStatus()
  if (!activityCheck.valid) {
    Message.warning(activityCheck.message)
    next('/rule')
    return
  }
  
  next()
}

/**
 * 红包雨页面守卫
 */
export const beforeEnterRedPacket = async (to, from, next) => {
  // 1. 必须已登录
  if (!checkUserLogin()) {
    Message.warning('请先登录')
    next('/login')
    return
  }
  
  // 2. 必须从倒计时页面跳转而来
  if (from.path !== '/countdown') {
    Message.warning('请按流程参与活动')
    next('/loading')
    return
  }
  
  // 3. 检查活动状态和用户状态
  try {
    const activityCheck = await checkActivityStatus()
    if (!activityCheck.valid) {
      Message.warning(activityCheck.message)
      next('/rule')
      return
    }
    
    // 初始化红包雨数据
    await store.dispatch('lottery/initLotteryData')
    
  } catch (error) {
    console.error('初始化红包雨数据失败:', error)
    Message.error('数据加载失败，请刷新重试')
    next('/loading')
    return
  }
  
  next()
}

/**
 * 我的优惠券页面守卫
 */
export const beforeEnterCoupon = async (to, from, next) => {
  // 1. 必须已登录
  if (!checkUserLogin()) {
    Message.warning('请先登录查看优惠券')
    next(`/login?redirect=${encodeURIComponent(to.fullPath)}`)
    return
  }
  
  // 2. 预加载用户优惠券记录
  try {
    await store.dispatch('lottery/fetchUserRecords', { pageNum: 1, pageSize: 10 })
  } catch (error) {
    console.error('加载用户优惠券记录失败:', error)
  }
  
  next()
}

/**
 * 检查用户今日参与次数
 */
export const checkDailyLimit = async () => {
  try {
    const todayStats = await store.dispatch('lottery/fetchTodayStats')
    const dailyLimit = store.state.lottery.activityConfig.dailyLimit
    
    return {
      canParticipate: todayStats.drawCount < dailyLimit,
      remaining: dailyLimit - todayStats.drawCount,
      total: dailyLimit
    }
  } catch (error) {
    console.error('检查每日限制失败:', error)
    return { canParticipate: false, remaining: 0, total: 0 }
  }
}

/**
 * 检查用户是否有剩余次数
 */
export const checkRemainingCount = () => {
  const drawCount = store.getters.lotteryDrawCount
  return {
    hasRemaining: drawCount > 0,
    count: drawCount
  }
}