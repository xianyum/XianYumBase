<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="金价时间" clearable>
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
          type="success"
          plain
          icon="el-icon-share"
          size="mini"
          @click="viewGoldPriceReport"
        >实时报表</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>
    <el-table ref="tables" v-loading="loading" :data="list" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="品种" align="center" prop="variety"/>
      <el-table-column label="最新价" align="center" prop="latestPrice"/>
      <el-table-column label="开盘价" align="center" prop="openPrice"/>
      <el-table-column label="最高价" align="center" prop="maxPrice"/>
      <el-table-column label="最低价" align="center" prop="minPrice"/>
      <el-table-column label="涨跌幅" align="center" prop="changePercentage"/>
      <el-table-column label="昨收价" align="center" prop="yesPrice"/>
      <el-table-column label="总成交量" align="center" prop="totalVol"/>
      <el-table-column label="统计日期" align="center" prop="time">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.time) }}</span>
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
import { getGoldPricePage } from '@/api/monitor/goldPrice'
import { Message } from 'element-ui'

export default {
  name: "GoldPrice",
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
  created() {
    this.getList();
  },
  methods: {
    viewGoldPriceReport(){
      Message.info("正在开发中...");
    },
    /** 查询登录日志 */
    getList() {
      this.loading = true;
      getGoldPricePage(this.addDateRange(this.queryParams, this.dateRange)).then( response => {
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
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    }
  }
};
</script>

