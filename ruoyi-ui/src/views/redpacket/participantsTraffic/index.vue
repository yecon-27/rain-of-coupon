<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="用户" prop="userId">
        <el-input
          v-model="queryParams.userId"
          placeholder="请输入用户"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option label="活跃" value="active" />
          <el-option label="排队中" value="queued" />
          <el-option label="已过期" value="expired" />
          <el-option label="已离开" value="left" />
        </el-select>
      </el-form-item>
      <el-form-item label="创建时间">
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
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['redpacket:participantsTraffic:edit']"
        >修改状态</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['redpacket:participantsTraffic:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="participantsTrafficList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="用户" align="center" prop="userId">
        <template slot-scope="scope">
          <span v-if="scope.row.userId">{{ scope.row.userId }}</span>
          <el-tag v-else type="info" size="mini">匿名</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status === 'active'" type="success">活跃</el-tag>
          <el-tag v-else-if="scope.row.status === 'queued'" type="warning">排队中</el-tag>
          <el-tag v-else-if="scope.row.status === 'expired'" type="danger">已过期</el-tag>
          <el-tag v-else-if="scope.row.status === 'left'" type="info">已离开</el-tag>
          <el-tag v-else type="default">{{ scope.row.status }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createdAt" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createdAt, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['redpacket:participantsTraffic:edit']"
          >修改状态</el-button>
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

    <!-- 修改参与者状态对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="用户ID" prop="userId">
          <el-input v-model="form.userId" disabled />
        </el-form-item>
        <el-form-item label="当前状态" prop="currentStatus">
          <el-tag v-if="form.status === 'active'" type="success">活跃</el-tag>
          <el-tag v-else-if="form.status === 'queued'" type="warning">排队中</el-tag>
          <el-tag v-else-if="form.status === 'expired'" type="danger">已过期</el-tag>
          <el-tag v-else-if="form.status === 'left'" type="info">已离开</el-tag>
        </el-form-item>
        <el-form-item label="修改为" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="active">活跃</el-radio>
            <el-radio label="queued">排队中</el-radio>
            <el-radio label="expired">已过期</el-radio>
            <el-radio label="left">已离开</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listParticipantsTraffic, getParticipantsTraffic, delParticipantsTraffic, addParticipantsTraffic, updateParticipantsTraffic } from "@/api/redpacket/participantsTraffic"

export default {
  name: "ParticipantsTraffic",
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
      // 红包活动参与者记录表格数据
      participantsTrafficList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 加入时间范围
      daterangeJoinTime: [],
      // 离开时间范围
      daterangeLeaveTime: [],
      // 最后心跳时间范围
      daterangeLastHeartbeat: [],
      // 创建时间范围
      daterangeCreatedAt: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        userId: null,
        sessionId: null,
        ipAddress: null,
        joinTime: null,
        leaveTime: null,
        lastHeartbeat: null,
        status: null,
        queuePosition: null,
        createdAt: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        sessionId: [
          { required: true, message: "会话ID不能为空", trigger: "blur" }
        ],
        joinTime: [
          { required: true, message: "加入时间不能为空", trigger: "blur" }
        ],
        lastHeartbeat: [
          { required: true, message: "最后心跳时间不能为空", trigger: "blur" }
        ],
        status: [
          { required: true, message: "状态不能为空", trigger: "change" }
        ],
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询红包活动参与者记录列表 */
    getList() {
      this.loading = true
      this.queryParams.params = {}
      if (null != this.daterangeJoinTime && '' != this.daterangeJoinTime) {
        this.queryParams.params["beginJoinTime"] = this.daterangeJoinTime[0]
        this.queryParams.params["endJoinTime"] = this.daterangeJoinTime[1]
      }
      if (null != this.daterangeLeaveTime && '' != this.daterangeLeaveTime) {
        this.queryParams.params["beginLeaveTime"] = this.daterangeLeaveTime[0]
        this.queryParams.params["endLeaveTime"] = this.daterangeLeaveTime[1]
      }
      if (null != this.daterangeLastHeartbeat && '' != this.daterangeLastHeartbeat) {
        this.queryParams.params["beginLastHeartbeat"] = this.daterangeLastHeartbeat[0]
        this.queryParams.params["endLastHeartbeat"] = this.daterangeLastHeartbeat[1]
      }
      if (null != this.daterangeCreatedAt && '' != this.daterangeCreatedAt) {
        this.queryParams.params["beginCreatedAt"] = this.daterangeCreatedAt[0]
        this.queryParams.params["endCreatedAt"] = this.daterangeCreatedAt[1]
      }
      listParticipantsTraffic(this.queryParams).then(response => {
        this.participantsTrafficList = response.rows
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
        userId: null,
        sessionId: null,
        ipAddress: null,
        joinTime: null,
        leaveTime: null,
        lastHeartbeat: null,
        status: null,
        queuePosition: null,
        createdAt: null,
        updatedAt: null
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
      this.daterangeJoinTime = []
      this.daterangeLeaveTime = []
      this.daterangeLastHeartbeat = []
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
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const id = row.id || this.ids
      getParticipantsTraffic(id).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改参与者状态"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          updateParticipantsTraffic(this.form).then(response => {
            this.$modal.msgSuccess("状态修改成功")
            this.open = false
            this.getList()
          })
        }
      })
    },

    /** 导出按钮操作 */
    handleExport() {
      this.download('redpacket/participantsTraffic/export', {
        ...this.queryParams
      }, `participantsTraffic_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
