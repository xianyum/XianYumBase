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
              {{ goldPriceData.latestPrice }}元
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="8" :md="6" :lg="3" style="color: #1c84c6;font-weight: bold">
            <el-form-item label="昨收价：" prop="yesPrice">
              {{ goldPriceData.yesPrice }}元
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="8" :md="6" :lg="3">
            <el-form-item label="开盘价：" prop="openPrice">
              {{ goldPriceData.openPrice }}元
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="8" :md="6" :lg="3">
            <el-form-item label="涨跌幅：" prop="changePercentage">
              {{ goldPriceData.changePercentage }}
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :xs="24" :sm="8" :md="6" :lg="3">
            <el-form-item label="最低价：" prop="minPrice">
              {{ goldPriceData.minPrice }}元
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="8" :md="6" :lg="3">
            <el-form-item label="最高价：" prop="maxPrice">
              {{ goldPriceData.maxPrice }}元
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="8" :md="6" :lg="3">
            <el-form-item label="总成交量：" prop="totalVol">
              {{ goldPriceData.totalVol }}
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="8" :md="6" :lg="5">
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
          <gold-price-trend :chart-data="goldPriceTrendChartData"/>
      </div>
    </el-card>
    <el-card style="width:100%;margin-top: 6px; clear:both;">
      <div slot="header" class="clearfix">
        <span>每日K线</span>
      </div>
      <div>
        <gold-price-by-day-k-line :chart-data="goldPriceKLineData"/>
      </div>
    </el-card>
  </div>
</template>

<script>
import { getGoldPriceLatestPrice, getGoldPriceTrend , getGoldPriceKLine} from '@/api/monitor/goldPrice'
import GoldPriceTrend from '@/views/dashboard/GoldPriceTrend'
import { parseTime } from '@/utils/ruoyi'
import GoldPriceByDayKLine from '@/views/dashboard/GoldPriceByDayKLine'

export default {
  name: 'index',
  components: { GoldPriceByDayKLine, GoldPriceTrend },
  data() {
    return {
      goldPriceKLineData: [],
      goldPriceData: {},
      goldPriceTrendChartData: {
        data: [],
        date: []
      }
    }
  },
  created() {
    this.getLatestPriceRequest()
    this.getGoldPriceTrendRequest()
    this.getGoldPriceKLineData()
  },
  methods: {
    getGoldPriceKLineData(){
      getGoldPriceKLine().then(response => {
          this.goldPriceKLineData = response.data
        }
      )
    },
    getLatestPriceRequest() {
      getGoldPriceLatestPrice().then(response => {
          this.goldPriceData = response.data
        }
      )
    },
    getGoldPriceTrendRequest() {
      getGoldPriceTrend().then(response => {
          response.data.forEach((item, index) => {
            this.goldPriceTrendChartData.data.push(item.latestPrice)
            this.goldPriceTrendChartData.date.push(parseTime(new Date(item.time)))
          })

        }
      )
    }
  }
}
</script>
