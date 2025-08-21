<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="用户ID" prop="userId">
        <el-input
          v-model="queryParams.userId"
          placeholder="请输入用户ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="参与时间">
        <el-date-picker
          v-model="daterangeParticipationTime"
          style="width: 240px"
          value-format="yyyy-MM-dd"
          type="daterange"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
        ></el-date-picker>
      </el-form-item>
      <el-form-item label="IP记录" prop="ipAddress">
        <el-input
          v-model="queryParams.ipAddress"
          placeholder="请输入IP记录"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="是否中奖(0未中奖 1中奖)" prop="isWin">
        <el-input
          v-model="queryParams.isWin"
          placeholder="请输入是否中奖(0未中奖 1中奖)"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="奖品名称" prop="prizeName">
        <el-input
          v-model="queryParams.prizeName"
          placeholder="请输入奖品名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="是否使用(0未使用 1已使用)" prop="isUsed">
        <el-input
          v-model="queryParams.isUsed"
          placeholder="请输入是否使用(0未使用 1已使用)"
          clearable
          @keyup.enter.native="handleQuery"
        />
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
          v-hasPermi="['redpacket:participationLog:add']"
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
          v-hasPermi="['redpacket:participationLog:edit']"
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
          v-hasPermi="['redpacket:participationLog:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['redpacket:participationLog:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="participationLogList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="用户ID" align="center" prop="userId" />
      <el-table-column label="参与时间" align="center" prop="participationTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.participationTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="IP记录" align="center" prop="ipAddress" />
      <el-table-column label="是否中奖(0未中奖 1中奖)" align="center" prop="isWin" />
      <el-table-column label="奖品名称" align="center" prop="prizeName" />
      <el-table-column label="是否使用(0未使用 1已使用)" align="center" prop="isUsed" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['redpacket:participationLog:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['redpacket:participationLog:remove']"
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

    <!-- 添加或修改用户参与记录（记录所有参与行为）对话框 -->
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
import { listParticipationLog, getParticipationLog, delParticipationLog, addParticipationLog, updateParticipationLog } from "@/api/redpacket/participationLog"

export default {
  name: "ParticipationLog",
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
      // 用户参与记录（记录所有参与行为）表格数据
      participationLogList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 是否使用(0未使用 1已使用)时间范围
      daterangeParticipationTime: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        userId: null,
        participationTime: null,
        ipAddress: null,
        isWin: null,
        prizeId: null,
        prizeName: null,
        isUsed: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        userId: [
          { required: true, message: "用户ID不能为空", trigger: "blur" }
        ],
        participationTime: [
          { required: true, message: "参与时间不能为空", trigger: "blur" }
        ],
        ipAddress: [
          { required: true, message: "IP记录不能为空", trigger: "blur" }
        ],
        isWin: [
          { required: true, message: "是否中奖(0未中奖 1中奖)不能为空", trigger: "blur" }
        ],
        isUsed: [
          { required: true, message: "是否使用(0未使用 1已使用)不能为空", trigger: "blur" }
        ]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询用户参与记录（记录所有参与行为）列表 */
    getList() {
      this.loading = true
      this.queryParams.params = {}
      if (null != this.daterangeParticipationTime && '' != this.daterangeParticipationTime) {
        this.queryParams.params["beginParticipationTime"] = this.daterangeParticipationTime[0]
        this.queryParams.params["endParticipationTime"] = this.daterangeParticipationTime[1]
      }
      listParticipationLog(this.queryParams).then(response => {
        this.participationLogList = response.rows
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
        participationTime: null,
        ipAddress: null,
        isWin: null,
        prizeId: null,
        prizeName: null,
        isUsed: null
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
      this.daterangeParticipationTime = []
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
      this.title = "添加用户参与记录（记录所有参与行为）"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const id = row.id || this.ids
      getParticipationLog(id).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改用户参与记录（记录所有参与行为）"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateParticipationLog(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addParticipationLog(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除用户参与记录（记录所有参与行为）编号为"' + ids + '"的数据项？').then(function() {
        return delParticipationLog(ids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('redpacket/participationLog/export', {
        ...this.queryParams
      }, `participationLog_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
