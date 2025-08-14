<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="统计时间" prop="statTime">
        <el-date-picker clearable v-model="queryParams.statTime" type="date" value-format="yyyy-MM-dd"
          placeholder="请选择统计时间">
        </el-date-picker>
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

    <!-- 统计图表 -->
    <el-card v-show="showChart" class="mb8">
      <div slot="header">
        <span>流量统计图表</span>
      </div>
      <div ref="chart" style="height: 400px;"></div>
    </el-card>

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
  </div>
</template>

<script>
import { listTrafficStats } from "@/api/redpacket/trafficStats";
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
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        statTime: null,
        activeUsers: null,
        queuedUsers: null,
      }
    };
  },
  created() {
    this.getList();
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
      const activeUsers = this.trafficStatsList.map(item => item.activeUsers);
      const queuedUsers = this.trafficStatsList.map(item => item.queuedUsers);
      const totalRequests = this.trafficStatsList.map(item => item.totalRequests);
      const rejectedRequests = this.trafficStatsList.map(item => item.rejectedRequests);

      const option = {
        title: {
          text: '流量统计趋势图'
        },
        tooltip: {
          trigger: 'axis'
        },
        legend: {
          data: ['活跃用户', '排队用户', '总请求', '拒绝请求']
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        toolbox: {
          feature: {
            saveAsImage: {}
          }
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: times
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            name: '活跃用户',
            type: 'line',
            stack: 'Total',
            data: activeUsers,
            itemStyle: { color: '#67C23A' }
          },
          {
            name: '排队用户',
            type: 'line',
            stack: 'Total',
            data: queuedUsers,
            itemStyle: { color: '#E6A23C' }
          },
          {
            name: '总请求',
            type: 'line',
            data: totalRequests,
            itemStyle: { color: '#409EFF' }
          },
          {
            name: '拒绝请求',
            type: 'line',
            data: rejectedRequests,
            itemStyle: { color: '#F56C6C' }
          }
        ]
      };

      this.chart.setOption(option);
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
</style>