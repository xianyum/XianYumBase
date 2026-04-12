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
              近一年
            </view>
          </view>
        </view>
      </view>
      <view class="charts-box">
        <!-- 里程&电量折线图 - 新增触摸事件和ref引用 -->
        <qiun-data-charts
            v-if="chartDataType === 'mileagePower'"
            type="line"
            :opts="mileagePowerOpts"
            :chartData="mileagePowerChartData"
            :key="`mileage-${dateType}-${JSON.stringify(mileagePowerOpts.yAxis.data[0])}`"
            :ontouch="true"
            @touchstart.stop="handleChartTouch"
            ref="mileagePowerChartRef"
        />
        <!-- 平均电耗折线图 - 新增触摸事件和ref引用 -->
        <qiun-data-charts
            v-if="chartDataType === 'avgPower'"
            type="line"
            :opts="avgPowerOpts"
            :chartData="avgPowerChartData"
            :key="`power-${dateType}-${JSON.stringify(avgPowerOpts.yAxis.data[0])}`"
            :ontouch="true"
            @touchstart.stop="handleChartTouch"
            ref="avgPowerChartRef"
        />
      </view>
    </view>

    <liu-drag-button
        @clickBtn="goToAiReport"
        :bottomPx="345"
        :canDocking="true"
        class="ai-drag-btn"
    >AI分析
    </liu-drag-button>

  </view>
</template>

<script>
import {getAppSummaryData, getEvDriveRecordsReportLineData, evDriveRecordsDoAiAnalysis} from "@/api/ev-drive/list";


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
      // 里程&电量图表配置 - 开启X轴滚动
      mileagePowerOpts: {
        color: ["#1890FF", "#EE6666"],
        padding: [15, 0, 0, 0],
        enableScroll: true, // 核心：开启滚动功能
        legend: {
          orient: "horizontal",
          bottom: 0,
          left: 0,
          right: 0,
          itemGap: 8,
          textStyle: {
            fontSize: 11,
            color: "#666"
          }
        },
        xAxis: {
          disableGrid: true,
          scrollShow: true, // 显示滚动条
          itemCount: 7, // 一屏显示7个数据点
          scrollAlign: 'right', // 滚动对齐到右侧（最新数据）
          scrollable: true, // 开启X轴滚动
          bounce: false // 关闭回弹效果
        },
        yAxis: {
          gridType: "dash",
          dashLength: 2,
          data: [{min: 0, max: 0, step: 0, precision: 0}]
        },
        extra: {
          line: {type: "straight", width: 2, activeType: "hollow"},
          scroll: {
            type: 'horizontal', // 横向滚动
            threshold: 10 // 滚动触发阈值
          }
        }
      },
      // 平均电耗图表配置 - 开启X轴滚动
      avgPowerOpts: {
        color: ["#52c41a"],
        padding: [15, 0, 0, 0],
        enableScroll: true, // 核心：开启滚动功能
        legend: {
          orient: "horizontal",
          bottom: 0,
          left: 0,
          right: 0,
          itemGap: 8,
          textStyle: {
            fontSize: 11,
            color: "#666"
          }
        },
        xAxis: {
          disableGrid: true,
          scrollShow: true, // 显示滚动条
          itemCount: 7, // 一屏显示7个数据点
          scrollAlign: 'right', // 滚动对齐到右侧（最新数据）
          scrollable: true, // 开启X轴滚动
          bounce: false // 关闭回弹效果
          // ,format: 'xAxisDemo1'
        },
        yAxis: {
          gridType: "dash",
          dashLength: 2,
          data: [{min: 0, max: 0, step: 0, precision: 2}]
        },
        extra: {
          line: {type: "straight", width: 2, activeType: "hollow"},
          scroll: {
            type: 'horizontal', // 横向滚动
            threshold: 10 // 滚动触发阈值
          }
        }
      }
    };
  },
  // 图表ref引用
  refs: {
    mileagePowerChartRef: null,
    avgPowerChartRef: null
  },
  onReady() {
    this.getAppSummaryData();
    this.getEvDriveChartData();
  },
  onPullDownRefresh() {
    this.refreshData();
  },
  methods: {
    /**
     * 处理图表触摸事件，阻止事件穿透
     */
    handleChartTouch() {
      return false;
    },

    /**
     * 滚动图表到最新数据位置
     */
    scrollToLatestData() {
      this.$nextTick(() => {
        const categoryLength = this.mileagePowerChartData.categories?.length || 0;
        // 根据当前选中的图表类型滚动对应图表
        if (this.chartDataType === 'mileagePower' && this.$refs.mileagePowerChartRef && this.$refs.mileagePowerChartRef.scrollTo) {
          this.$refs.mileagePowerChartRef.scrollTo({
            x: categoryLength - 8,
            y: 0,
            animate: false
          });
        } else if (this.chartDataType === 'avgPower' && this.$refs.avgPowerChartRef && this.$refs.avgPowerChartRef.scrollTo) {
          this.$refs.avgPowerChartRef.scrollTo({
            x: categoryLength - 8,
            y: 0,
            animate: false
          });
        }
      });
    },

    /**
     * 刷新页面数据
     */
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

    /**
     * 获取统计数据
     */
    async getAppSummaryData() {
      try {
        const response = await getAppSummaryData();
        if (response.code === 200) {
          this.stats = {
            currentMonthResponse: typeof response.data.currentMonthResponse === 'object' && response.data.currentMonthResponse !== null
                ? response.data.currentMonthResponse
                : {},
            lastMonthResponse: typeof response.data.lastMonthResponse === 'object' && response.data.lastMonthResponse !== null
                ? response.data.lastMonthResponse
                : {},
            lastYearResponse: typeof response.data.lastYearResponse === 'object' && response.data.lastYearResponse !== null
                ? response.data.lastYearResponse
                : {}
          };
        } else {
          this.stats = {
            currentMonthResponse: {},
            lastMonthResponse: {},
            lastYearResponse: {}
          };
        }
      } catch (error) {
        console.error('获取统计数据失败：', error);
        this.stats = {
          currentMonthResponse: {},
          lastMonthResponse: {},
          lastYearResponse: {}
        };
      }
    },

    /**
     * 切换图表数据类型（里程&电量/平均电耗）
     */
    switchChartDataType(type) {
      if (this.chartDataType === type) return;
      this.chartDataType = type;
      // 切换后滚动到最新数据
      this.$nextTick(() => {
        this.scrollToLatestData();
      });
    },

    /**
     * 切换时间范围（近一周/近半年）
     */
    switchChartType(type) {
      if (this.dateType === type) return;
      this.dateType = type;
      this.getEvDriveChartData();
    },

    /**
     * 获取图表数据
     */
    async getEvDriveChartData() {
      try {
        let queryParams = {'dateType': this.dateType === 'day' ? 0 : 1};
        const response = await getEvDriveRecordsReportLineData(queryParams);
        if (response.code === 200) {
          const categories = [];
          const mileageData = [];
          const powerData = [];
          const avgPowerData = [];

          // 保留完整数据用于滚动（移除原有的数据截取）
          let filteredData = [...response.data];
          filteredData = this.dateType === 'day' ? filteredData.slice(-7) : filteredData.slice(-12);

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

          // 计算里程&电量Y轴范围
          const maxMileagePower = Math.max(...mileageData, ...powerData);
          const finalMaxMileage = maxMileagePower === 0 ? 10 : maxMileagePower * 1.2;
          const mileageStep = finalMaxMileage / 5;
          this.mileagePowerOpts.yAxis.data = [{
            min: 0,
            max: finalMaxMileage,
            step: mileageStep,
            precision: 0
          }];

          // 计算平均电耗Y轴范围
          const maxAvgPower = Math.max(...avgPowerData);
          const finalMaxAvg = maxAvgPower === 0 ? 0.1 : maxAvgPower * 1.2;
          let avgStep = finalMaxAvg / 5;
          avgStep = avgStep < 0.01 ? 0.01 : avgStep;
          this.avgPowerOpts.yAxis.data = [{
            min: 0,
            max: finalMaxAvg,
            step: avgStep,
            precision: 2
            // unit: '度/公里'
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

          // 数据加载完成后滚动到最新数据
          this.scrollToLatestData();
        }
      } catch (error) {
        console.error('获取图表数据失败：', error);
      }
    },
    async goToAiReport() {
      const response = await evDriveRecordsDoAiAnalysis();
      const title = "行驶数据近24小时AI分析报告";
      const content = response.msg || "";
      uni.navigateTo({
        url: `/pages/common/markdown/markdown-view?markdownTitle=${encodeURIComponent(title)}&markdownContent=${encodeURIComponent(content)}`
      });
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
  border-radius: 16px;
  padding: 2px;
  white-space: nowrap;
}
.switch-btn {
  padding: 2px 8px;
  font-size: 10px;
  color: #666;
  border-radius: 14px;
  cursor: pointer;
  transition: all 0.2s ease;
  white-space: nowrap;
  line-height: 20px;
}
.switch-btn.active {
  color: #fff;
  background-color: #1890FF;
  box-shadow: 0 1px 2px rgba(24, 144, 255, 0.2);
}
.charts-box {
  width: 100%;
  height: 260px;
  background: #fff;
  min-height: 260px;
  user-select: none;
  -webkit-user-select: none;
}

/* 只当前页面生效的AI悬浮按钮样式 */
::v-deep .ai-drag-btn .movable-view {
  width: 100rpx !important;
  height: 100rpx !important;
  background: linear-gradient(360deg, #cb28f8 0%, #6EA8FF 100%) !important;
  box-shadow: 0px 4rpx 12rpx 0px #ADC3F8 !important;
  border-radius: 50rpx !important;
  color: #FFFFFF !important;
  font-size: 26rpx !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
}
</style>