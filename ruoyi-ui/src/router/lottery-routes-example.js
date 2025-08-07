/**
 * 抽奖路由配置说明和使用示例
 */

// 路由结构说明
const routeStructure = {
  '/lottery': {
    name: 'Lottery',
    title: '抽奖活动',
    description: '主抽奖页面，包含转盘和抽奖功能',
    requireAuth: true,      // 需要登录
    requireActivity: true,  // 需要活动进行中
    noCache: true          // 不缓存页面
  },
  
  '/lottery/result': {
    name: 'LotteryResult', 
    title: '抽奖结果',
    description: '显示抽奖结果的页面',
    requireAuth: true,
    fromLottery: true,     // 必须从抽奖页面跳转
    noCache: true
  },
  
  '/lottery/records': {
    name: 'LotteryRecords',
    title: '我的记录', 
    description: '用户抽奖历史记录',
    requireAuth: true,
    preload: 'userRecords', // 预加载用户记录
    noCache: true
  },
  
  '/lottery/rules': {
    name: 'LotteryRules',
    title: '活动规则',
    description: '活动规则说明页面',
    public: true           // 公开访问，无需登录
  },
  
  '/lottery/prizes': {
    name: 'LotteryPrizes',
    title: '奖品展示',
    description: '展示所有奖品信息',
    public: true
  },
  
  '/lottery/winners': {
    name: 'LotteryWinners', 
    title: '中奖名单',
    description: '显示中奖用户列表',
    public: true
  },
  
  '/lottery/share': {
    name: 'LotteryShare',
    title: '分享页面',
    description: '分享抽奖结果的页面',
    requireAuth: true,
    requireResult: true,   // 需要有抽奖结果数据
    noCache: true
  }
}

// 在组件中使用路由的示例
export const routerUsageExamples = {
  
  // 1. 编程式导航到抽奖页面
  goToLottery() {
    this.$router.push('/lottery')
  },
  
  // 2. 抽奖成功后跳转到结果页面
  onDrawSuccess(result) {
    // 先保存结果到store
    this.$store.dispatch('lottery/showDrawResult', result)
    // 然后跳转
    this.$router.push('/lottery/result')
  },
  
  // 3. 查看抽奖记录
  viewRecords() {
    this.$router.push('/lottery/records')
  },
  
  // 4. 分享抽奖结果
  shareResult(result) {
    this.$router.push({
      path: '/lottery/share',
      query: { result: JSON.stringify(result) }
    })
  },
  
  // 5. 路由守卫中的跳转逻辑
  handleActivityStatus(status) {
    switch (status) {
      case 0: // 未开始
        this.$router.push('/lottery/rules')
        break
      case 2: // 已结束  
        this.$router.push('/lottery/winners')
        break
      default:
        this.$router.push('/lottery')
    }
  },
  
  // 6. 带参数的路由跳转
  goToShareWithData(drawResult) {
    this.$router.push({
      name: 'LotteryShare',
      params: { result: drawResult },
      query: { 
        from: 'lottery',
        timestamp: Date.now()
      }
    })
  }
}

// 路由监听示例
export const routeWatchExamples = {
  
  // 监听路由变化
  watch: {
    '$route'(to, from) {
      // 进入抽奖相关页面时的处理
      if (to.path.startsWith('/lottery')) {
        this.handleLotteryRoute(to, from)
      }
    }
  },
  
  methods: {
    handleLotteryRoute(to, from) {
      // 根据不同页面执行不同逻辑
      switch (to.name) {
        case 'Lottery':
          this.initLotteryPage()
          break
        case 'LotteryResult':
          this.showResultAnimation()
          break
        case 'LotteryRecords':
          this.loadUserRecords()
          break
      }
    },
    
    // 页面初始化
    initLotteryPage() {
      // 检查用户状态
      this.$store.dispatch('lottery/fetchUserStatus')
      // 获取奖品列表
      this.$store.dispatch('lottery/fetchPrizes')
    }
  }
}

// 路由元信息的使用
export const metaUsageExamples = {
  
  // 在组件中访问路由元信息
  computed: {
    pageTitle() {
      return this.$route.meta.title || '抽奖活动'
    },
    
    requireAuth() {
      return this.$route.meta.requireAuth || false
    },
    
    isPublicPage() {
      return this.$route.meta.public || false
    }
  },
  
  // 根据元信息控制页面行为
  created() {
    // 设置页面标题
    document.title = this.pageTitle
    
    // 根据是否需要认证执行不同逻辑
    if (this.requireAuth) {
      this.checkUserAuth()
    }
  }
}

// 错误处理示例
export const errorHandlingExamples = {
  
  // 路由错误处理
  handleRouteError(error, to, from) {
    console.error('路由错误:', error)
    
    switch (error.type) {
      case 'AUTH_REQUIRED':
        this.$message.warning('请先登录')
        this.$router.push('/login')
        break
        
      case 'ACTIVITY_NOT_ACTIVE':
        this.$message.warning('活动未开始或已结束')
        this.$router.push('/lottery/rules')
        break
        
      case 'NO_PERMISSION':
        this.$message.error('没有访问权限')
        this.$router.push('/')
        break
        
      default:
        this.$message.error('页面访问失败')
        this.$router.push('/')
    }
  }
}