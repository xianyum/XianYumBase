<template>
  <div class="app-container">
    <el-card style="width:100%; clear:both;">
      <div slot="header" class="clearfix">
        <span>最新金价</span>
      </div>
      <el-form :model="goldPriceData" label-width="110px">
        <el-row :gutter="1">
          <el-col :xs="24" :sm="8" :md="6" :lg="3">
            <el-form-item label="品种：" prop="variety">
              {{ goldPriceData.variety }}
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="8" :md="6" :lg="3">
            <el-form-item label="最新价：" prop="latestPrice" style="color: red;font-weight: bold">
              {{ goldPriceData.latestPrice }}
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="8" :md="6" :lg="3" style="color: #1c84c6;font-weight: bold">
            <el-form-item label="昨收价：" prop="yesPrice">
              {{ goldPriceData.yesPrice }}
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :xs="24" :sm="8" :md="6" :lg="3">
            <el-form-item label="开盘价：" prop="openPrice">
              {{ goldPriceData.openPrice }}
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="8" :md="6" :lg="3">
            <el-form-item label="最高价：" prop="maxPrice">
              {{ goldPriceData.maxPrice }}
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="8" :md="6" :lg="3">
            <el-form-item label="最低价：" prop="minPrice">
              {{ goldPriceData.minPrice }}
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :xs="24" :sm="8" :md="6" :lg="3">
            <el-form-item label="涨跌幅：" prop="changePercentage">
              {{ goldPriceData.changePercentage }}
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="8" :md="6" :lg="3">
            <el-form-item label="总成交量：" prop="totalVol">
              {{ goldPriceData.totalVol }}
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="8" :md="6" :lg="4">
            <el-form-item label="统计时间：" prop="time">
              {{ parseTime(goldPriceData.time) }}
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-card>

    <el-card style="width:100%;margin-top: 6px; clear:both;">
      <div slot="header" class="clearfix">
        <span>金价趋势</span>
      </div>
      <div>
        开发中...
      </div>
    </el-card>
  </div>
</template>

<script>
import { getLatestPrice } from '@/api/monitor/goldPrice'

export default {
  name: "index",
  data() {
    return {
      goldPriceData: {},
      proxyLogList: []
    }
  },
  created() {
    this.getLatestPriceRequest();
  },
  methods: {
    getLatestPriceRequest() {
      getLatestPrice().then(response => {
          this.goldPriceData = response.data;
        }
      );
    },
  }
}
</script>
