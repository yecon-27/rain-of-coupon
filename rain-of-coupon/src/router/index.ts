import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
// 导入路由守卫
import { 
  beforeEnterLoading, 
  beforeEnterCountdown, 
  beforeEnterRedPacket, 
  beforeEnterCoupon,
  setupRedPacketGuards 
} from './redpacket-guards'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'home',
    component: () => import('@/views/HomePage.vue')
  },
  {
    path: '/login',
    name: 'login',
    component: () => import('@/views/LoginPage.vue')
  },
  {
    path: '/loading',
    name: 'loading',
    component: () => import('@/views/LoadingPage.vue'),
    meta: { requiresAuth: true },
    beforeEnter: beforeEnterLoading
  },
  {
    path: '/countdown',
    name: 'countdown',
    component: () => import('@/views/CountDownPage.vue'),
    meta: { requiresAuth: true },
    beforeEnter: beforeEnterCountdown
  },
  {
    path: '/redpacket',
    name: 'redpacket',
    component: () => import('@/views/RedPacketPage.vue'),
    meta: { requiresAuth: true, requiresActivity: true },
    beforeEnter: beforeEnterRedPacket
  },
  {
    path: '/rule',
    name: 'rule',
    component: () => import('@/views/RulePage.vue')
  },
  {
    path: '/coupon',
    name: 'coupon',
    component: () => import('@/views/CouponPage.vue'),
    meta: { requiresAuth: true },
    beforeEnter: beforeEnterCoupon
  }
]

const router = createRouter({
  history: createWebHistory('/'),
  routes
})

// 设置全局路由守卫
setupRedPacketGuards(router)

export default router