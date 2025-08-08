// 红包雨 Composable
import { computed } from 'vue'
import { useLotteryStore } from '@/stores/lottery'
import { lotteryService } from '@/services/lotteryService'

export function useLottery() {
  const store = useLotteryStore()

  // 状态
  const userStatus = computed(() => store.userStatus)
  const prizes = computed(() => store.prizes)
  const records = computed(() => store.records)
  const activityConfig = computed(() => store.activityConfig)
  const gameSession = computed(() => store.gameSession)
  const drawing = computed(() => store.drawing)
  const drawResult = computed(() => store.drawResult)
  const showResult = computed(() => store.showResult)
  const todayStats = computed(() => store.todayStats)

  // 计算属性
  const canDraw = computed(() => store.canDraw)
  const isActivityActive = computed(() => store.isActivityActive)
  const canDrawToday = computed(() => store.canDrawToday)
  const winningRecords = computed(() => store.winningRecords)

  // 方法
  const initLotteryData = () => lotteryService.initLotteryData()
  const fetchUserStatus = () => lotteryService.fetchUserStatus()
  const fetchPrizes = () => lotteryService.fetchPrizes()
  const fetchUserRecords = (params?: import('@/types/lottery').UserRecordsQuery) => lotteryService.fetchUserRecords(params)
  const fetchActivityConfig = () => lotteryService.fetchActivityConfig()
  const fetchTodayStats = () => lotteryService.fetchTodayStats()
  const checkActivity = () => lotteryService.checkActivity()
  const performDraw = (clickedCount?: number) => lotteryService.performDraw(clickedCount)
  const showDrawResult = (result: import('@/types/lottery').DrawResult) => lotteryService.showDrawResult(result)
  const hideDrawResult = () => lotteryService.hideDrawResult()
  const startGameSession = () => lotteryService.startGameSession()
  const clickRedPacket = () => lotteryService.clickRedPacket()
  const endGameSession = () => lotteryService.endGameSession()

  // 工具方法
  const getPrizeById = (id: string | number) => store.getPrizeById(id)

  return {
    // 状态
    userStatus,
    prizes,
    records,
    activityConfig,
    gameSession,
    drawing,
    drawResult,
    showResult,
    todayStats,

    // 计算属性
    canDraw,
    isActivityActive,
    canDrawToday,
    winningRecords,

    // 方法
    initLotteryData,
    fetchUserStatus,
    fetchPrizes,
    fetchUserRecords,
    fetchActivityConfig,
    fetchTodayStats,
    checkActivity,
    performDraw,
    showDrawResult,
    hideDrawResult,
    startGameSession,
    clickRedPacket,
    endGameSession,
    getPrizeById
  }
}