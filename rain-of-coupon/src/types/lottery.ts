// 红包雨相关类型定义

export interface Prize {
  id: number
  prizeName: string
  totalCount: number
  remainingCount: number
  probability: number
}

export interface ParticipationRecord {
  id: number
  userId: number
  participationTime: string
  ipAddress: string
  isWin: number
  prizeId: number | null
  prizeName: string | null
  isUsed: number
}

export interface UserStatus {
  canDraw: boolean
  remainingCount: number
  todayDrawCount: number
  totalDrawCount: number
  maxDrawsPerDay: number
  isLogin: boolean
  userId: number | null
  hasEverWon: boolean
  isCrowded: boolean
  canEnterCountdown: boolean
  participationHistory: ParticipationRecord[]
  todayParticipations: ParticipationRecord[]
  winRecords: ParticipationRecord[]
  canStillWin: boolean
  canParticipateToday: boolean
  todayClickedTotal: number
  avgClicksPerGame: number
}

export interface ActivityConfig {
  id: number | null
  name: string
  startTime: string | null
  endTime: string | null
  status: number // 0-未开始 1-进行中 2-已结束
  dailyLimit: number
  description: string
  rules: string
}

export interface GameSession {
  sessionId: string | null
  startTime: Date | null
  clickedCount: number
  gameStatus: 'idle' | 'playing' | 'finished'
  duration: number
}

export interface DrawResult {
  id: number
  userId: number
  participationTime: string
  ipAddress: string
  isWin: number
  prizeId: number | null
  prizeName: string | null
  isUsed: number
}

export interface TodayStats {
  drawCount: number
  winCount: number
  winRate: number
}

export interface RecordsState {
  list: ParticipationRecord[]
  total: number
  pageNum: number
  pageSize: number
  loading: boolean
}

export interface UserRecordsQuery {
  pageNum?: number
  pageSize?: number
  isWin?: number | null
  isUsed?: number | null
  startDate?: string | null
  endDate?: string | null
}