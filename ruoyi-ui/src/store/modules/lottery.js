import {
  drawLottery,
  getUserRecords,
  getDrawCount,
  getPrizes,
  getUserStatus,
  getActivityConfig,
  getWinningAnnouncements,
  getTodayStats,
  checkActivityStatus
} from '@/api/redpacket/lottery'

/**
 * 红包雨状态管理模块
 * 管理用户红包雨优惠券相关的所有状态和业务逻辑
 */
const lottery = {
  namespaced: true,
  
  state: {
    // 用户红包雨状态 - 混合规则：一共只能中一次 + 每天三次参与机会
    userStatus: {
      canDraw: false,           // 是否可以参与红包雨（根据活动状态和参与次数计算）
      remainingCount: null,     // 今日剩余参与次数（从后端计算：maxDrawsPerDay - todayDrawCount）
      todayDrawCount: null,     // 今日已参与次数（从数据库统计）
      totalDrawCount: null,     // 历史总参与次数（从数据库统计）
      maxDrawsPerDay: null,     // 每日最大参与次数（从redpacket_event_config表读取，固定为3）
      isLogin: false,           // 是否已登录
      userId: null,             // 用户ID
      hasEverWon: false,        // 是否曾经中过奖（混合规则的关键字段）
      winRecord: null,          // 中奖记录（如果有的话，最多只有一条）
      isCrowded: false,         // 当前是否拥挤（根据max_users和当前在线用户数计算）
      canEnterCountdown: false, // 是否可以进入倒计时
      participationHistory: [], // 用户参与历史记录（按日期分组）
      canStillWin: true,        // 是否还能中奖（!hasEverWon）
      canParticipateToday: true // 今日是否还能参与（remainingCount > 0）
    },
    
    // 优惠券信息
    prizes: [],                 // 优惠券列表
    prizeMap: {},              // 优惠券映射表（便于快速查找）
    
    // 领取记录
    records: {
      list: [],                // 记录列表
      total: 0,                // 总记录数
      pageNum: 1,              // 当前页码
      pageSize: 10,            // 每页大小
      loading: false           // 加载状态
    },
    
    // 活动配置
    activityConfig: {
      id: null,                // 活动ID
      name: '',                // 活动名称
      startTime: null,         // 开始时间
      endTime: null,           // 结束时间
      status: 0,               // 活动状态 0-未开始 1-进行中 2-已结束
      dailyLimit: 3,           // 每日限制次数
      description: '',         // 活动描述
      rules: ''                // 活动规则
    },
    
    // 领取公告
    announcements: [],         // 领取公告列表
    
    // 红包雨相关状态
    drawing: false,            // 是否正在参与红包雨
    drawResult: null,          // 领取结果
    showResult: false,         // 是否显示结果弹窗
    
    // 今日统计
    todayStats: {
      drawCount: 0,            // 今日参与次数
      winCount: 0,             // 今日获得优惠券次数
      winRate: 0               // 今日获得率
    }
  },

  mutations: {
    // 设置用户状态
    SET_USER_STATUS: (state, status) => {
      state.userStatus = { ...state.userStatus, ...status }
    },
    
    // 设置奖品列表
    SET_PRIZES: (state, prizes) => {
      state.prizes = prizes
      // 创建奖品映射表
      state.prizeMap = prizes.reduce((map, prize) => {
        map[prize.id] = prize
        return map
      }, {})
    },
    
    // 设置抽奖记录
    SET_RECORDS: (state, { list, total, pageNum, pageSize }) => {
      state.records.list = list
      state.records.total = total
      state.records.pageNum = pageNum
      state.records.pageSize = pageSize
    },
    
    // 添加新记录到列表开头
    ADD_RECORD: (state, record) => {
      state.records.list.unshift(record)
      state.records.total += 1
    },
    
    // 设置记录加载状态
    SET_RECORDS_LOADING: (state, loading) => {
      state.records.loading = loading
    },
    
    // 设置活动配置
    SET_ACTIVITY_CONFIG: (state, config) => {
      state.activityConfig = { ...state.activityConfig, ...config }
    },
    
    // 设置中奖公告
    SET_ANNOUNCEMENTS: (state, announcements) => {
      state.announcements = announcements
    },
    
    // 设置抽奖状态
    SET_DRAWING: (state, drawing) => {
      state.drawing = drawing
    },
    
    // 设置抽奖结果
    SET_DRAW_RESULT: (state, result) => {
      state.drawResult = result
    },
    
    // 显示/隐藏结果弹窗
    SET_SHOW_RESULT: (state, show) => {
      state.showResult = show
    },
    
    // 更新剩余次数
    UPDATE_DRAW_COUNT: (state, count) => {
      state.userStatus.drawCount = count
    },
    
    // 减少抽奖次数
    DECREASE_DRAW_COUNT: (state) => {
      if (state.userStatus.drawCount > 0) {
        state.userStatus.drawCount -= 1
        state.userStatus.todayDrawCount += 1
      }
    },
    
    // 设置今日统计
    SET_TODAY_STATS: (state, stats) => {
      state.todayStats = { ...state.todayStats, ...stats }
    }
  },

  actions: {
    // 初始化抽奖数据
    async initLotteryData({ dispatch }) {
      try {
        await Promise.all([
          dispatch('fetchUserStatus'),
          dispatch('fetchPrizes'),
          dispatch('fetchActivityConfig'),
          dispatch('fetchTodayStats')
        ])
      } catch (error) {
        console.error('初始化抽奖数据失败:', error)
        throw error
      }
    },
    
    // 获取用户状态
    async fetchUserStatus({ commit }) {
      try {
        const [statusRes, countRes] = await Promise.all([
          getUserStatus(),
          getDrawCount()
        ])
        
        commit('SET_USER_STATUS', {
          ...statusRes.data,
          drawCount: countRes.data
        })
        
        return statusRes.data
      } catch (error) {
        console.error('获取用户状态失败:', error)
        throw error
      }
    },
    
    // 获取奖品列表
    async fetchPrizes({ commit }) {
      try {
        const res = await getPrizes()
        commit('SET_PRIZES', res.data)
        return res.data
      } catch (error) {
        console.error('获取奖品列表失败:', error)
        throw error
      }
    },
    
    // 获取用户记录
    async fetchUserRecords({ commit, state }, params = {}) {
      try {
        commit('SET_RECORDS_LOADING', true)
        
        const query = {
          pageNum: params.pageNum || state.records.pageNum,
          pageSize: params.pageSize || state.records.pageSize,
          ...params
        }
        
        const res = await getUserRecords(query)
        
        commit('SET_RECORDS', {
          list: res.rows,
          total: res.total,
          pageNum: query.pageNum,
          pageSize: query.pageSize
        })
        
        return res
      } catch (error) {
        console.error('获取用户记录失败:', error)
        throw error
      } finally {
        commit('SET_RECORDS_LOADING', false)
      }
    },
    
    // 获取活动配置
    async fetchActivityConfig({ commit }) {
      try {
        const res = await getActivityConfig()
        commit('SET_ACTIVITY_CONFIG', res.data)
        return res.data
      } catch (error) {
        console.error('获取活动配置失败:', error)
        throw error
      }
    },
    
    // 获取中奖公告
    async fetchAnnouncements({ commit }, params = {}) {
      try {
        const res = await getWinningAnnouncements(params)
        commit('SET_ANNOUNCEMENTS', res.rows)
        return res.rows
      } catch (error) {
        console.error('获取中奖公告失败:', error)
        throw error
      }
    },
    
    // 获取今日统计
    async fetchTodayStats({ commit }) {
      try {
        const res = await getTodayStats()
        commit('SET_TODAY_STATS', res.data)
        return res.data
      } catch (error) {
        console.error('获取今日统计失败:', error)
        throw error
      }
    },
    
    // 执行抽奖
    async performDraw({ commit, dispatch, state }) {
      if (state.drawing) {
        throw new Error('正在抽奖中，请稍候')
      }
      
      if (state.userStatus.drawCount <= 0) {
        throw new Error('抽奖次数不足')
      }
      
      try {
        commit('SET_DRAWING', true)
        
        const res = await drawLottery()
        
        if (res.code === 200) {
          // 抽奖成功
          commit('SET_DRAW_RESULT', res.data)
          commit('DECREASE_DRAW_COUNT')
          commit('ADD_RECORD', res.data)
          
          // 更新今日统计
          await dispatch('fetchTodayStats')
          
          return res.data
        } else {
          throw new Error(res.msg || '抽奖失败')
        }
        
      } catch (error) {
        console.error('抽奖失败:', error)
        throw error
      } finally {
        commit('SET_DRAWING', false)
      }
    },
    
    // 显示抽奖结果
    showDrawResult({ commit }, result) {
      commit('SET_DRAW_RESULT', result)
      commit('SET_SHOW_RESULT', true)
    },
    
    // 隐藏抽奖结果
    hideDrawResult({ commit }) {
      commit('SET_SHOW_RESULT', false)
      commit('SET_DRAW_RESULT', null)
    },
    
    // 检查活动状态
    async checkActivity({ commit }) {
      try {
        const res = await checkActivityStatus()
        commit('SET_ACTIVITY_CONFIG', { status: res.data.status })
        return res.data
      } catch (error) {
        console.error('检查活动状态失败:', error)
        throw error
      }
    }
  },

  getters: {
    // 是否可以抽奖
    canDraw: state => {
      return state.userStatus.canDraw && 
             state.userStatus.drawCount > 0 && 
             state.activityConfig.status === 1 &&
             !state.drawing
    },
    
    // 获取奖品信息
    getPrizeById: state => id => {
      return state.prizeMap[id] || null
    },
    
    // 活动是否进行中
    isActivityActive: state => {
      return state.activityConfig.status === 1
    },
    
    // 今日是否还能抽奖
    canDrawToday: state => {
      return state.userStatus.todayDrawCount < state.activityConfig.dailyLimit
    },
    
    // 获取中奖记录（只包含中奖的）
    winningRecords: state => {
      return state.records.list.filter(record => record.prizeId && record.prizeId !== 'empty')
    }
  }
}

export default lottery