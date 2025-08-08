/**
 * 红包雨路由守卫
 * 控制用户在不同页面间的流转逻辑
 */

import type { NavigationGuardNext, RouteLocationNormalized, Router } from 'vue-router'
// 正确导入 Pinia store
import { useLotteryStore } from '@/stores/lottery'
import { getToken } from '@/utils/auth'

/**
 * 检查用户登录状态
 */
function checkAuth() {
  const token = getToken()
  return !!token
}

/**
 * 检查活动状态
 */
async function checkActivityStatus() {
  try {
    const lotteryStore = useLotteryStore()
    return lotteryStore.isActivityActive // 使用 computed 或 getter
  } catch (error) {
    console.error('检查活动状态失败:', error)
    return false
  }
}

/**
 * 检查用户参与资格
 */
async function checkUserEligibility() {
  try {
    const lotteryStore = useLotteryStore()
    const userStatus = lotteryStore.userStatus

    return {
      canDraw: userStatus.canDraw,
      hasEverWon: userStatus.hasEverWon,
      remainingCount: userStatus.remainingCount,
      isCrowded: userStatus.isCrowded,
    }
  } catch (error) {
    console.error('检查用户资格失败:', error)
    return {
      canDraw: false,
      hasEverWon: false,
      remainingCount: 0,
      isCrowded: true,
    }
  }
}

// 在全局守卫中也需要修改
export function setupRedPacketGuards(router: Router) {
  router.beforeEach(async (to, from, next) => {
    const redPacketPages = ['loading', 'countdown', 'redpacket', 'coupon']

    if (redPacketPages.includes(to.name as string) && checkAuth()) {
      try {
        const lotteryStore = useLotteryStore()
      } catch (error) {
        console.error('初始化红包雨数据失败:', error)
      }
    }

    next()
  })
}

/**
 * 加载页面路由守卫
 */
export async function beforeEnterLoading(
  to: RouteLocationNormalized,
  from: RouteLocationNormalized,
  next: NavigationGuardNext,
) {
  // 检查登录状态
  if (!checkAuth()) {
    next('/login')
    return
  }

  // 检查活动状态
  const isActive = await checkActivityStatus()
  if (!isActive) {
    next('/')
    return
  }

  // 检查用户资格
  const eligibility = await checkUserEligibility()

  // 如果已经中过奖，直接跳转到首页显示中奖弹窗
  if (eligibility.hasEverWon) {
    next('/')
    return
  }

  // 如果没有剩余次数，跳转到首页
  if (eligibility.remainingCount <= 0) {
    next('/')
    return
  }

  // 如果人数拥挤，停留在加载页面显示拥挤提示
  if (eligibility.isCrowded) {
    next()
    return
  }

  // 正常情况下，自动跳转到倒计时页面
  next('/countdown')
}

/**
 * 倒计时页面路由守卫
 */
export async function beforeEnterCountdown(
  to: RouteLocationNormalized,
  from: RouteLocationNormalized,
  next: NavigationGuardNext,
) {
  // 检查登录状态
  if (!checkAuth()) {
    next('/login')
    return
  }

  // 检查活动状态
  const isActive = await checkActivityStatus()
  if (!isActive) {
    next('/')
    return
  }

  // 检查用户资格
  const eligibility = await checkUserEligibility()

  // 如果已经中过奖或没有剩余次数，跳转到首页
  if (eligibility.hasEverWon || eligibility.remainingCount <= 0) {
    next('/')
    return
  }

  // 如果人数拥挤，跳转到加载页面
  if (eligibility.isCrowded) {
    next('/loading')
    return
  }

  // 正常进入倒计时页面
  next()
}

/**
 * 红包雨页面路由守卫
 */
export async function beforeEnterRedPacket(
  to: RouteLocationNormalized,
  from: RouteLocationNormalized,
  next: NavigationGuardNext,
) {
  // 检查登录状态
  if (!checkAuth()) {
    next('/login')
    return
  }

  // 检查活动状态
  const isActive = await checkActivityStatus()
  if (!isActive) {
    next('/')
    return
  }

  // 检查用户资格
  const eligibility = await checkUserEligibility()

  // 如果已经中过奖或没有剩余次数，跳转到首页
  if (eligibility.hasEverWon || eligibility.remainingCount <= 0) {
    next('/')
    return
  }

  // 如果人数拥挤，跳转到加载页面
  if (eligibility.isCrowded) {
    next('/loading')
    return
  }

  // 只允许从倒计时页面进入红包雨页面
  if (from.name !== 'countdown') {
    // 小写，与路由配置一致
    next('/countdown')
    return
  }

  // 正常进入红包雨页面
  next()
}

/**
 * 优惠券页面路由守卫
 */
export async function beforeEnterCoupon(
  to: RouteLocationNormalized,
  from: RouteLocationNormalized,
  next: NavigationGuardNext,
) {
  // 检查登录状态
  if (!checkAuth()) {
    next('/login')
    return
  }

  // 优惠券页面不需要检查活动状态，用户随时可以查看
  next()
}
