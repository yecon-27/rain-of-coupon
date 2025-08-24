import { ref, computed } from 'vue'
import { defineStore } from 'pinia'
import type { 
  UserStatus, 
  Prize, 
  RecordsState, 
  ActivityConfig, 
  GameSession, 
  DrawResult, 
  TodayStats,
  ParticipationRecord
} from '@/types/lottery'

// 红包雨抽奖状态管理
export const useLotteryStore = defineStore('lottery', () => {
  // 用户状态
  const userStatus = ref({
    canDraw: false,
    remainingCount: 3,
    todayDrawCount: 0,
    totalDrawCount: 0,
    maxDrawsPerDay: 3,
    isLogin: false,
    userId: null,
    hasEverWon: false,
    isCrowded: false,
    canEnterCountdown: false,
    participationHistory: [],
    todayParticipations: [],
    winRecords: [],
    canStillWin: true,
    canParticipateToday: true,
    todayClickedTotal: 0,
    avgClicksPerGame: 0
  })

  // 奖品信息
  const prizes = ref<Prize[]>([])
  const prizeMap = ref<Record<string, Prize>>({})

  // 参与记录
  const records = ref<RecordsState>({
    list: [],
    total: 0,
    pageNum: 1,
    pageSize: 10,
    loading: false
  })

  // 活动配置
  const activityConfig = ref<ActivityConfig>({
    id: null,
    name: '',
    startTime: null,
    endTime: null,
    status: 0, // 0-未开始 1-进行中 2-已结束
    dailyLimit: 3,
    description: '',
    rules: ''
  })

  // 红包雨游戏状态
  const gameSession = ref<GameSession>({
    sessionId: null,
    startTime: null,
    clickedCount: 0,
    gameStatus: 'idle',
    duration: 50000 // 50秒
  })

  // 抽奖状态
  const drawing = ref(false)
  const drawResult = ref<DrawResult | null>(null)
  const showResult = ref(false)

  // 今日统计
  const todayStats = ref<TodayStats>({
    drawCount: 0,
    winCount: 0,
    winRate: 0
  })

  // Computed
  const canDraw = computed(() => {
    return userStatus.value.canDraw && 
           userStatus.value.remainingCount > 0 && 
           activityConfig.value.status === 1 &&
           !drawing.value
  })

  const isActivityActive = computed(() => {
    return activityConfig.value.status === 1
  })

  const canDrawToday = computed(() => {
    return userStatus.value.todayDrawCount < activityConfig.value.dailyLimit
  })

  const winningRecords = computed(() => {
    return records.value.list.filter(record => record.prizeId && record.prizeId !== 0)
  })

  // Actions
  const setUserStatus = (status: Partial<typeof userStatus.value>) => {
    userStatus.value = { ...userStatus.value, ...status }
  }

  const setPrizes = (prizeList: Prize[]) => {
    prizes.value = prizeList
    // 创建奖品映射表
    prizeMap.value = prizeList.reduce((map, prize) => {
      map[prize.id] = prize
      return map
    }, {} as Record<string, Prize>)
  }

  const setRecords = (data: { list: ParticipationRecord[], total: number, pageNum: number, pageSize: number }) => {
    records.value.list = data.list
    records.value.total = data.total
    records.value.pageNum = data.pageNum
    records.value.pageSize = data.pageSize
  }

  const addRecord = (record: ParticipationRecord) => {
    records.value.list.unshift(record)
    records.value.total += 1
  }

  const setRecordsLoading = (loading: boolean) => {
    records.value.loading = loading
  }

  const setActivityConfig = (config: Partial<typeof activityConfig.value>) => {
    activityConfig.value = { ...activityConfig.value, ...config }
  }

  const setDrawing = (isDrawing: boolean) => {
    drawing.value = isDrawing
  }

  const setDrawResult = (result: DrawResult | null) => {
    drawResult.value = result
  }

  const setShowResult = (show: boolean) => {
    showResult.value = show
  }

  const updateDrawCount = (count: number) => {
    userStatus.value.remainingCount = count
  }

  const decreaseDrawCount = () => {
    if (userStatus.value.remainingCount > 0) {
      userStatus.value.remainingCount -= 1
      userStatus.value.todayDrawCount += 1
    }
  }

  const setTodayStats = (stats: Partial<typeof todayStats.value>) => {
    todayStats.value = { ...todayStats.value, ...stats }
  }

  // 游戏会话管理
  const startGameSession = () => {
    const sessionId = 'session_' + Date.now() + '_' + Math.random().toString(36).substring(2, 11)
    gameSession.value = {
      sessionId,
      startTime: new Date(),
      clickedCount: 0,
      gameStatus: 'playing',
      duration: 50000
    }
    return sessionId
  }

  const clickRedPacket = () => {
    if (gameSession.value.gameStatus === 'playing') {
      gameSession.value.clickedCount += 1
      return gameSession.value.clickedCount
    }
    return 0
  }

  const endGameSession = () => {
    gameSession.value.gameStatus = 'finished'
    return {
      sessionId: gameSession.value.sessionId,
      clickedCount: gameSession.value.clickedCount,
      duration: gameSession.value.startTime ? 
        Date.now() - new Date(gameSession.value.startTime).getTime() : 0
    }
  }

  const resetGameSession = () => {
    gameSession.value = {
      sessionId: null,
      startTime: null,
      clickedCount: 0,
      gameStatus: 'idle',
      duration: 50000
    }
  }

  const getPrizeById = (id: string | number) => {
    return prizeMap.value[id] || null
  }

  return {
    // State
    userStatus,
    prizes,
    prizeMap,
    records,
    activityConfig,
    gameSession,
    drawing,
    drawResult,
    showResult,
    todayStats,

    // Computed
    canDraw,
    isActivityActive,
    canDrawToday,
    winningRecords,

    // Actions
    setUserStatus,
    setPrizes,
    setRecords,
    addRecord,
    setRecordsLoading,
    setActivityConfig,
    setDrawing,
    setDrawResult,
    setShowResult,
    updateDrawCount,
    decreaseDrawCount,
    setTodayStats,
    startGameSession,
    clickRedPacket,
    endGameSession,
    resetGameSession,
    getPrizeById
  }
})