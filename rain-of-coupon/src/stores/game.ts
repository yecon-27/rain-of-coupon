import { ref } from 'vue'
import { defineStore } from 'pinia'

export interface GameResult {
  userId: string
  score: number
  collectedCount: number
  maxCombo: number
  playTime: string
  rewards: Reward[]
}

export interface Reward {
  id: string
  type: 'coupon' | 'points'
  name: string
  description: string
  image: string
  value: number
  expireDate?: string
  isUsed: boolean
}

export const useGameStore = defineStore('game', () => {
  // 状态
  const gameResults = ref<GameResult[]>([])
  const userRewards = ref<Reward[]>([])
  const loading = ref(false)

  // 保存游戏结果
  const saveGameResult = async (result: Omit<GameResult, 'playTime'>) => {
    loading.value = true
    try {
      const gameResult: GameResult = {
        ...result,
        playTime: new Date().toISOString()
      }
      
      // 模拟API调用保存到数据库
      await mockSaveGameResultAPI(gameResult)
      
      // 更新本地状态
      gameResults.value.push(gameResult)
      
      // 更新用户奖励
      userRewards.value.push(...gameResult.rewards)
      
      // 持久化到localStorage（模拟数据库）
      localStorage.setItem('gameResults', JSON.stringify(gameResults.value))
      localStorage.setItem('userRewards', JSON.stringify(userRewards.value))
      
      return { success: true }
    } catch (error) {
      console.error('保存游戏结果失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // 获取用户奖励
  const getUserRewards = async (userId: string) => {
    loading.value = true
    try {
      // 模拟API调用从数据库获取用户奖励
      const rewards = await mockGetUserRewardsAPI(userId)
      userRewards.value = rewards
      return rewards
    } catch (error) {
      console.error('获取用户奖励失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // 初始化数据（从localStorage恢复）
  const initializeData = () => {
    try {
      const storedResults = localStorage.getItem('gameResults')
      const storedRewards = localStorage.getItem('userRewards')
      
      if (storedResults) {
        gameResults.value = JSON.parse(storedResults)
      }
      
      if (storedRewards) {
        userRewards.value = JSON.parse(storedRewards)
      }
    } catch (error) {
      console.error('初始化游戏数据失败:', error)
    }
  }

  // 清除数据
  const clearData = () => {
    gameResults.value = []
    userRewards.value = []
    localStorage.removeItem('gameResults')
    localStorage.removeItem('userRewards')
  }

  return {
    // 状态
    gameResults,
    userRewards,
    loading,
    // 方法
    saveGameResult,
    getUserRewards,
    initializeData,
    clearData
  }
})

// 模拟API：保存游戏结果
async function mockSaveGameResultAPI(result: GameResult): Promise<void> {
  await new Promise(resolve => setTimeout(resolve, 500))
  console.log('游戏结果已保存到数据库:', result)
}

// 模拟API：获取用户奖励
async function mockGetUserRewardsAPI(userId: string): Promise<Reward[]> {
  await new Promise(resolve => setTimeout(resolve, 500))
  
  // 从localStorage获取数据（模拟数据库查询）
  const storedRewards = localStorage.getItem('userRewards')
  if (storedRewards) {
    const allRewards: Reward[] = JSON.parse(storedRewards)
    return allRewards // 在实际项目中这里会根据userId过滤
  }
  
  return []
}