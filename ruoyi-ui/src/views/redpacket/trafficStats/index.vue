<template>
  <div class="app-container">
    <!-- 实时状态卡片 -->
    <el-row :gutter="20" class="mb8">
      <el-col :span="6">
        <el-card class="status-card">
          <div class="status-item">
            <div class="status-icon active">
              <i class="el-icon-user"></i>
            </div>
            <div class="status-content">
              <div class="status-value">{{ realTimeStats.activeUsers }}</div>
              <div class="status-label">当前活跃用户</div>
              <div class="status-trend" :class="trends.activeUsers.type">
                <i :class="trends.activeUsers.icon"></i>
                {{ trends.activeUsers.value }}%
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="status-card">
          <div class="status-item">
            <div class="status-icon queued">
              <i class="el-icon-time"></i>
            </div>
            <div class="status-content">
              <div class="status-value">{{ realTimeStats.queuedUsers }}</div>
              <div class="status-label">排队用户</div>
              <div class="status-detail">平均等待: {{ realTimeStats.avgWaitTime }}分钟</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="status-card">
          <div class="status-item">
            <div class="status-icon load">
              <i class="el-icon-cpu"></i>
            </div>
            <div class="status-content">
              <div class="status-value">{{ realTimeStats.systemLoad }}%</div>
              <div class="status-label">系统负载</div>
              <div class="load-bar">
                <div class="load-progress" :style="{ width: realTimeStats.systemLoad + '%' }"></div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="status-card">
          <div class="status-item">
            <div class="status-icon success">
              <i class="el-icon-check"></i>
            </div>
            <div class="status-content">
              <div class="status-value">{{ realTimeStats.successRate }}%</div>
              <div class="status-label">成功率</div>
              <div class="status-detail">今日: {{ realTimeStats.todayJoins }} 次</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 快速操作按钮 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button 
          :type="realTimeMode ? 'success' : 'primary'" 
          :icon="realTimeMode ? 'el-icon-video-pause' : 'el-icon-video-play'" 
          size="mini" 
          @click="toggleRealTimeMode"
        >
          {{ realTimeMode ? '停止实时' : '开启实时' }}
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-setting" size="mini" @click="showConfigDialog = true">
          系统配置
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" @click="handleClearQueue">
          清空队列
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="info" plain icon="el-icon-refresh" size="mini" @click="handleResetStats">
          重置统计
        </el-button>
      </el-col>
    </el-row>

    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="时间范围" prop="dateRange">
        <el-date-picker 
          v-model="queryParams.dateRange" 
          type="datetimerange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="yyyy-MM-dd HH:mm:ss"
          :default-time="['00:00:00', '23:59:59']">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="统计类型" prop="statType">
        <el-select v-model="queryParams.statType" placeholder="请选择统计类型" clearable>
          <el-option label="小时统计" value="hour"></el-option>
          <el-option label="日统计" value="day"></el-option>
          <el-option label="周统计" value="week"></el-option>
          <el-option label="月统计" value="month"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="活跃用户数" prop="activeUsers">
        <el-input v-model="queryParams.activeUsers" placeholder="请输入活跃用户数" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="排队用户数" prop="queuedUsers">
        <el-input v-model="queryParams.queuedUsers" placeholder="请输入排队用户数" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['redpacket:trafficStats:export']">导出</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="info" plain icon="el-icon-s-data" size="mini" @click="showChart = !showChart">{{ showChart ?
          '隐藏图表' : '显示图表' }}</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 多维度图表分析 -->
    <el-card v-show="showChart" class="mb8">
      <div slot="header">
        <span>流量统计分析</span>
        <div style="float: right;">
          <el-radio-group v-model="chartType" size="mini" @change="updateChart">
            <el-radio-button label="trend">趋势图</el-radio-button>
            <el-radio-button label="distribution">分布图</el-radio-button>
            <el-radio-button label="heatmap">热力图</el-radio-button>
            <el-radio-button label="comparison">对比图</el-radio-button>
          </el-radio-group>
        </div>
      </div>
      <div class="chart-container">
        <div ref="chart" style="height: 400px;"></div>
        <div v-if="chartType === 'heatmap'" ref="heatmapChart" style="height: 300px; margin-top: 20px;"></div>
      </div>
    </el-card>

    <!-- 详细分析面板 -->
    <el-row :gutter="20" class="mb8">
      <el-col :span="12">
        <el-card>
          <div slot="header">
            <span>流量峰值分析</span>
          </div>
          <div class="analysis-content">
            <div class="peak-info">
              <div class="peak-item">
                <span class="peak-label">今日峰值:</span>
                <span class="peak-value">{{ peakAnalysis.todayPeak }} 用户</span>
                <span class="peak-time">{{ peakAnalysis.todayPeakTime }}</span>
              </div>
              <div class="peak-item">
                <span class="peak-label">历史峰值:</span>
                <span class="peak-value">{{ peakAnalysis.historyPeak }} 用户</span>
                <span class="peak-time">{{ peakAnalysis.historyPeakTime }}</span>
              </div>
              <div class="peak-item">
                <span class="peak-label">预测峰值:</span>
                <span class="peak-value">{{ peakAnalysis.predictedPeak }} 用户</span>
                <span class="peak-time">{{ peakAnalysis.predictedTime }}</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <div slot="header">
            <span>用户行为分析</span>
          </div>
          <div class="behavior-analysis">
            <div class="behavior-item">
              <span class="behavior-label">平均会话时长:</span>
              <span class="behavior-value">{{ behaviorAnalysis.avgSessionDuration }} 分钟</span>
            </div>
            <div class="behavior-item">
              <span class="behavior-label">跳出率:</span>
              <span class="behavior-value">{{ behaviorAnalysis.bounceRate }}%</span>
            </div>
            <div class="behavior-item">
              <span class="behavior-label">重试率:</span>
              <span class="behavior-value">{{ behaviorAnalysis.retryRate }}%</span>
            </div>
            <div class="behavior-item">
              <span class="behavior-label">转化率:</span>
              <span class="behavior-value">{{ behaviorAnalysis.conversionRate }}%</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-table v-loading="loading" :data="trafficStatsList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="统计ID" align="center" prop="id" />
      <el-table-column label="统计时间" align="center" prop="statTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.statTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="活跃用户数" align="center" prop="activeUsers">
        <template slot-scope="scope">
          <el-tag type="success">{{ scope.row.activeUsers }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="排队用户数" align="center" prop="queuedUsers">
        <template slot-scope="scope">
          <el-tag type="warning">{{ scope.row.queuedUsers }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="总请求数" align="center" prop="totalRequests">
        <template slot-scope="scope">
          <el-tag type="primary">{{ scope.row.totalRequests }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="被拒绝请求数" align="center" prop="rejectedRequests">
        <template slot-scope="scope">
          <el-tag type="danger">{{ scope.row.rejectedRequests }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="平均会话时间" align="center" prop="averageSessionTime">
        <template slot-scope="scope">
          <span>{{ scope.row.averageSessionTime }}秒</span>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createdAt" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createdAt, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 系统配置对话框 -->
    <el-dialog title="系统配置" :visible.sync="showConfigDialog" width="600px">
      <el-form :model="systemConfig" label-width="120px">
        <el-form-item label="最大并发用户">
          <el-input-number v-model="systemConfig.maxConcurrentUsers" :min="100" :max="10000" :step="100"></el-input-number>
        </el-form-item>
        <el-form-item label="队列超时时间">
          <el-input-number v-model="systemConfig.queueTimeoutSeconds" :min="60" :max="1800" :step="30"></el-input-number>
          <span class="config-unit">秒</span>
        </el-form-item>
        <el-form-item label="心跳超时时间">
          <el-input-number v-model="systemConfig.heartbeatTimeoutSeconds" :min="30" :max="300" :step="10"></el-input-number>
          <span class="config-unit">秒</span>
        </el-form-item>
        <el-form-item label="维护模式">
          <el-switch v-model="systemConfig.maintenanceMode"></el-switch>
        </el-form-item>
        <el-form-item label="自动扩缩容">
          <el-switch v-model="systemConfig.autoScaling"></el-switch>
        </el-form-item>
        <el-form-item label="扩容阈值">
          <el-slider v-model="systemConfig.scaleUpThreshold" :min="50" :max="95" :step="5" show-stops></el-slider>
          <span class="config-unit">{{ systemConfig.scaleUpThreshold }}%</span>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="showConfigDialog = false">取 消</el-button>
        <el-button type="primary" @click="saveSystemConfig">确 定</el-button>
      </div>
    </el-dialog>

    <!-- 用户详情对话框 -->
    <el-dialog title="用户会话详情" :visible.sync="showUserDialog" width="800px">
      <el-table :data="userSessions" v-loading="userLoading">
        <el-table-column prop="sessionId" label="会话ID" width="200"></el-table-column>
        <el-table-column prop="userId" label="用户ID" width="120"></el-table-column>
        <el-table-column prop="ipAddress" label="IP地址" width="120"></el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="joinTime" label="加入时间" width="150">
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.joinTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="lastHeartbeat" label="最后心跳" width="150">
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.lastHeartbeat, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template slot-scope="scope">
            <el-button size="mini" type="danger" @click="kickUser(scope.row.sessionId)">踢出</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script>
import { 
  listTrafficStats, 
  getTrafficStats, 
  exportTrafficStats,
  getRealTimeStats,
  getPeakAnalysis,
  getBehaviorAnalysis,
  getSystemConfig,
  updateSystemConfig,
  getUserSessions,
  clearQueue,
  resetStats,
  removeUserSession
} from "@/api/redpacket/trafficStats";
import * as echarts from 'echarts';

export default {
  name: "TrafficStats",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 显示图表
      showChart: false,
      // 总条数
      total: 0,
      // 流量统计表格数据
      trafficStatsList: [],
      // 图表实例
      chart: null,
      heatmapChart: null,
      // 图表类型
      chartType: 'trend',
      // 实时模式
      realTimeMode: false,
      realTimeTimer: null,
      // 实时统计数据
      realTimeStats: {
        activeUsers: 0,
        queuedUsers: 0,
        systemLoad: 0,
        successRate: 0,
        avgWaitTime: 0,
        todayJoins: 0
      },
      // 趋势数据
      trends: {
        activeUsers: { type: 'up', icon: 'el-icon-top', value: 0 }
      },
      // 峰值分析
      peakAnalysis: {
        todayPeak: 0,
        todayPeakTime: '',
        historyPeak: 0,
        historyPeakTime: '',
        predictedPeak: 0,
        predictedTime: ''
      },
      // 用户行为分析
      behaviorAnalysis: {
        avgSessionDuration: 0,
        bounceRate: 0,
        retryRate: 0,
        conversionRate: 0
      },
      // 系统配置
      systemConfig: {
        maxConcurrentUsers: 1000,
        queueTimeoutSeconds: 300,
        heartbeatTimeoutSeconds: 60,
        maintenanceMode: false,
        autoScaling: false,
        scaleUpThreshold: 80
      },
      // 对话框显示状态
      showConfigDialog: false,
      showUserDialog: false,
      // 用户会话数据
      userSessions: [],
      userLoading: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        dateRange: null,
        statType: 'hour',
        activeUsers: null,
        queuedUsers: null,
      }
    };
  },
  created() {
    this.getList();
    this.loadSystemConfig();
    this.loadRealTimeStats();
    this.loadAnalysisData();
  },
  mounted() {
    this.$nextTick(() => {
      this.initChart();
    });
  },
  beforeDestroy() {
    if (this.chart) {
      this.chart.dispose();
    }
    if (this.heatmapChart) {
      this.heatmapChart.dispose();
    }
    if (this.realTimeTimer) {
      clearInterval(this.realTimeTimer);
    }
  },
  methods: {
    /** 查询流量统计列表 */
    getList() {
      this.loading = true;
      listTrafficStats(this.queryParams).then(response => {
        this.trafficStatsList = response.rows;
        this.total = response.total;
        this.loading = false;
        this.updateChart();
      });
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('redpacket/trafficStats/export', {
        ...this.queryParams
      }, `trafficStats_${new Date().getTime()}.xlsx`)
    },
    /** 初始化图表 */
    initChart() {
      this.chart = echarts.init(this.$refs.chart);
      this.updateChart();
    },
    /** 更新图表 */
    updateChart() {
      if (!this.chart || this.trafficStatsList.length === 0) return;

      const times = this.trafficStatsList.map(item =>
        this.parseTime(item.statTime, '{m}-{d} {h}:{i}')
      );

      let option = {};

      switch (this.chartType) {
        case 'trend':
          option = this.getTrendChartOption(times);
          break;
        case 'distribution':
          option = this.getDistributionChartOption();
          break;
        case 'heatmap':
          option = this.getHeatmapChartOption();
          this.initHeatmapChart();
          break;
        case 'comparison':
          option = this.getComparisonChartOption(times);
          break;
      }

      this.chart.setOption(option, true);
    },

    /** 趋势图配置 */
    getTrendChartOption(times) {
      const activeUsers = this.trafficStatsList.map(item => item.activeUsers);
      const queuedUsers = this.trafficStatsList.map(item => item.queuedUsers);
      const totalRequests = this.trafficStatsList.map(item => item.totalRequests);
      const rejectedRequests = this.trafficStatsList.map(item => item.rejectedRequests);

      return {
        title: {
          text: '流量统计趋势图',
          left: 'center'
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'cross'
          }
        },
        legend: {
          data: ['活跃用户', '排队用户', '总请求', '拒绝请求'],
          top: 30
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          top: '15%',
          containLabel: true
        },
        toolbox: {
          feature: {
            saveAsImage: {},
            dataZoom: {},
            restore: {}
          }
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: times
        },
        yAxis: [
          {
            type: 'value',
            name: '用户数',
            position: 'left'
          },
          {
            type: 'value',
            name: '请求数',
            position: 'right'
          }
        ],
        series: [
          {
            name: '活跃用户',
            type: 'line',
            data: activeUsers,
            smooth: true,
            itemStyle: { color: '#67C23A' },
            areaStyle: { opacity: 0.3 }
          },
          {
            name: '排队用户',
            type: 'line',
            data: queuedUsers,
            smooth: true,
            itemStyle: { color: '#E6A23C' },
            areaStyle: { opacity: 0.3 }
          },
          {
            name: '总请求',
            type: 'line',
            yAxisIndex: 1,
            data: totalRequests,
            smooth: true,
            itemStyle: { color: '#409EFF' }
          },
          {
            name: '拒绝请求',
            type: 'line',
            yAxisIndex: 1,
            data: rejectedRequests,
            smooth: true,
            itemStyle: { color: '#F56C6C' }
          }
        ]
      };
    },

    /** 分布图配置 */
    getDistributionChartOption() {
      const hourlyData = this.calculateHourlyDistribution();
      
      return {
        title: {
          text: '24小时流量分布',
          left: 'center'
        },
        tooltip: {
          trigger: 'axis'
        },
        xAxis: {
          type: 'category',
          data: Array.from({length: 24}, (_, i) => i + ':00')
        },
        yAxis: {
          type: 'value',
          name: '平均用户数'
        },
        series: [
          {
            name: '活跃用户',
            type: 'bar',
            data: hourlyData.active,
            itemStyle: { color: '#67C23A' }
          },
          {
            name: '排队用户',
            type: 'bar',
            data: hourlyData.queued,
            itemStyle: { color: '#E6A23C' }
          }
        ]
      };
    },

    /** 对比图配置 */
    getComparisonChartOption(times) {
      const thisWeekData = this.trafficStatsList.slice(-7);
      const lastWeekData = this.trafficStatsList.slice(-14, -7);

      return {
        title: {
          text: '本周与上周对比',
          left: 'center'
        },
        tooltip: {
          trigger: 'axis'
        },
        legend: {
          data: ['本周活跃用户', '上周活跃用户', '本周排队用户', '上周排队用户'],
          top: 30
        },
        xAxis: {
          type: 'category',
          data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            name: '本周活跃用户',
            type: 'line',
            data: thisWeekData.map(item => item.activeUsers),
            itemStyle: { color: '#67C23A' }
          },
          {
            name: '上周活跃用户',
            type: 'line',
            data: lastWeekData.map(item => item.activeUsers),
            itemStyle: { color: '#67C23A' },
            lineStyle: { type: 'dashed' }
          },
          {
            name: '本周排队用户',
            type: 'line',
            data: thisWeekData.map(item => item.queuedUsers),
            itemStyle: { color: '#E6A23C' }
          },
          {
            name: '上周排队用户',
            type: 'line',
            data: lastWeekData.map(item => item.queuedUsers),
            itemStyle: { color: '#E6A23C' },
            lineStyle: { type: 'dashed' }
          }
        ]
      };
    },

    /** 初始化热力图 */
    initHeatmapChart() {
      if (!this.$refs.heatmapChart) return;
      
      this.heatmapChart = echarts.init(this.$refs.heatmapChart);
      const heatmapData = this.generateHeatmapData();
      
      const option = {
        title: {
          text: '流量热力图 (小时 x 星期)',
          left: 'center'
        },
        tooltip: {
          position: 'top'
        },
        grid: {
          height: '50%',
          top: '10%'
        },
        xAxis: {
          type: 'category',
          data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
          splitArea: {
            show: true
          }
        },
        yAxis: {
          type: 'category',
          data: Array.from({length: 24}, (_, i) => i + ':00'),
          splitArea: {
            show: true
          }
        },
        visualMap: {
          min: 0,
          max: 1000,
          calculable: true,
          orient: 'horizontal',
          left: 'center',
          bottom: '15%'
        },
        series: [{
          name: '用户数',
          type: 'heatmap',
          data: heatmapData,
          label: {
            show: true
          },
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          }
        }]
      };
      
      this.heatmapChart.setOption(option);
    },

    /** 生成热力图数据 */
    generateHeatmapData() {
      const data = [];
      // 基于真实的流量统计数据生成热力图
      if (this.trafficStatsList && this.trafficStatsList.length > 0) {
        // 按小时和星期分组统计
        const hourlyStats = {};
        
        this.trafficStatsList.forEach(item => {
          const date = new Date(item.statTime);
          const hour = date.getHours();
          const day = date.getDay(); // 0=周日, 1=周一, ..., 6=周六
          const key = `${day}-${hour}`;
          
          if (!hourlyStats[key]) {
            hourlyStats[key] = [];
          }
          hourlyStats[key].push(item.activeUsers || 0);
        });
        
        // 生成热力图数据
        for (let day = 0; day < 7; day++) {
          for (let hour = 0; hour < 24; hour++) {
            const key = `${day}-${hour}`;
            let value = 0;
            
            if (hourlyStats[key] && hourlyStats[key].length > 0) {
              // 计算平均值
              value = Math.round(hourlyStats[key].reduce((a, b) => a + b, 0) / hourlyStats[key].length);
            }
            
            data.push([day, hour, value]);
          }
        }
      } else {
        // 如果没有数据，返回空数据
        for (let day = 0; day < 7; day++) {
          for (let hour = 0; hour < 24; hour++) {
            data.push([day, hour, 0]);
          }
        }
      }
      
      return data;
    },

    /** 计算小时分布 */
    calculateHourlyDistribution() {
      const active = new Array(24).fill(0);
      const queued = new Array(24).fill(0);
      const counts = new Array(24).fill(0);

      this.trafficStatsList.forEach(item => {
        const hour = new Date(item.statTime).getHours();
        active[hour] += item.activeUsers;
        queued[hour] += item.queuedUsers;
        counts[hour]++;
      });

      return {
        active: active.map((sum, i) => counts[i] ? Math.round(sum / counts[i]) : 0),
        queued: queued.map((sum, i) => counts[i] ? Math.round(sum / counts[i]) : 0)
      };
    },

    /** 切换实时模式 */
    toggleRealTimeMode() {
      this.realTimeMode = !this.realTimeMode;
      
      if (this.realTimeMode) {
        this.startRealTimeUpdates();
        this.$message.success('已开启实时监控');
      } else {
        this.stopRealTimeUpdates();
        this.$message.info('已停止实时监控');
      }
    },

    /** 开始实时更新 */
    startRealTimeUpdates() {
      this.realTimeTimer = setInterval(() => {
        this.loadRealTimeStats();
      }, 5000); // 每5秒更新一次
    },

    /** 停止实时更新 */
    stopRealTimeUpdates() {
      if (this.realTimeTimer) {
        clearInterval(this.realTimeTimer);
        this.realTimeTimer = null;
      }
    },

    /** 加载实时统计数据 */
    async loadRealTimeStats() {
      try {
        const response = await this.fetchRealTimeStats();
        const oldStats = { ...this.realTimeStats };
        this.realTimeStats = response.data;
        
        // 计算趋势
        this.calculateTrends(oldStats, this.realTimeStats);
      } catch (error) {
        console.error('加载实时统计失败:', error);
        // 发生错误时，停止实时模式
        if (this.realTimeMode) {
          this.toggleRealTimeMode();
        }
      }
    },

    /** 获取实时统计数据 */
    async fetchRealTimeStats() {
      try {
        const response = await getRealTimeStats();
        return response;
      } catch (error) {
        console.error('获取实时统计数据失败:', error);
        // 如果API调用失败，返回默认值
        return {
          data: {
            activeUsers: 0,
            queuedUsers: 0,
            systemLoad: 0,
            successRate: 0,
            avgWaitTime: 0,
            todayJoins: 0
          }
        };
      }
    },

    /** 计算趋势 */
    calculateTrends(oldStats, newStats) {
      const activeChange = newStats.activeUsers - oldStats.activeUsers;
      const changePercent = oldStats.activeUsers ? Math.abs(activeChange / oldStats.activeUsers * 100) : 0;
      
      this.trends.activeUsers = {
        type: activeChange > 0 ? 'up' : activeChange < 0 ? 'down' : 'stable',
        icon: activeChange > 0 ? 'el-icon-top' : activeChange < 0 ? 'el-icon-bottom' : 'el-icon-minus',
        value: Math.round(changePercent)
      };
    },

    /** 加载系统配置 */
    async loadSystemConfig() {
      try {
        // 模拟API调用
        const response = await this.fetchSystemConfig();
        this.systemConfig = response.data;
      } catch (error) {
        console.error('加载系统配置失败:', error);
      }
    },

    /** 获取系统配置 */
    async fetchSystemConfig() {
      try {
        const response = await getSystemConfig();
        return response;
      } catch (error) {
        console.error('获取系统配置失败:', error);
        return {
          data: {
            maxConcurrentUsers: 1000,
            queueTimeoutSeconds: 300,
            heartbeatTimeoutSeconds: 60,
            maintenanceMode: false,
            autoScaling: false,
            scaleUpThreshold: 80
          }
        };
      }
    },

    /** 保存系统配置 */
    async saveSystemConfig() {
      try {
        // 模拟API调用
        await this.updateSystemConfig(this.systemConfig);
        this.$message.success('配置保存成功');
        this.showConfigDialog = false;
      } catch (error) {
        this.$message.error('配置保存失败');
      }
    },

    /** 更新系统配置 */
    async updateSystemConfig(config) {
      try {
        await updateSystemConfig(config);
      } catch (error) {
        console.error('更新系统配置失败:', error);
        throw error;
      }
    },

    /** 加载分析数据 */
    async loadAnalysisData() {
      try {
        const [peakResponse, behaviorResponse] = await Promise.all([
          this.fetchPeakAnalysis(),
          this.fetchBehaviorAnalysis()
        ]);
        
        this.peakAnalysis = peakResponse.data;
        this.behaviorAnalysis = behaviorResponse.data;
      } catch (error) {
        console.error('加载分析数据失败:', error);
      }
    },

    /** 获取峰值分析数据 */
    async fetchPeakAnalysis() {
      try {
        const response = await getPeakAnalysis();
        return response;
      } catch (error) {
        console.error('获取峰值分析数据失败:', error);
        return {
          data: {
            todayPeak: 0,
            todayPeakTime: '',
            historyPeak: 0,
            historyPeakTime: '',
            predictedPeak: 0,
            predictedTime: ''
          }
        };
      }
    },

    /** 获取用户行为分析数据 */
    async fetchBehaviorAnalysis() {
      try {
        const response = await getBehaviorAnalysis();
        return response;
      } catch (error) {
        console.error('获取用户行为分析数据失败:', error);
        return {
          data: {
            avgSessionDuration: 0,
            bounceRate: 0,
            retryRate: 0,
            conversionRate: 0
          }
        };
      }
    },

    /** 清空队列 */
    async handleClearQueue() {
      try {
        await this.$confirm('确定要清空所有排队用户吗？', '警告', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        });
        
        // 模拟API调用
        await this.clearQueue();
        this.$message.success('队列已清空');
        this.loadRealTimeStats();
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error('清空队列失败');
        }
      }
    },

    /** 清空队列 */
    async clearQueue() {
      try {
        await clearQueue();
      } catch (error) {
        console.error('清空队列失败:', error);
        throw error;
      }
    },

    /** 重置统计 */
    async handleResetStats() {
      try {
        await this.$confirm('确定要重置所有统计数据吗？', '警告', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        });
        
        // 模拟API调用
        await this.resetStats();
        this.$message.success('统计数据已重置');
        this.getList();
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error('重置统计失败');
        }
      }
    },

    /** 重置统计 */
    async resetStats() {
      try {
        await resetStats();
      } catch (error) {
        console.error('重置统计失败:', error);
        throw error;
      }
    },

    /** 获取状态类型 */
    getStatusType(status) {
      const statusMap = {
        'active': 'success',
        'queued': 'warning',
        'expired': 'danger',
        'left': 'info'
      };
      return statusMap[status] || 'info';
    },

    /** 获取状态文本 */
    getStatusText(status) {
      const statusMap = {
        'active': '活跃',
        'queued': '排队',
        'expired': '过期',
        'left': '已离开'
      };
      return statusMap[status] || status;
    },

    /** 踢出用户 */
    async kickUser(sessionId) {
      try {
        await this.$confirm('确定要踢出该用户吗？', '确认', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        });
        
        // 模拟API调用
        await this.removeUserSession(sessionId);
        this.$message.success('用户已被踢出');
        this.loadUserSessions();
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error('踢出用户失败');
        }
      }
    },

    /** 移除用户会话 */
    async removeUserSession(sessionId) {
      try {
        await removeUserSession(sessionId);
      } catch (error) {
        console.error('移除用户会话失败:', error);
        throw error;
      }
    },

    /** 加载用户会话 */
    async loadUserSessions() {
      this.userLoading = true;
      try {
        const response = await this.fetchUserSessions();
        this.userSessions = response.data;
      } catch (error) {
        this.$message.error('加载用户会话失败');
      } finally {
        this.userLoading = false;
      }
    },

    /** 获取用户会话 */
    async fetchUserSessions() {
      try {
        const response = await getUserSessions();
        return response;
      } catch (error) {
        console.error('获取用户会话失败:', error);
        return { data: [] };
      }
    }
  },
  watch: {
    showChart(val) {
      if (val) {
        this.$nextTick(() => {
          if (!this.chart) {
            this.initChart();
          } else {
            this.chart.resize();
          }
        });
      }
    }
  }
};
</script>

<style scoped>
.mb8 {
  margin-bottom: 8px;
}

/* 状态卡片样式 */
.status-card {
  height: 120px;
}

.status-item {
  display: flex;
  align-items: center;
  height: 100%;
}

.status-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
  margin-right: 15px;
}

.status-icon.active {
  background: linear-gradient(135deg, #67C23A, #85CE61);
}

.status-icon.queued {
  background: linear-gradient(135deg, #E6A23C, #EEBE77);
}

.status-icon.load {
  background: linear-gradient(135deg, #409EFF, #79BBFF);
}

.status-icon.success {
  background: linear-gradient(135deg, #67C23A, #85CE61);
}

.status-content {
  flex: 1;
}

.status-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  line-height: 1;
}

.status-label {
  font-size: 14px;
  color: #909399;
  margin-top: 5px;
}

.status-detail {
  font-size: 12px;
  color: #C0C4CC;
  margin-top: 3px;
}

.status-trend {
  font-size: 12px;
  margin-top: 5px;
}

.status-trend.up {
  color: #67C23A;
}

.status-trend.down {
  color: #F56C6C;
}

.status-trend.stable {
  color: #909399;
}

.load-bar {
  width: 100%;
  height: 4px;
  background-color: #EBEEF5;
  border-radius: 2px;
  margin-top: 5px;
  overflow: hidden;
}

.load-progress {
  height: 100%;
  background: linear-gradient(90deg, #409EFF, #79BBFF);
  border-radius: 2px;
  transition: width 0.3s ease;
}

/* 图表容器 */
.chart-container {
  position: relative;
}

/* 分析面板样式 */
.analysis-content {
  padding: 10px 0;
}

.peak-info {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.peak-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px;
  background-color: #F5F7FA;
  border-radius: 6px;
}

.peak-label {
  font-weight: 500;
  color: #606266;
}

.peak-value {
  font-weight: bold;
  color: #409EFF;
  margin: 0 10px;
}

.peak-time {
  font-size: 12px;
  color: #909399;
}

.behavior-analysis {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.behavior-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px;
  background-color: #F5F7FA;
  border-radius: 6px;
}

.behavior-label {
  font-weight: 500;
  color: #606266;
}

.behavior-value {
  font-weight: bold;
  color: #67C23A;
}

/* 配置对话框样式 */
.config-unit {
  margin-left: 10px;
  color: #909399;
  font-size: 12px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .status-item {
    flex-direction: column;
    text-align: center;
  }
  
  .status-icon {
    margin-right: 0;
    margin-bottom: 10px;
  }
  
  .peak-item,
  .behavior-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 5px;
  }
}

/* 动画效果 */
.status-card {
  transition: all 0.3s ease;
}

.status-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.status-value {
  transition: all 0.3s ease;
}

/* 表格增强样式 */
.el-table .el-tag {
  font-weight: 500;
}

/* 工具栏样式 */
.el-row .el-button {
  margin-right: 10px;
}

.el-row .el-button:last-child {
  margin-right: 0;
}
</style>