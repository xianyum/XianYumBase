<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="85px">
      <el-form-item label="客户端ID" prop="proxyId">
        <el-input
          v-model="queryParams.proxyId"
          placeholder="请输入客户端ID"
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

    <el-table v-loading="loading" :data="detailsList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="序号" type="index" align="center" width="50"/>
      <el-table-column label="客户端名称" align="center" prop="proxyName" />
      <el-table-column label="主机ip" align="center" prop="hostIp" width="130"/>
      <el-table-column label="操作系统" align="center" prop="osName" width="140"/>
      <el-table-column label="当前版本号" align="center" prop="clientVersion" >
        <template v-slot="scope">
          <span v-if="scope.row.clientVersion">V {{ scope.row.clientVersion }}</span>
        </template>
      </el-table-column>
      <el-table-column label="内存信息" align="center" prop="memoryInfo" :show-overflow-tooltip="true" :formatter='formatMemoryInfo'/>
      <el-table-column label="cpu使用率" align="center" prop="cpuUseAge" >
        <template v-slot="scope">
          <span v-if="scope.row.cpuUseAge">{{ scope.row.cpuUseAge }}%</span>
        </template>
      </el-table-column>
      <el-table-column label="运行路径" align="center" prop="userDir" :show-overflow-tooltip="true"/>
<!--      <el-table-column label="计算机名称" align="center" prop="computerName" />-->
      <el-table-column label="计算机用户" align="center" prop="computerUserName" />
      <el-table-column label="mac地址" align="center" prop="macAddress" :show-overflow-tooltip="true"/>
      <el-table-column label="使用时长" align="center" prop="onlineTime" />
      <el-table-column label="cpu信息" align="center" prop="cpuModel" :show-overflow-tooltip="true"/>
      <el-table-column label="使用时间" align="center" prop="createTime" width="160">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template v-slot="scope">
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
import { getProxyLogList, delProxyLog } from "@/api/xianyu/proxyLog";

export default {
  name: "Details",
  data() {
    return {
      proxyList: [],
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
      // 客户端配置详细表格数据
      detailsList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        proxyId: null
      },
      // 表单参数
      form: {}
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询客户端配置详细列表 */
    getList() {
      this.loading = true;
      getProxyLogList(this.queryParams).then(response => {
        this.detailsList = response.data;
        this.total = response.total;
        this.loading = false;
      });
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
        proxyId: null,
        inetPort: null,
        lan: null,
        name: null
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
      this.queryParams.proxyId = undefined
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
      this.reset();
      this.open = true;
      this.title = "添加代理配置";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
    },
    /** 提交按钮 */
    submitForm() {
    },
    formatMemoryInfo(row){
      if(!row.memoryInfo || row.memoryInfo === 'null'){
        return null;
      }
      let info = row.memoryInfo.replace("Available: ","")
      return info;
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id ? [row.id] : this.ids;
      this.$modal.confirm('删除审计数据会影响统计数据，请谨慎操作！！！').then(function() {
        return delProxyLog(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    }
  }
};
</script>
