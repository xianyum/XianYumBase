<template>
  <view class="driving-record-page">
    <!-- 统计卡片区 -->
    <view class="stats-container">
      <view class="stats-card">
        <view class="card-header">本月数据</view>
        <view class="card-content">
          <view class="data-item">
            <view class="item-label">行驶里程</view>
            <view class="item-value">{{ stats.currentMonthResponse.totalDistanceKm || 0 }}<span class="unit">公里</span></view>
          </view>
          <view class="split-line"></view>
          <view class="data-item">
            <view class="item-label">电耗总量</view>
            <view class="item-value">{{ stats.currentMonthResponse.totalElectricityConsumed || 0 }}<span class="unit">度</span></view>
          </view>
          <view class="split-line"></view>
          <view class="data-item">
            <view class="item-label">平均电耗</view>
            <view class="item-value">{{ stats.currentMonthResponse.electricityPerKm || 0 }}<span class="unit">度/公里</span></view>
          </view>
        </view>
      </view>
      <view class="stats-card">
        <view class="card-header">上月数据</view>
        <view class="card-content">
          <view class="data-item">
            <view class="item-label">行驶里程</view>
            <view class="item-value">{{ stats.lastMonthResponse.totalDistanceKm || 0 }}<span class="unit">公里</span></view>
          </view>
          <view class="split-line"></view>
          <view class="data-item">
            <view class="item-label">电耗总量</view>
            <view class="item-value">{{ stats.lastMonthResponse.totalElectricityConsumed || 0 }}<span class="unit">度</span></view>
          </view>
          <view class="split-line"></view>
          <view class="data-item">
            <view class="item-label">平均电耗</view>
            <view class="item-value">{{ stats.lastMonthResponse.electricityPerKm || 0 }}<span class="unit">度/公里</span></view>
          </view>
        </view>
      </view>
      <view class="stats-card">
        <view class="card-header">近一年数据</view>
        <view class="card-content">
          <view class="data-item">
            <view class="item-label">行驶里程</view>
            <view class="item-value">{{ stats.lastYearResponse.totalDistanceKm || 0 }}<span class="unit">公里</span></view>
          </view>
          <view class="split-line"></view>
          <view class="data-item">
            <view class="item-label">电耗总量</view>
            <view class="item-value">{{ stats.lastYearResponse.totalElectricityConsumed || 0 }}<span class="unit">度</span></view>
          </view>
          <view class="split-line"></view>
          <view class="data-item">
            <view class="item-label">平均电耗</view>
            <view class="item-value">{{ stats.lastYearResponse.electricityPerKm || 0 }}<span class="unit">度/公里</span></view>
          </view>
        </view>
      </view>
    </view>

    <!-- 折线图区域 -->
    <view class="chart-container">
      <view class="chart-header">
        <view class="chart-title">新能源趋势</view>
        <view class="chart-switch-group">
          <view class="chart-switch data-type-switch">
            <view class="switch-btn" :class="{active: chartDataType === 'mileagePower'}" @click="switchChartDataType('mileagePower')">
              里程&电量
            </view>
            <view class="switch-btn" :class="{active: chartDataType === 'avgPower'}" @click="switchChartDataType('avgPower')">
              平均电耗
            </view>
          </view>
          <view class="chart-switch time-range-switch">
            <view class="switch-btn" :class="{active: dateType === 'day'}" @click="switchChartType('day')">
              近一周
            </view>
            <view class="switch-btn" :class="{active: dateType === 'month'}" @click="switchChartType('month')">
              近半年
            </view>
          </view>
        </view>
      </view>
      <view class="charts-box">
        <!-- 修复1：key绑定组合值，确保切换必触发重绘，无闪烁 -->
        <qiun-data-charts
            v-if="chartDataType === 'mileagePower'"
            type="line"
            :opts="mileagePowerOpts"
            :chartData="mileagePowerChartData"
            :key="`mileage-${dateType}-${JSON.stringify(mileagePowerOpts.yAxis.data[0])}`"
        />
        <qiun-data-charts
            v-if="chartDataType === 'avgPower'"
            type="line"
            :opts="avgPowerOpts"
            :chartData="avgPowerChartData"
            :key="`power-${dateType}-${JSON.stringify(avgPowerOpts.yAxis.data[0])}`"
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
      stats: {
        currentMonthResponse: {},
        lastMonthResponse: {},
        lastYearResponse: {}
      },
      chartDataType: 'mileagePower',
      dateType: 'day',
      mileagePowerChartData: {},
      avgPowerChartData: {},
      mileagePowerOpts: {
        color: ["#1890FF", "#EE6666"],
        padding: [15, 0, 0, 0],
        enableScroll: false,
        legend: {},
        xAxis: {
          disableGrid: true
        },
        yAxis: {
          gridType: "dash",
          dashLength: 2,
          data: [{min: 0, max: 0, step: 0, precision: 0}]
        },
        extra: {
          line: {type: "straight", width: 2, activeType: "hollow"}
        }
      },
      avgPowerOpts: {
        color: ["#52c41a"],
        padding: [15, 0, 0, 0],
        enableScroll: false,
        legend: {},
        xAxis: {
          disableGrid: true
        },
        yAxis: {
          gridType: "dash",
          dashLength: 2,
          data: [{min: 0, max: 0, step: 0, precision: 2}]
        },
        extra: {
          line: {type: "straight", width: 2, activeType: "hollow"}
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
    async refreshData() {
      try {
        await Promise.all([this.getAppSummaryData(), this.getEvDriveChartData()]);
        uni.stopPullDownRefresh();
        this.$modal.msgSuccess("刷新成功");
      } catch (error) {
        uni.stopPullDownRefresh();
        this.$modal.msgError("刷新失败");
      }
    },
    async getAppSummaryData() {
      const response = await getAppSummaryData();
      if (response.code === 200) {
        this.stats = response.data;
      }
    },
    switchChartDataType(type) {
      if (this.chartDataType === type) return;
      this.chartDataType = type;
    },
    switchChartType(type) {
      if (this.dateType === type) return;
      this.dateType = type;
      this.getEvDriveChartData();
    },
    async getEvDriveChartData() {
      let queryParams = {'dateType': this.dateType === 'day' ? 0 : 1};
      const response = await getEvDriveRecordsReportLineData(queryParams);
      if (response.code === 200) {
        const categories = [];
        const mileageData = [];
        const powerData = [];
        const avgPowerData = [];

        let filteredData = [...response.data];
        filteredData = this.dateType === 'day' ? filteredData.slice(-9) : filteredData.slice(-6);

        filteredData.forEach(item => {
          let dateLabel = '';
          if (this.dateType === 'day') {
            const date = new Date(item.driveDate);
            dateLabel = `${date.getMonth() + 1}/${date.getDate()}`;
          } else {
            const dateParts = item.driveDate.split('-');
            const year = dateParts[0].slice(-2);
            const month = dateParts[1].padStart(2, '0');
            dateLabel = `${year}/${month}`;
          }
          categories.push(dateLabel);
          mileageData.push(Number(item.totalDistanceKm) || 0);
          powerData.push(Number(item.totalElectricityConsumed) || 0);
          avgPowerData.push(Number(item.electricityPerKm) || 0);
        });

        // 修复2：深拷贝重构Y轴配置，触发Vue响应式+图表重绘
        // 里程&电量Y轴计算
        const maxMileagePower = Math.max(...mileageData, ...powerData);
        const finalMaxMileage = maxMileagePower === 0 ? 10 : maxMileagePower * 1.2;
        const mileageStep = finalMaxMileage / 5;
        // 深拷贝重构，替代直接修改属性
        this.mileagePowerOpts.yAxis.data = [{
          min: 0,
          max: finalMaxMileage,
          step: mileageStep,
          precision: 0
        }];

        // 平均电耗Y轴计算
        const maxAvgPower = Math.max(...avgPowerData);
        const finalMaxAvg = maxAvgPower === 0 ? 0.1 : maxAvgPower * 1.2;
        let avgStep = finalMaxAvg / 5;
        avgStep = avgStep < 0.01 ? 0.01 : avgStep;
        // 深拷贝重构，替代直接修改属性
        this.avgPowerOpts.yAxis.data = [{
          min: 0,
          max: finalMaxAvg,
          step: avgStep,
          precision: 2
        }];

        // 组装图表数据
        this.mileagePowerChartData = {
          categories,
          series: [{name: "行驶公里数", data: mileageData}, {name: "消耗电量", data: powerData}]
        };
        this.avgPowerChartData = {
          categories,
          series: [{name: "平均电耗", data: avgPowerData}]
        };
      }
    }
  }
};
</script>

<style scoped>
.driving-record-page {
  padding: 15px;
  background-color: #f5f5f5;
  min-height: 100vh;
  box-sizing: border-box;
}
.stats-container {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 18px;
}
.stats-card {
  background: #ffffff;
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.03);
  overflow: hidden;
}
.card-header {
  padding: 8px 15px;
  font-size: 14px;
  font-weight: 600;
  color: #333;
  background-color: #f8f9fa;
  border-bottom: 1px solid #eee;
  line-height: 1.2;
}
.card-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 10px;
}
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
.split-line {
  width: 1px;
  height: 30px;
  background-color: #eee;
}
.chart-container {
  background: #fff;
  border-radius: 8px;
  padding: 12px 10px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.03);
}
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
.chart-switch-group {
  display: flex;
  gap: 8px;
  flex-wrap: nowrap;
  align-items: center;
}
.chart-switch {
  display: flex;
  gap: 4px;
  background: #f5f5f5;
  border-radius: 16px; /* 按比例缩小容器圆角 */
  padding: 2px;
  white-space: nowrap;
}
.switch-btn {
  padding: 2px 8px; /* 减少内边距，缩小按钮 */
  font-size: 10px; /* 缩小字体 */
  color: #666;
  border-radius: 14px; /* 按比例缩小按钮圆角 */
  cursor: pointer;
  transition: all 0.2s ease;
  white-space: nowrap;
  line-height: 20px; /* 降低行高，整体缩小按钮高度 */
}
.switch-btn.active {
  color: #fff;
  background-color: #1890FF;
  box-shadow: 0 1px 2px rgba(24, 144, 255, 0.2);
}
.charts-box {
  width: 100%;
  height: 260px;
  /* 修复3：添加背景和最小高度，防止重绘时空白闪烁 */
  background: #fff;
  min-height: 260px;
}
</style>