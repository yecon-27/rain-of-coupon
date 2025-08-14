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
      <el-form-item label="会话ID" prop="sessionId">
        <el-input
          v-model="queryParams.sessionId"
          placeholder="请输入会话ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="IP地址" prop="ipAddress">
        <el-input
          v-model="queryParams.ipAddress"
          placeholder="请输入IP地址"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="加入时间" prop="joinTime">
        <el-date-picker clearable
          v-model="queryParams.joinTime"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="yyyy-MM-dd">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option label="活跃" value="active" />
          <el-option label="排队中" value="queued" />
          <el-option label="已过期" value="expired" />
          <el-option label="已离开" value="left" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['redpacket:participants:export']"
        >导出</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="info"
          plain
          icon="el-icon-refresh"
          size="mini"
          @click="handleRefresh"
        >刷新</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="mb8">
      <el-col :span="6">
        <el-card class="box-card">
          <div slot="header">
            <span>活跃用户</span>
          </div>
          <div class="text item">
            <span class="count active">{{ statsData.activeCount }}</span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="box-card">
          <div slot="header">
            <span>排队用户</span>
          </div>
          <div class="text item">
            <span class="count queued">{{ statsData.queuedCount }}</span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="box-card">
          <div slot="header">
            <span>已过期</span>
          </div>
          <div class="text item">
            <span class="count expired">{{ statsData.expiredCount }}</span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="box-card">
          <div slot="header">
            <span>已离开</span>
          </div>
          <div class="text item">
            <span class="count left">{{ statsData.leftCount }}</span>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-table v-loading="loading" :data="participantsList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" width="80" />
      <el-table-column label="用户ID" align="center" prop="userId" width="120">
        <template slot-scope="scope">
          <span v-if="scope.row.userId">{{ scope.row.userId }}</span>
          <el-tag v-else type="info" size="mini">匿名</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="会话ID" align="center" prop="sessionId" width="200" :show-overflow-tooltip="true" />
      <el-table-column label="IP地址" align="center" prop="ipAddress" width="140" />
      <el-table-column label="加入时间" align="center" prop="joinTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.joinTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="离开时间" align="center" prop="leaveTime" width="160">
        <template slot-scope="scope">
          <span v-if="scope.row.leaveTime">{{ parseTime(scope.row.leaveTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
          <el-tag v-else type="success" size="mini">在线</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="最后心跳" align="center" prop="lastHeartbeat" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.lastHeartbeat, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status === 'active'" type="success">活跃</el-tag>
          <el-tag v-else-if="scope.row.status === 'queued'" type="warning">排队中</el-tag>
          <el-tag v-else-if="scope.row.status === 'expired'" type="danger">已过期</el-tag>
          <el-tag v-else-if="scope.row.status === 'left'" type="info">已离开</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="队列位置" align="center" prop="queuePosition" width="100">
        <template slot-scope="scope">
          <span v-if="scope.row.queuePosition">第{{ scope.row.queuePosition }}位</span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="120">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['redpacket:participants:edit']"
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

    <!-- 修改状态对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="用户ID" prop="userId">
          <el-input v-model="form.userId" disabled />
        </el-form-item>
        <el-form-item label="会话ID" prop="sessionId">
          <el-input v-model="form.sessionId" disabled />
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
import { listParticipants, getParticipants, updateParticipants } from "@/api/redpacket/participants";

export default {
  name: "Participants",
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
      // 活动参与者记录表格数据
      participantsList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 统计数据
      statsData: {
        activeCount: 0,
        queuedCount: 0,
        expiredCount: 0,
        leftCount: 0
      },
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        userId: null,
        sessionId: null,
        ipAddress: null,
        joinTime: null,
        status: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        status: [
          { required: true, message: "状态不能为空", trigger: "change" }
        ],
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询活动参与者记录列表 */
    getList() {
      this.loading = true;
      listParticipants(this.queryParams).then(response => {
        this.participantsList = response.rows;
        this.total = response.total;
        this.loading = false;
        this.updateStats();
      });
    },
    /** 更新统计数据 */
    updateStats() {
      this.statsData = {
        activeCount: this.participantsList.filter(item => item.status === 'active').length,
        queuedCount: this.participantsList.filter(item => item.status === 'queued').length,
        expiredCount: this.participantsList.filter(item => item.status === 'expired').length,
        leftCount: this.participantsList.filter(item => item.status === 'left').length
      };
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        status: null
      };
      this.resetForm("form");
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
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getParticipants(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改参与者状态";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          updateParticipants(this.form).then(response => {
            this.$modal.msgSuccess("修改成功");
            this.open = false;
            this.getList();
          });
        }
      });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('redpacket/participants/export', {
        ...this.queryParams
      }, `participants_${new Date().getTime()}.xlsx`)
    },
    /** 刷新按钮操作 */
    handleRefresh() {
      this.getList();
      this.$modal.msgSuccess("刷新成功");
    }
  }
};
</script>