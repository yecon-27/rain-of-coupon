/**
 * API使用示例
 * 展示如何在Vue组件中使用抽奖API接口
 */

// 方式1：单独导入需要的接口
import { drawLottery, getUserRecords, getDrawCount } from '@/api/redpacket/lottery'

// 方式2：统一导入所有接口
import * as lotteryApi from '@/api/redpacket'

// 在Vue组件中的使用示例
export default {
  name: 'LotteryExample',
  data() {
    return {
      drawCount: 0,
      userRecords: [],
      prizes: [],
      loading: false
    }
  },
  
  async mounted() {
    await this.initData()
  },
  
  methods: {
    // 初始化数据
    async initData() {
      try {
        // 获取用户剩余次数
        const countRes = await getDrawCount()
        this.drawCount = countRes.data
        
        // 获取奖品列表
        const prizesRes = await lotteryApi.getPrizes()
        this.prizes = prizesRes.data
        
        // 获取用户记录
        const recordsRes = await getUserRecords({ pageSize: 5 })
        this.userRecords = recordsRes.rows
        
      } catch (error) {
        console.error('初始化数据失败:', error)
        this.$modal.msgError('数据加载失败')
      }
    },
    
    // 执行抽奖
    async handleDraw() {
      if (this.loading) return
      if (this.drawCount <= 0) {
        this.$modal.msgWarning('抽奖次数不足')
        return
      }
      
      try {
        this.loading = true
        const result = await drawLottery()
        
        if (result.code === 200) {
          // 抽奖成功，显示结果
          this.$modal.msgSuccess(`恭喜获得：${result.data.prizeName}`)
          
          // 更新剩余次数
          this.drawCount--
          
          // 刷新记录
          await this.loadUserRecords()
          
        } else {
          this.$modal.msgError(result.msg || '抽奖失败')
        }
        
      } catch (error) {
        console.error('抽奖失败:', error)
        this.$modal.msgError('抽奖失败，请稍后重试')
      } finally {
        this.loading = false
      }
    },
    
    // 加载用户记录
    async loadUserRecords() {
      try {
        const res = await getUserRecords({ pageSize: 5 })
        this.userRecords = res.rows
      } catch (error) {
        console.error('加载记录失败:', error)
      }
    }
  }
}

// 错误处理示例
const handleApiError = (error, defaultMessage = '操作失败') => {
  if (error.response) {
    // 服务器返回错误状态码
    const { status, data } = error.response
    switch (status) {
      case 401:
        // 未授权，跳转登录
        router.push('/login')
        break
      case 403:
        // 无权限
        Message.error('无权限访问')
        break
      case 500:
        // 服务器错误
        Message.error(data.msg || '服务器错误')
        break
      default:
        Message.error(data.msg || defaultMessage)
    }
  } else if (error.request) {
    // 网络错误
    Message.error('网络连接失败')
  } else {
    // 其他错误
    Message.error(defaultMessage)
  }
}