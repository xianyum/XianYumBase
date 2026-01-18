<template>
  <view class="driving-record-page">
    <!-- 统计卡片区：紧凑版通栏卡片 -->
    <view class="stats-container">
      <!-- 本月卡片 -->
      <view class="stats-card">
        <view class="card-header">本月数据</view>
        <view class="card-content">
          <view class="data-item">
            <view class="item-label">行驶里程</view>
            <view class="item-value">{{ stats.currentMonthResponse.totalDistanceKm }}<span class="unit">公里</span>
            </view>
          </view>
          <view class="split-line"></view>
          <view class="data-item">
            <view class="item-label">电耗总量</view>
            <view class="item-value">{{ stats.currentMonthResponse.totalElectricityConsumed }}<span
                class="unit">度</span></view>
          </view>
          <view class="split-line"></view>
          <view class="data-item">
            <view class="item-label">平均电耗</view>
            <view class="item-value">{{ stats.currentMonthResponse.electricityPerKm }}<span class="unit">度/公里</span>
            </view>
          </view>
        </view>
      </view>

      <!-- 上月卡片 -->
      <view class="stats-card">
        <view class="card-header">上月数据</view>
        <view class="card-content">
          <view class="data-item">
            <view class="item-label">行驶里程</view>
            <view class="item-value">{{ stats.lastMonthResponse.totalDistanceKm }}<span class="unit">公里</span></view>
          </view>
          <view class="split-line"></view>
          <view class="data-item">
            <view class="item-label">电耗总量</view>
            <view class="item-value">{{ stats.lastMonthResponse.totalElectricityConsumed }}<span class="unit">度</span>
            </view>
          </view>
          <view class="split-line"></view>
          <view class="data-item">
            <view class="item-label">平均电耗</view>
            <view class="item-value">{{ stats.lastMonthResponse.electricityPerKm }}<span class="unit">度/公里</span>
            </view>
          </view>
        </view>
      </view>

      <!-- 近一年卡片 -->
      <view class="stats-card">
        <view class="card-header">近一年数据</view>
        <view class="card-content">
          <view class="data-item">
            <view class="item-label">行驶里程</view>
            <view class="item-value">{{ stats.lastYearResponse.totalDistanceKm }}<span class="unit">公里</span></view>
          </view>
          <view class="split-line"></view>
          <view class="data-item">
            <view class="item-label">电耗总量</view>
            <view class="item-value">{{ stats.lastYearResponse.totalElectricityConsumed }}<span class="unit">度</span>
            </view>
          </view>
          <view class="split-line"></view>
          <view class="data-item">
            <view class="item-label">平均电耗</view>
            <view class="item-value">{{ stats.lastYearResponse.electricityPerKm }}<span class="unit">度/公里</span>
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- 折线图区域：新增数据类型切换按钮 -->
    <view class="chart-container">
      <view class="chart-header">
        <view class="chart-title">趋势</view>
        <view class="chart-switch-group">
          <!-- 数据类型切换 -->
          <view class="chart-switch data-type-switch">
            <view
                class="switch-btn"
                :class="{active: chartDataType === 'mileagePower'}"
                @click="switchChartDataType('mileagePower')"
            >
              里程&电量
            </view>
            <view
                class="switch-btn"
                :class="{active: chartDataType === 'avgPower'}"
                @click="switchChartDataType('avgPower')"
            >
              平均电耗
            </view>
          </view>
          <!-- 时间范围切换 -->
          <view class="chart-switch time-range-switch">
            <view
                class="switch-btn"
                :class="{active: dateType === 'day'}"
                @click="switchChartType('day')"
            >
              近一周
            </view>
            <view
                class="switch-btn"
                :class="{active: dateType === 'month'}"
                @click="switchChartType('month')"
            >
              近半年
            </view>
          </view>
        </view>
      </view>
      <view class="charts-box">
        <!-- 第一组：行驶公里数 + 消耗电量 -->
        <qiun-data-charts
            v-if="chartDataType === 'mileagePower'"
            type="line"
            :opts="mileagePowerOpts"
            :chartData="mileagePowerChartData"
            :key="`mileage-${dateType}`"
        />
        <!-- 第二组：平均电耗 -->
        <qiun-data-charts
            v-if="chartDataType === 'avgPower'"
            type="line"
            :opts="avgPowerOpts"
            :chartData="avgPowerChartData"
            :key="`power-${dateType}`"
        />
      </view>
    </view>
  </view>
</template>

<script>
import {getAppSummaryData, getEvDriveRecordsReportLineData} from "@/api/ev-drive/list";

export default {
  name: 'DrivingRecord',
  data() {
    return {
      // 统计数据
      stats: {
        currentMonthResponse: {},
        lastMonthResponse: {},
        lastYearResponse: {}
      },
      // 图表数据类型切换：mileagePower(里程&电量) / avgPower(平均电耗)
      chartDataType: 'mileagePower',
      // 时间范围切换：day(近7天) / month(近12月)
      dateType: 'day',
      // 第一组图表数据：行驶公里数 + 消耗电量
      mileagePowerChartData: {},
      // 第二组图表数据：平均电耗
      avgPowerChartData: {},
      // 第一组图表配置（里程&电量）- 多Y轴配置方式
      mileagePowerOpts: {
        color: ["#1890FF", "#EE6666"], // 对应：行驶公里数、消耗电量
        padding: [15, 10, 0, 15],
        enableScroll: false,
        legend: {},
        xAxis: {
          disableGrid: true
        },
        yAxis: {
          gridType: "dash",
          dashLength: 2,
          data: [
            {
              min: 0,    // 基础最小值
              max: 0,    // 动态计算赋值
              step: 0,   // 动态计算赋值
              precision: 0 // 整数刻度
            }
          ]
        },
        extra: {
          line: {
            type: "straight",
            width: 2,
            activeType: "hollow"
          }
        }
      },
      // 第二组图表配置（平均电耗）- 多Y轴配置方式
      avgPowerOpts: {
        color: ["#52c41a"], // 对应：平均电耗
        padding: [15, 10, 0, 15],
        enableScroll: false,
        legend: {},
        xAxis: {
          disableGrid: true
        },
        yAxis: {
          gridType: "dash",
          dashLength: 2,
          data: [
            {
              min: 0,      // 基础最小值
              max: 0,      // 动态计算赋值
              step: 0,     // 动态计算赋值
              precision: 2 // 保留2位小数
            }
          ]
        },
        extra: {
          line: {
            type: "straight",
            width: 2,
            activeType: "hollow"
          }
        }
      }
    };
  },
  onReady() {
    this.getAppSummaryData();
    this.getEvDriveChartData();
  },
  onPullDownRefresh() {
    this.refreshData();
  },
  methods: {
    async refreshData(){
      try {
        await Promise.all([this.getAppSummaryData(), this.getEvDriveChartData()]);
        uni.stopPullDownRefresh();
        this.$modal.msgSuccess("刷新成功")
      }catch (error) {
        uni.stopPullDownRefresh();
        this.$modal.msgError("刷新失败")
      }
    },
    // 获取统计卡片数据
    async getAppSummaryData() {
      const response = await getAppSummaryData();
      if (response.code === 200) {
        this.stats = response.data;
      }
    },

    // 切换数据类型（里程&电量 / 平均电耗）
    switchChartDataType(type) {
      if (this.chartDataType === type) return;
      this.chartDataType = type;
    },

    // 切换时间范围（近一周 / 近半年）
    switchChartType(type) {
      if (this.dateType === type) return;
      this.dateType = type;
      this.getEvDriveChartData();
    },

    // 获取图表数据并动态计算Y轴刻度
    async getEvDriveChartData() {
      let queryParams = {
        'dateType': this.dateType === 'day' ? 0 : 1,
      }
      const response = await getEvDriveRecordsReportLineData(queryParams);
      if (response.code === 200) {
        // 初始化数据数组
        const categories = []; // X轴标签（日期/月份）
        const mileageData = []; // 行驶公里数
        const powerData = []; // 消耗电量
        const avgPowerData = []; // 平均电耗

        // 1. 先对原始数据进行截取
        let filteredData = [...response.data]; // 复制原始数据
        if (this.dateType === 'day') {
          // 按天：只保留最后7条（近7天）
          filteredData = filteredData.slice(-9);
        } else {
          // 按月：只保留最后6条（近6个月）
          filteredData = filteredData.slice(-6);
        }

        // 处理接口返回的原始数据
        filteredData.forEach(item => {
          // 格式化日期：根据dateType处理不同的显示格式
          let dateLabel = '';
          if (this.dateType === 'day') {
            // 日维度：2024-04-03 → 4/3
            const date = new Date(item.driveDate);
            dateLabel = `${date.getMonth() + 1}/${date.getDate()}`;
          } else {
            const dateParts = item.driveDate.split('-');
            // 取年份后两位 + 月份（补0到两位）
            const year = dateParts[0].slice(-2); // 截取年份最后两位
            const month = dateParts[1].padStart(2, '0'); // 月份补0到两位
            dateLabel = `${year}/${month}`;
          }

          // 填充各类数据（确保为数字类型，空值赋值0）
          categories.push(dateLabel);
          mileageData.push(Number(item.totalDistanceKm) || 0);
          powerData.push(Number(item.totalElectricityConsumed) || 0);
          avgPowerData.push(Number(item.electricityPerKm) || 0);
        });

        // --- 核心：动态计算Y轴刻度（适配yAxis.data配置）---
        // 1. 里程&电量图表：动态计算max和step
        const maxMileagePower = Math.max(...mileageData, ...powerData);
        // 确保最大值不为0（避免全0数据）
        const finalMaxMileage = maxMileagePower === 0 ? 10 : maxMileagePower * 1.2;
        this.mileagePowerOpts.yAxis.data[0].max = finalMaxMileage;
        this.mileagePowerOpts.yAxis.data[0].step = finalMaxMileage / 5; // 分成5个刻度

        // 2. 平均电耗图表：动态计算max和step
        const maxAvgPower = Math.max(...avgPowerData);
        // 确保最大值不为0（避免全0数据，默认给0.1）
        const finalMaxAvg = maxAvgPower === 0 ? 0.1 : maxAvgPower * 1.2;
        this.avgPowerOpts.yAxis.data[0].max = finalMaxAvg;
        this.avgPowerOpts.yAxis.data[0].step = finalMaxAvg / 5; // 分成5个刻度
        // 确保step至少为0.01（避免刻度太密）
        if (this.avgPowerOpts.yAxis.data[0].step < 0.01) {
          this.avgPowerOpts.yAxis.data[0].step = 0.01;
        }

        // 调试打印：查看计算结果
        console.log('平均电耗最大值：', maxAvgPower);
        console.log('平均电耗Y轴max：', finalMaxAvg);
        console.log('平均电耗Y轴step：', this.avgPowerOpts.yAxis.data[0].step);

        // 组装第一组图表数据（里程&电量）
        this.mileagePowerChartData = {
          categories: categories,
          series: [
            {name: "行驶公里数", data: mileageData},
            {name: "消耗电量", data: powerData}
          ]
        };

        // 组装第二组图表数据（平均电耗）
        this.avgPowerChartData = {
          categories: categories,
          series: [
            {name: "平均电耗", data: avgPowerData}
          ]
        };
      }
    }
  }
}
</script>

<style scoped>
/* 页面基础样式 */
.driving-record-page {
  padding: 15px;
  background-color: #f5f5f5;
  min-height: 100vh;
  box-sizing: border-box;
}

/* 统计区容器 */
.stats-container {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 18px;
}

/* 通栏统计卡片 */
.stats-card {
  background: #ffffff;
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.03);
  overflow: hidden;
}

/* 卡片头部 */
.card-header {
  padding: 8px 15px;
  font-size: 14px;
  font-weight: 600;
  color: #333;
  background-color: #f8f9fa;
  border-bottom: 1px solid #eee;
  line-height: 1.2;
}

/* 卡片内容 */
.card-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 10px;
}

/* 单个数据项 */
.data-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
}

.item-label {
  font-size: 12px;
  color: #999;
  margin-bottom: 4px;
  line-height: 1;
}

.item-value {
  font-size: 16px;
  font-weight: 700;
  color: #333;
  display: flex;
  align-items: baseline;
  gap: 3px;
  line-height: 1;
}

.unit {
  font-size: 10px;
  color: #666;
  font-weight: 400;
}

/* 竖线分割 */
.split-line {
  width: 1px;
  height: 30px;
  background-color: #eee;
}

/* 图表容器 */
.chart-container {
  background: #fff;
  border-radius: 8px;
  padding: 12px 10px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.03);
}

/* 图表头部（标题+切换按钮） */
.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  padding-left: 5px;
}

.chart-title {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  line-height: 1.2;
}

/* 切换按钮组容器 */
.chart-switch-group {
  display: flex;
  gap: 8px;
  flex-wrap: nowrap; /* 强制不换行 */
  align-items: center;
}

/* 通用切换按钮样式 */
.chart-switch {
  display: flex;
  gap: 4px; /* 减小按钮内部间距 */
  background: #f5f5f5;
  border-radius: 12px;
  padding: 2px;
  white-space: nowrap; /* 防止按钮文本换行 */
}

/* 数据类型切换按钮 */
.data-type-switch {
  background: #f5f5f5;
}

/* 时间范围切换按钮 */
.time-range-switch {
  background: #f5f5f5;
}

.switch-btn {
  padding: 4px 10px; /* 减小按钮内边距，缩小按钮宽度 */
  font-size: 11px; /* 减小字体大小 */
  color: #666;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s ease;
  white-space: nowrap; /* 防止按钮文本换行 */
}

.switch-btn.active {
  color: #fff;
  background-color: #1890FF;
}

/* 图表容器样式 */
.charts-box {
  width: 100%;
  height: 260px;
}
</style>