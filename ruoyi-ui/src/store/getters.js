const getters = {
  sidebar: state => state.app.sidebar,
  size: state => state.app.size,
  device: state => state.app.device,
  dict: state => state.dict.dict,
  visitedViews: state => state.tagsView.visitedViews,
  cachedViews: state => state.tagsView.cachedViews,
  token: state => state.user.token,
  avatar: state => state.user.avatar,
  id: state => state.user.id,
  name: state => state.user.name,
  nickName: state => state.user.nickName,
  introduction: state => state.user.introduction,
  roles: state => state.user.roles,
  permissions: state => state.user.permissions,
  permission_routes: state => state.permission.routes,
  topbarRouters: state => state.permission.topbarRouters,
  defaultRoutes: state => state.permission.defaultRoutes,
  sidebarRouters: state => state.permission.sidebarRouters,
  
  // 抽奖相关全局getters
  lotteryCanDraw: state => state.lottery.canDraw,
  lotteryDrawCount: state => state.lottery.userStatus.drawCount,
  lotteryPrizes: state => state.lottery.prizes,
  lotteryRecords: state => state.lottery.records.list,
  lotteryDrawing: state => state.lottery.drawing,
  lotteryActivityStatus: state => state.lottery.activityConfig.status
}
export default getters
