<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="消息编码" prop="messageCode">
        <el-input
          v-model="queryParams.messageCode"
          placeholder="请输入消息编码"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="消息标题" prop="messageTitle">
        <el-input
          v-model="queryParams.messageTitle"
          placeholder="请输入消息标题"
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
        >删除</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="proxyList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="序号" type="index" align="center" width="50" />
      <el-table-column label="消息标题" align="center" prop="messageTitle" />
      <el-table-column label="消息编码" align="center" prop="messageCode" width="400"/>
      <el-table-column label="消息类型描述" align="center" prop="messageTypeDescription" />
      <el-table-column label="状态" align="center" prop="status">
        <template v-slot="scope">
          <el-tag v-if="scope.row.status === 1" size="small" type="danger">禁用</el-tag>
          <el-tag v-else type="success" size="small">启用</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建日期" align="center" prop="createTime" width="180">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template v-slot="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
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

  </div>
</template>

<script>
import { getMessageEmailConfigPage, delMessageSendConfig } from '@/api/message/messageSendConfig'


export default {
  name: "Email",
  data() {
    return {
      sendEmailLoading: false,
      drawer: false,
      direction: 'rtl',
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
      // 客户端管理表格数据
      proxyList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        messageCode: undefined,
        messageTitle: undefined
      },
      // 表单参数
      form: {}
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询客户端管理列表 */
    getList() {
      this.loading = true;
      getMessageEmailConfigPage(this.queryParams).then(response => {
        this.proxyList = response.data.records;
        this.total = response.data.total;
        this.loading = false;
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
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.$router.push({ name: "SendConfigAddOrUpdate", params: {'id': undefined} });
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      const id = row.id || this.ids
      this.$router.push({ name: "SendConfigAddOrUpdate", params: {'id': id} });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id ?[row.id] : this.ids;
      this.$modal.confirm('是否确认删除发送配置数据？').then(function() {
        return delMessageSendConfig(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    }
  }
}
</script>
