/**
 * 红包抽奖模块API统一导出
 * 方便组件中统一引入使用
 */

// 抽奖相关接口
export {
  drawLottery,
  getUserRecords,
  getDrawCount,
  getPrizes,
  getUserStatus,
  getActivityConfig,
  getWinningAnnouncements,
  claimPrize,
  getTodayStats,
  checkActivityStatus
} from './lottery'

// 管理端接口（如需要）
export {
  listConfig,
  getConfig,
  addConfig,
  updateConfig,
  delConfig
} from './config'

export {
  listLog,
  getLog,
  addLog,
  updateLog,
  delLog
} from './log'

export {
  listPrize,
  getPrize,
  addPrize,
  updatePrize,
  delPrize
} from './prize'