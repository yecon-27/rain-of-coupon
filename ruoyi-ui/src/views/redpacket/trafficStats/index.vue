<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="统计时间">
        <el-date-picker
          v-model="daterangeStatTime"
          style="width: 240px"
          value-format="yyyy-MM-dd"
          type="daterange"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
        ></el-date-picker>
      </el-form-item>
      <el-form-item label="活跃用户数" prop="activeUsers">
        <el-input
          v-model="queryParams.activeUsers"
          placeholder="请输入活跃用户数"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="排队用户数" prop="queuedUsers">
        <el-input
          v-model="queryParams.queuedUsers"
          placeholder="请输入排队用户数"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="总请求数" prop="totalRequests">
        <el-input
          v-model="queryParams.totalRequests"
          placeholder="请输入总请求数"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="被拒绝的请求数" prop="rejectedRequests">
        <el-input
          v-model="queryParams.rejectedRequests"
          placeholder="请输入被拒绝的请求数"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="${comment}">
        <el-date-picker
          v-model="daterangeCreatedAt"
          style="width: 240px"
          value-format="yyyy-MM-dd"
          type="daterange"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
        ></el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['redpacket:trafficStats:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['redpacket:trafficStats:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['redpacket:trafficStats:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['redpacket:trafficStats:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="trafficStatsList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="${comment}" align="center" prop="id" />
      <el-table-column label="统计时间" align="center" prop="statTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.statTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="活跃用户数" align="center" prop="activeUsers" />
      <el-table-column label="排队用户数" align="center" prop="queuedUsers" />
      <el-table-column label="总请求数" align="center" prop="totalRequests" />
      <el-table-column label="被拒绝的请求数" align="center" prop="rejectedRequests" />
      <el-table-column label="平均会话时间" align="center" prop="averageSessionTime" />
      <el-table-column label="${comment}" align="center" prop="createdAt" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createdAt, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['redpacket:trafficStats:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['redpacket:trafficStats:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改红包流量统计对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listTrafficStats, getTrafficStats, delTrafficStats, addTrafficStats, updateTrafficStats } from "@/api/redpacket/trafficStats"

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
      // 总条数
      total: 0,
      // 红包流量统计表格数据
      trafficStatsList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // $comment时间范围
      daterangeStatTime: [],
      // $comment时间范围
      daterangeCreatedAt: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        statTime: null,
        activeUsers: null,
        queuedUsers: null,
        totalRequests: null,
        rejectedRequests: null,
        createdAt: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        statTime: [
          { required: true, message: "统计时间不能为空", trigger: "blur" }
        ],
        activeUsers: [
          { required: true, message: "活跃用户数不能为空", trigger: "blur" }
        ],
        queuedUsers: [
          { required: true, message: "排队用户数不能为空", trigger: "blur" }
        ],
        totalRequests: [
          { required: true, message: "总请求数不能为空", trigger: "blur" }
        ],
        rejectedRequests: [
          { required: true, message: "被拒绝的请求数不能为空", trigger: "blur" }
        ],
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询红包流量统计列表 */
    getList() {
      this.loading = true
      this.queryParams.params = {}
      if (null != this.daterangeStatTime && '' != this.daterangeStatTime) {
        this.queryParams.params["beginStatTime"] = this.daterangeStatTime[0]
        this.queryParams.params["endStatTime"] = this.daterangeStatTime[1]
      }
      if (null != this.daterangeCreatedAt && '' != this.daterangeCreatedAt) {
        this.queryParams.params["beginCreatedAt"] = this.daterangeCreatedAt[0]
        this.queryParams.params["endCreatedAt"] = this.daterangeCreatedAt[1]
      }
      listTrafficStats(this.queryParams).then(response => {
        this.trafficStatsList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    // 取消按钮
    cancel() {
      this.open = false
      this.reset()
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        statTime: null,
        activeUsers: null,
        queuedUsers: null,
        totalRequests: null,
        rejectedRequests: null,
        averageSessionTime: null,
        createdAt: null
      }
      this.resetForm("form")
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.daterangeStatTime = []
      this.daterangeCreatedAt = []
      this.resetForm("queryForm")
      this.handleQuery()
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加红包流量统计"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const id = row.id || this.ids
      getTrafficStats(id).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改红包流量统计"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateTrafficStats(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addTrafficStats(this.form).then(response => {
              this.$modal.msgSuccess("新增成功")
              this.open = false
              this.getList()
            })
          }
        }
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids
      this.$modal.confirm('是否确认删除红包流量统计编号为"' + ids + '"的数据项？').then(function() {
        return delTrafficStats(ids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('redpacket/trafficStats/export', {
        ...this.queryParams
      }, `trafficStats_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
