<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="85px">
      <el-form-item label="商品名称" prop="productName">
        <el-input
          v-model="queryParams.productName"
          placeholder="请输入商品名称"
          clearable
          style="width: 240px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="运营商" prop="operator">
        <el-select
          v-model="queryParams.operator"
          placeholder="请选择运营商"
          clearable
          style="width: 240px"
        >
          <el-option
            v-for="item in operatorList"
            :key="item.code"
            :label="item.code"
            :value="item.code"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
        <el-button type="success" icon="el-icon-s-shop" size="mini" @click="clickShop">号卡店铺</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="商品ID" width="80" align="center" prop="productID" />
      <el-table-column label="商品主图" align="center" prop="mainPic" >
        <template v-slot="scope">
          <el-image
            style="width: 100px; height: 100px"
            :src="scope.row.mainPic"
            :preview-src-list="getPicList(scope.row)"
            ></el-image>
        </template>
      </el-table-column>
      <el-table-column label="商品名称" align="center" prop="productName" >
        <template v-slot="scope">
          <div>{{scope.row.productName}}</div>
          <div style="color: #7b00ff">年龄：{{scope.row.age1}}-{{scope.row.age2}}周岁 </div>
        </template>
      </el-table-column>
      <el-table-column label="运营商" align="center" prop="operator" />
      <el-table-column label="归属地" align="center" prop="area" />
      <el-table-column label="佣金" align="center" prop="sPrice" >
        <template v-slot="scope">
          <div v-if="scope.row.sprice >= 100" style="color: #ff00b7">￥{{scope.row.sprice}}</div>
          <div v-else >￥{{scope.row.sprice}}</div>
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark" >
        <template v-slot="scope">
          <div>{{scope.row.remark}}</div>
          <div style="color: #FF0000">结算规则：{{scope.row.rule}}</div>
        </template>
      </el-table-column>
      <el-table-column label="禁发区域" align="center" prop="disableArea" :show-overflow-tooltip="true" />
      <el-table-column label="添加时间" align="center" prop="startTime" width="180">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>

      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template v-slot="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-share"
            @click="goMyUrl(scope.row)"
          >专属链接</el-button>
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
import { getHaoKaLotPage } from "@/api/monitor/haoKaLot.js";

export default {
  name: "JobLog",
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
      // 调度日志表格数据
      dataList: [],
      // 是否显示弹出层
      open: false,
      // 日期范围
      dateRange: [],
      // 表单参数
      form: {},
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        productName: undefined,
        operator: undefined
      },
      operatorList:[
        {
          code: '移动'
        },
        {
          code: '电信'
        }, {
          code: '联通'
        }, {
          code: '广电'
        }
      ]
    };
  },
  created() {
    this.getList();
  },
  methods: {
    clickShop(){
      const w = window.open("about:blank");
      w.location.href = 'https://haokawx.lot-ml.com/Product/Shop/134856'
    },
    getPicList(row){
      let picList = []
      picList.push(row.mainPic)
      if(row.littlepicture){
        let littlePics = row.littlepicture.split('|')
        littlePics.forEach(item => {
          if(item && item != ''){
            picList.push(item)
          }
        })
      }
      return picList;
    },
    goMyUrl(row){
      let url = `https://haokawx.lot-ml.com/h5order/index?pudID=${row.productID}&userid=${row.userID}`;
      const w = window.open("about:blank");
      w.location.href = url
    },
    /** 查询调度日志列表 */
    getList() {
      this.loading = true;
      getHaoKaLotPage(this.queryParams).then(response => {
          this.dataList = response.data;
          this.total = response.total;
          this.loading = false;
        }
      );
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.queryParams.jobId = undefined;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.dateRange = [];
      this.resetForm("queryForm");
      this.queryParams.jobId = undefined
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id);
      this.multiple = !selection.length;
    }
  }
}
</script>
