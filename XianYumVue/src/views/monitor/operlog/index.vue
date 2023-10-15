<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="账号" prop="username">
        <el-input
          v-model="queryParams.username"
          placeholder="请输入账号"
          clearable
          style="width: 240px;"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="操作时间" clearable>
        <el-date-picker
          v-model="dateRange"
          style="width: 240px"
          value-format="yyyy-MM-dd HH:mm:ss"
          type="daterange"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          :default-time="['00:00:00', '23:59:59']"
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
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          @click="handleClean"
        >清空</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table ref="tables" v-loading="loading" :data="list" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="日志编号" align="center" prop="id" width="100"/>
      <el-table-column label="账号" align="center" prop="username" width="120"/>
      <el-table-column label="用户操作" align="center" prop="operation" width="180"/>
      <el-table-column label="请求方法" align="center" prop="method" width="150" :show-overflow-tooltip="true"/>
      <el-table-column label="请求参数" align="center" prop="params" width="250" :show-overflow-tooltip="true"/>
      <el-table-column label="执行时长" align="center" prop="time">
        <template v-slot="scope">
          <span>{{ scope.row.time }}ms</span>
        </template>
      </el-table-column>
      <el-table-column label="IP地址" align="center" prop="ip" width="150"/>
      <el-table-column label="IP地点" align="center" prop="ipInfo" width="180"/>
      <el-table-column label="创建日期" align="center" prop="createTime" width="180">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>

      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="200">
        <template v-slot="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleView(scope.row)"
          >查看请求参数</el-button>
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

    <!-- 操作日志详细 -->
    <el-dialog title="请求参数详细" :visible.sync="open" width="700px" append-to-body>
      <json-viewer
        :value="requestParam" :expand-depth=6
        copyable
        boxed
        sort></json-viewer>
      <div slot="footer" class="dialog-footer">
        <el-button @click="open = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { list, delOperLog, cleanOperLog } from "@/api/monitor/operlog";
import JsonViewer from 'vue-json-viewer';

export default {
  name: "Operlog",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 表格数据
      list: [],
      // 是否显示弹出层
      open: false,
      // 日期范围
      dateRange: [this.parseTime(new Date(new Date().getFullYear(),new Date().getMonth(),new Date().getDate(),0,0,0)),this.parseTime(new Date(new Date().getFullYear(),new Date().getMonth(),new Date().getDate(),23,59,59))],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        username: undefined
      },
      requestParam: {}
    };
  },
  components: { JsonViewer },
  created() {
    this.getList();
  },
  methods: {
    /** 查询登录日志 */
    getList() {
      this.loading = true;
      list(this.addDateRange(this.queryParams, this.dateRange)).then( response => {
          this.list = response.data;
          this.total = response.total;
          this.loading = false;
        }
      );
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.dateRange = [this.parseTime(new Date(new Date().getFullYear(),new Date().getMonth(),new Date().getDate(),0,0,0))
        ,this.parseTime(new Date(new Date().getFullYear(),new Date().getMonth(),new Date().getDate(),23,59,59))];
      this.resetForm("queryForm");
      this.queryParams.pageNum = 1;
    },
    /** 多选框选中数据 */
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.multiple = !selection.length
    },
    /** 详细按钮操作 */
    handleView(row) {
      this.open = true;
      this.requestParam = JSON.parse(row.params)
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const operIds = row.id ? [row.id] : this.ids;
      this.$modal.confirm('是否确认删除日志编号为"' + operIds + '"的数据项？').then(function() {
        return delOperLog(operIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 清空按钮操作 */
    handleClean() {
      this.$modal.confirm('是否确认清空所有操作日志数据项？').then(function() {
        return cleanOperLog();
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("清空成功");
      }).catch(() => {});
    }
  }
};
</script>

