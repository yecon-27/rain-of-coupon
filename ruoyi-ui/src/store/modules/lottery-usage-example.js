/**
 * 抽奖状态管理使用示例
 * 展示如何在Vue组件中使用lottery store模块
 */

// 在Vue组件中使用示例
export default {
  name: 'LotteryComponent',
  
  computed: {
    // 方式1：使用mapState映射状态
    ...mapState('lottery', [
      'userStatus',
      'prizes',
      'records',
      'activityConfig',
      'drawing',
      'drawResult',
      'showResult'
    ]),
    
    // 方式2：使用mapGetters映射计算属性
    ...mapGetters('lottery', [
      'canDraw',
      'isActivityActive',
      'canDrawToday',
      'winningRecords'
    ]),
    
    // 方式3：直接访问全局getters
    drawCount() {
      return this.$store.getters.lotteryDrawCount
    },
    
    // 自定义计算属性
    drawProgress() {
      const { todayDrawCount, dailyLimit } = this.activityConfig
      return Math.round((todayDrawCount / dailyLimit) * 100)
    }
  },
  
  async mounted() {
    // 初始化数据
    await this.initData()
  },
  
  methods: {
    // 方式1：使用mapActions映射方法
    ...mapActions('lottery', [
      'initLotteryData',
      'fetchUserStatus',
      'fetchUserRecords',
      'performDraw',
      'showDrawResult',
      'hideDrawResult'
    ]),
    
    // 初始化数据
    async initData() {
      try {
        this.$loading.show('加载中...')
        await this.initLotteryData()
      } catch (error) {
        this.$modal.msgError('数据加载失败')
      } finally {
        this.$loading.hide()
      }
    },
    
    // 执行抽奖
    async handleDraw() {
      if (!this.canDraw) {
        this.$modal.msgWarning('当前无法抽奖')
        return
      }
      
      try {
        const result = await this.performDraw()
        
        // 显示抽奖结果
        this.showDrawResult(result)
        
        // 播放动画效果
        this.playDrawAnimation(result)
        
      } catch (error) {
        this.$modal.msgError(error.message || '抽奖失败')
      }
    },
    
    // 播放抽奖动画
    playDrawAnimation(result) {
      // 转盘动画逻辑
      this.$refs.wheel.startSpin(result.prizeId)
    },
    
    // 关闭结果弹窗
    closeResult() {
      this.hideDrawResult()
    },
    
    // 刷新用户状态
    async refreshUserStatus() {
      try {
        await this.fetchUserStatus()
      } catch (error) {
        console.error('刷新用户状态失败:', error)
      }
    },
    
    // 加载更多记录
    async loadMoreRecords() {
      try {
        const nextPage = this.records.pageNum + 1
        await this.fetchUserRecords({ pageNum: nextPage })
      } catch (error) {
        this.$modal.msgError('加载记录失败')
      }
    }
  },
  
  watch: {
    // 监听抽奖次数变化
    'userStatus.drawCount'(newCount, oldCount) {
      if (newCount < oldCount) {
        this.$message.success(`剩余抽奖次数：${newCount}`)
      }
    },
    
    // 监听活动状态变化
    'activityConfig.status'(newStatus) {
      switch (newStatus) {
        case 0:
          this.$modal.msgWarning('活动尚未开始')
          break
        case 2:
          this.$modal.msgWarning('活动已结束')
          break
      }
    }
  }
}

// 在非组件文件中使用store
import store from '@/store'

// 直接调用actions
const performLottery = async () => {
  try {
    const result = await store.dispatch('lottery/performDraw')
    console.log('抽奖结果:', result)
  } catch (error) {
    console.error('抽奖失败:', error)
  }
}

// 直接访问state
const getUserDrawCount = () => {
  return store.state.lottery.userStatus.drawCount
}

// 直接访问getters
const canUserDraw = () => {
  return store.getters['lottery/canDraw']
}

// 监听状态变化
store.watch(
  state => state.lottery.userStatus.drawCount,
  (newCount, oldCount) => {
    console.log(`抽奖次数从 ${oldCount} 变为 ${newCount}`)
  }
)

// 订阅mutations
store.subscribe((mutation, state) => {
  if (mutation.type === 'lottery/SET_DRAW_RESULT') {
    console.log('抽奖结果更新:', mutation.payload)
  }
})

// 在路由守卫中使用
router.beforeEach(async (to, from, next) => {
  if (to.path === '/lottery') {
    // 进入抽奖页面前检查活动状态
    try {
      await store.dispatch('lottery/checkActivity')
      const isActive = store.getters['lottery/isActivityActive']
      
      if (!isActive) {
        next('/activity-closed')
        return
      }
    } catch (error) {
      console.error('检查活动状态失败:', error)
    }
  }
  next()
})