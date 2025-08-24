// 红包雨业务逻辑服务层
import {
  drawLottery,
  getUserRecords,
  getPrizes,
  getUserStatus,
  getActivityConfig,
  getTodayStats,
  checkActivityStatus,
} from '@/api/lottery'
import { useLotteryStore } from '@/stores/lottery'
import type { UserRecordsQuery, DrawResult } from '@/types/lottery'

export class LotteryService {
  private store = useLotteryStore()

  // 初始化红包雨数据
  async initLotteryData() {
    try {
      await Promise.all([
        this.fetchUserStatus(),
        this.fetchPrizes(),
        this.fetchActivityConfig(),
        this.fetchTodayStats(),
      ])
    } catch (error) {
      console.error('初始化红包雨数据失败:', error)
      throw error
    }
  }

  // 获取用户状态
  async fetchUserStatus() {
    try {
      const data = await getUserStatus()
      // 转换API返回的数据格式以匹配store中的类型
      const userStatus = {
        canDraw: data.data.canDraw,
        hasEverWon: data.data.hasEverWon,
        isCrowded: data.data.isCrowded,
        remainingCount: data.data.remainingCount
      }
      this.store.setUserStatus(userStatus)
      return data
    } catch (error) {
      console.error('获取用户状态失败:', error)
      throw error
    }
  }

  // 获取奖品列表
  async fetchPrizes() {
    try {
      const data = await getPrizes()
      this.store.setPrizes(data)
      return data
    } catch (error) {
      console.error('获取奖品列表失败:', error)
      throw error
    }
  }

  // 获取用户记录
  async fetchUserRecords(params: UserRecordsQuery = {}) {
    try {
      this.store.setRecordsLoading(true)

      const query: UserRecordsQuery = {
        pageNum: params.pageNum || this.store.records.pageNum,
        pageSize: params.pageSize || this.store.records.pageSize,
        ...params,
      }

      const res = await getUserRecords(query)

      this.store.setRecords({
        list: res.rows,
        total: res.total,
        pageNum: query.pageNum || 1,
        pageSize: query.pageSize || 10,
      })

      return res
    } catch (error) {
      console.error('获取用户记录失败:', error)
      throw error
    } finally {
      this.store.setRecordsLoading(false)
    }
  }

  // 获取活动配置
  async fetchActivityConfig() {
    try {
      const data = await getActivityConfig()
      this.store.setActivityConfig(data)
      return data
    } catch (error) {
      console.error('获取活动配置失败:', error)
      throw error
    }
  }

  // 获取今日统计
  async fetchTodayStats() {
    try {
      const data = await getTodayStats()
      this.store.setTodayStats(data)
      return data
    } catch (error) {
      console.error('获取今日统计失败:', error)
      throw error
    }
  }

  // 检查活动状态
  async checkActivity() {
    try {
      const data = await checkActivityStatus()
      this.store.setActivityConfig({ status: data.status })
      return data
    } catch (error) {
      console.error('检查活动状态失败:', error)
      throw error
    }
  }

  // 执行红包雨抽奖
  async performDraw(clickedCount: number = 1) {
    if (this.store.drawing) {
      throw new Error('正在参与红包雨中，请稍候')
    }

    if (this.store.userStatus.remainingCount <= 0) {
      throw new Error('今日参与次数不足')
    }

    if (this.store.userStatus.hasEverWon) {
      throw new Error('您已经中过奖了，无法再次参与')
    }

    try {
      this.store.setDrawing(true)

      const res = await drawLottery({
        clickedCount,
        sessionId: this.store.gameSession.sessionId,
      })

      if (res.code === 200) {
        // 参与成功 - 转换数据类型
        const drawResult = {
          ...res.data,
          isWin: res.data.isWin ? 1 : 0 // 转换boolean为number
        }
        this.store.setDrawResult(drawResult)
        this.store.decreaseDrawCount()
        this.store.addRecord(drawResult)

        // 如果中奖了，更新用户状态
        if (res.data.isWin) {
          this.store.setUserStatus({ hasEverWon: true })
        }

        // 重置游戏会话
        this.store.resetGameSession()

        // 更新统计数据
        await this.fetchTodayStats()
        await this.fetchUserStatus()

        return res.data
      } else {
        throw new Error(res.msg || '参与红包雨失败')
      }
    } catch (error) {
      console.error('参与红包雨失败:', error)
      throw error
    } finally {
      this.store.setDrawing(false)
    }
  }

  // 显示抽奖结果
  showDrawResult(result: DrawResult) {
    this.store.setDrawResult(result)
    this.store.setShowResult(true)
  }

  // 隐藏抽奖结果
  hideDrawResult() {
    this.store.setShowResult(false)
    this.store.setDrawResult(null)
  }

  // 开始游戏会话
  startGameSession() {
    return this.store.startGameSession()
  }

  // 点击红包
  clickRedPacket() {
    return this.store.clickRedPacket()
  }

  // 结束游戏会话
  endGameSession() {
    return this.store.endGameSession()
  }
}

// 创建单例实例
export const lotteryService = new LotteryService()
