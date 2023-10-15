<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="标题" prop="title">
        <el-input
          v-model="queryParams.title"
          placeholder="请输入标题"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="daoList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="序号" type="index" align="center" width="50"/>
      <el-table-column label="标题概要" align="center" prop="title" >
        <template v-slot="scope">
          <el-link type="primary" @click="toUrl(scope.row.url)">{{scope.row.title}}</el-link>
        </template>
      </el-table-column>
      <el-table-column label="推送状态" align="center" prop="pushStatus" >
        <template v-slot="scope">
          <el-tag v-if="scope.row.pushStatus === 0" size="small" type="danger">未推送</el-tag>
          <el-tag v-else size="small">已推送</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="推送时间" align="center" prop="pushTime" width="180">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.pushTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
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
import { getPageList} from "@/api/monitor/xiaodao";

export default {
  name: "Dao",
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
      // 【请填写功能名称】表格数据
      daoList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        title: null,
        url: null,
        time: null,
        pushStatus: null,
        pushTime: null,
        pushId: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    toUrl(url){
      window.open(url,'_blank')
    },
    /** 查询【请填写功能名称】列表 */
    getList() {
      this.loading = true;
      getPageList(this.queryParams).then(response => {
        this.daoList = response.data;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        title: null,
        url: null,
        time: null,
        pushStatus: null,
        pushTime: null,
        createTime: null,
        pushId: null
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
      this.single = selection.length !== 1
      this.multiple = !selection.length
    }
  }
};
</script>
