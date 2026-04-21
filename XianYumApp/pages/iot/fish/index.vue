<template>
  <view class="env-monitor-page">
    <!-- 顶部标题 -->
    <view class="page-header">
      <text class="header-title">环境监测中心</text>
      <text class="update-time">数据更新于：{{ updateTime }}</text>
      <text class="water-change-time" v-if="waterChangeLastTime">最近换水：{{ waterChangeLastTime }}</text>
    </view>

    <!-- 实时数据卡片 -->
    <uni-row :gutter="15">
      <uni-col :span="12" v-for="(item, index) in realTimeData" :key="index">
        <view class="data-card">
          <text class="data-label">{{ item.label }}</text>
          <view class="value-wrap">
            <text class="data-value">{{ item.value }}{{ item.unit }}</text>
            <view class="data-trend" :class="item.trend === 'up' ? 'trend-up' : item.trend === 'down' ? 'trend-down' : 'trend-stable'">
              <text>{{ item.trend === 'up' ? '↑' : item.trend === 'down' ? '↓' : '—' }}</text>
            </view>
          </view>
        </view>
      </uni-col>
    </uni-row>

    <!-- 折线图：拆分两组 + 切换按钮 -->
    <uni-row :gutter="15" class="mt-20">
      <uni-col :span="24">
        <view class="chart-section">
          <!-- 标题 + 切换按钮 -->
          <view class="section-header">
            <text class="section-title">环境趋势对比</text>
            <view class="chart-tabs">
              <text
                  class="tab-item"
                  :class="{active: chartType === '0'}"
                  @click="switchChartType('0')"
              >
                温度数据
              </text>
              <text
                  class="tab-item"
                  :class="{active: chartType === '1'}"
                  @click="switchChartType('1')"
              >
                湿度&TDS
              </text>
              <!-- 保留原有时间范围切换 -->
              <view class="chart-tabs time-tabs">
                <text
                    class="tab-item"
                    :class="{active: timeRange === '0'}"
                    @click="switchTimeRange('0')"
                >
                  近24小时
                </text>
                <text
                    class="tab-item"
                    :class="{active: timeRange === '1'}"
                    @click="switchTimeRange('1')"
                >
                  近31天
                </text>
              </view>
            </view>
          </view>

          <!-- 第一组：室内温度 + 鱼缸温度 -->
          <view class="charts-box line-box" v-if="chartType === '0'">
            <qiun-data-charts
                type="line"
                :opts="tempLineOpts"
                :chartData="tempLineChartData"
                :ontouch="true"
                @touchstart.stop="handleChartTouch"
                ref="tempLineChartRef"
            />
          </view>

          <!-- 第二组：室内湿度 + 鱼缸TDS -->
          <view class="charts-box line-box" v-if="chartType === '1'">
            <qiun-data-charts
                type="line"
                :opts="humTdsLineOpts"
                :chartData="humTdsLineChartData"
                :ontouch="true"
                @touchstart.stop="handleChartTouch"
                ref="humTdsLineChartRef"
            />
          </view>
        </view>
      </uni-col>
    </uni-row>

    <!-- 环形图 -->
    <uni-row :gutter="15" class="mt-15">
      <uni-col :span="12">
        <view class="arcbar-item">
          <text class="arcbar-title">室内湿度</text>
          <view class="charts-box arcbar-box">
            <qiun-data-charts
                type="arcbar"
                :opts="humArcbarOpts"
                :chartData="humArcbarData"
            />
          </view>
        </view>
      </uni-col>
      <uni-col :span="12">
        <view class="arcbar-item">
          <text class="arcbar-title">TDS值</text>
          <view class="charts-box arcbar-box">
            <qiun-data-charts
                type="arcbar"
                :opts="tdsArcbarOpts"
                :chartData="tdsArcbarData"
            />
          </view>
        </view>
      </uni-col>
    </uni-row>

    <liu-drag-button
        @clickBtn="goToAiReport"
        :bottomPx="155"
        :canDocking="true"
        class="ai-drag-btn"
    >AI分析
    </liu-drag-button>

  </view>
</template>

<script>
import { formatTime } from '@/utils/dateFormat'
import { queryLatestData, getReportLineData,doAiAnalysis } from '@/api/iot/fish'

export default {
  data() {
    return {
      realTimeData: [],
      chartType: '0',
      timeRange: '0',
      backendLineData: {
        xaxisDataList: [],
        indoorTempList: [],
        fishTankTempList: [],
        indoorHumidityList: [],
        fishTankTdsList: []
      },
      tempLineChartData: {},
      tempLineOpts: {
        color: ["#1890FF", "#91CB74"],
        padding: [15, 0, 0, 0],
        enableScroll: true,
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
          scrollShow: true,
          itemCount: 7,
          scrollAlign: 'right',
          scrollable: true,
          bounce: false
        },
        yAxis: {
          gridType: "dash",
          dashLength: 2,
          data: [{ min: 0, max: 0 }]
        },
        extra: {
          line: { type: "straight", width: 2, activeType: "hollow" },
          scroll: { type: 'horizontal', threshold: 10 }
        }
      },
      humTdsLineChartData: {},
      humTdsLineOpts: {
        color: ["#FAC858", "#EE6666"],
        padding: [15, 0, 0, 0],
        enableScroll: true,
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
          scrollShow: true,
          itemCount: 7,
          scrollAlign: 'right',
          scrollable: true,
          bounce: false
        },
        yAxis: {
          gridType: "dash",
          dashLength: 2,
          data: [{ min: 0, max: 0 }]
        },
        extra: {
          line: { type: "straight", width: 2, activeType: "hollow" },
          scroll: { type: 'horizontal', threshold: 10 }
        }
      },
      humArcbarData: {},
      tdsArcbarData: {},
      tdsColorConfig: [
        { min: 0, max: 50, mainColor: "#1890FF", gradientColor: "#40a9ff", desc: "优质" },
        { min: 51, max: 100, mainColor: "#91CB74", gradientColor: "#2fc25b", desc: "良好" },
        { min: 101, max: 200, mainColor: "#FAC858", gradientColor: "#ffab91", desc: "一般" },
        { min: 201, max: 300, mainColor: "#EE6666", gradientColor: "#4d6bff", desc: "较差" },
        { min: 301, max: Infinity, mainColor: "#9A60B4", gradientColor: "#d12e2e", desc: "极差" }
      ],
      humArcbarOpts: {
        color: ["#91CB74"],
        title: { name: "0%", fontSize: 28, color: "#91CB74" },
        subtitle: { name: "室内湿度", fontSize: 20, color: "#666" },
        extra: {
          arcbar: {
            width: 10,
            backgroundColor: "#E9E9E9",
            startAngle: 0.75,
            endAngle: 0.25,
            gap: 2,
            linearType: "custom",
            linearColor: [[0, "#91CB74"], [1, "#2fc25b"]]
          }
        }
      },
      tdsArcbarOpts: {
        color: ["#EE6666"],
        title: { name: "0ppm", fontSize: 28, color: "#EE6666" },
        subtitle: { name: "TDS值", fontSize: 20, color: "#666" },
        extra: {
          arcbar: {
            width: 10,
            backgroundColor: "#E9E9E9",
            startAngle: 0.75,
            endAngle: 0.25,
            gap: 2,
            linearType: "custom",
            linearColor: [[0, "#EE6666"], [1, "#FA7D8D"]]
          }
        }
      },
      waterChangeLastTime: "",
      updateTime: "",
      timer: null
    };
  },
  onReady() {
    this.fetchEnvData();
    this.getLineData();
    this.timer = setInterval(() => {
      this.fetchEnvData();
      this.getLineData();
    }, 20000);
  },
  onUnload() {
    if (this.timer) {
      clearInterval(this.timer);
      this.timer = null;
    }
  },
  onLoad(options) {
    if (options.pageTitle) {
      uni.setNavigationBarTitle({
        title: options.pageTitle
      });
    }
  },
  methods: {
    handleChartTouch() {
      return false;
    },
    switchChartType(type) {
      this.chartType = type;
      this.$nextTick(() => {
        this.scrollToLatestData();
      });
    },
    switchTimeRange(range) {
      this.timeRange = range;
      this.getLineData();
    },
    formatXAxisLabel(label, range) {
      if (!label) return '';
      if (range === '0') {
        const hourMatch = label.match(/\s(\d{1,2})$/);
        if (hourMatch && hourMatch[1]) {
          return `${hourMatch[1]}时`;
        }
        const timeParts = label.split(/\s|:/);
        if (timeParts.length >= 2) {
          return `${timeParts[1]}时`;
        }
      } else if (range === '1') {
        const dateMatch = label.match(/(\d{4})-(\d{2})-(\d{2})/);
        if (dateMatch && dateMatch[2] && dateMatch[3]) {
          return `${dateMatch[2]}/${dateMatch[3]}`;
        }
      }
      return label;
    },
    calculateYAxisRange(dataLists) {
      const allData = dataLists.flat().filter(item => {
        return item !== null && item !== undefined && !isNaN(Number(item));
      });
      if (allData.length === 0) {
        return { min: 0, max: 100 };
      }
      const minVal = Math.min(...allData);
      const maxVal = Math.max(...allData);
      const range = maxVal - minVal;
      const padding = range * 0.05;
      let finalMin = minVal;
      let finalMax = maxVal;
      if (range > 0) {
        finalMin = minVal - padding;
        finalMax = maxVal + padding;
      } else {
        finalMin = minVal - 1;
        finalMax = maxVal + 1;
      }
      return {
        min: Number(finalMin.toFixed(1)),
        max: Number(finalMax.toFixed(1))
      };
    },
    async fetchEnvData() {
      try {
        const response = await queryLatestData();
        if (response.code === 200 && response.data) {
          const data = response.data;
          this.realTimeData = [
            { label: '室内温度', value: data.indoorTemp, unit: '℃', trend: data.indoorTempTrend },
            { label: '室内湿度', value: data.indoorHumidity, unit: '%RH', trend: data.indoorHumidityTrend },
            { label: '鱼缸水温', value: data.fishTankTemp, unit: '℃', trend: data.fishTankTempTrend },
            { label: 'TDS值', value: data.fishTankTds, unit: 'ppm', trend: data.fishTankTdsTrend }
          ];
          // this.waterChangeLastTime = data.waterChangeLastTime
          this.waterChangeLastTime = '2026-4-21 21:53:49'
          const createTime = new Date(data.createTime);
          this.updateTime = formatTime(createTime);
          this.updateArcbarData();
        }
      } catch (error) {
        console.error('获取环境监测数据失败：', error);
      }
    },
    updateArcbarData() {
      const humValue = this.realTimeData[1].value;
      this.humArcbarOpts.title.name = `${humValue}%`;
      this.humArcbarData = {
        series: [{ name: "室内湿度", color: "#91CB74", data: humValue / 100 }]
      };

      const tdsValue = this.realTimeData[3].value;
      const tdsConfig = this.getTdsConfigByValue(tdsValue);
      this.tdsArcbarOpts.title.name = `${tdsValue}ppm`;
      this.tdsArcbarOpts.title.color = tdsConfig.mainColor;
      this.tdsArcbarOpts.color = [tdsConfig.mainColor];
      this.tdsArcbarOpts.subtitle.name = `${tdsConfig.desc}`;
      this.tdsArcbarOpts.subtitle.color = tdsConfig.mainColor;
      this.tdsArcbarOpts.extra.arcbar.linearColor = [[0, tdsConfig.mainColor], [1, tdsConfig.gradientColor]];
      const tdsRatio = Math.min(tdsValue / 300, 1);
      this.tdsArcbarData = {
        series: [{ name: "TDS值", color: tdsConfig.mainColor, data: tdsRatio }]
      };
    },
    getTdsConfigByValue(value) {
      for (const config of this.tdsColorConfig) {
        if (value >= config.min && value <= config.max) {
          return config;
        }
      }
      return this.tdsColorConfig[this.tdsColorConfig.length - 1];
    },
    async getLineData() {
      try {
        const response = await getReportLineData({ dateType: this.timeRange });
        if (response.code === 200 && response.data) {
          this.backendLineData = response.data;
          const formattedCategories = this.backendLineData.xaxisDataList.map(label => {
            return this.formatXAxisLabel(label, this.timeRange);
          });

          this.tempLineChartData = {
            categories: formattedCategories,
            series: [
              { name: "室内温度", data: this.backendLineData.indoorTempList },
              { name: "鱼缸水温", data: this.backendLineData.fishTankTempList }
            ]
          };

          this.humTdsLineChartData = {
            categories: formattedCategories,
            series: [
              { name: "室内湿度", data: this.backendLineData.indoorHumidityList },
              { name: "鱼缸TDS", data: this.backendLineData.fishTankTdsList }
            ]
          };

          const tempRange = this.calculateYAxisRange([
            this.backendLineData.indoorTempList,
            this.backendLineData.fishTankTempList
          ]);
          this.tempLineOpts.yAxis.data[0].min = tempRange.min;
          this.tempLineOpts.yAxis.data[0].max = tempRange.max;

          const humTdsRange = this.calculateYAxisRange([
            this.backendLineData.indoorHumidityList,
            this.backendLineData.fishTankTdsList
          ]);
          this.humTdsLineOpts.yAxis.data[0].min = humTdsRange.min;
          this.humTdsLineOpts.yAxis.data[0].max = humTdsRange.max;

          this.scrollToLatestData();
        }
      } catch (error) {
        console.error('获取趋势数据失败：', error);
      }
    },
    scrollToLatestData() {
      this.$nextTick(() => {
        const categoryLength = this.backendLineData.xaxisDataList.length;
        if (this.chartType === '0' && this.$refs.tempLineChartRef && this.$refs.tempLineChartRef.scrollTo) {
          this.$refs.tempLineChartRef.scrollTo({
            x: categoryLength - 8,
            y: 0,
            animate: false
          });
        } else if (this.chartType === '1' && this.$refs.humTdsLineChartRef && this.$refs.humTdsLineChartRef.scrollTo) {
          this.$refs.humTdsLineChartRef.scrollTo({
            x: categoryLength - 8,
            y: 0,
            animate: false
          });
        }
      });
    },
    async goToAiReport() {
      const response = await doAiAnalysis();
      const title = "智能鱼缸AI分析报告";
      const content = response.msg || "";
      uni.navigateTo({
        url: `/pages/common/markdown/markdown-view?markdownTitle=${encodeURIComponent(title)}&markdownContent=${encodeURIComponent(content)}`
      });
    }
  }
};
</script>

<style scoped lang="scss">
.env-monitor-page {
  min-height: 100vh;
  background-color: #f5f7fa;
  padding: 15rpx;
  box-sizing: border-box;
}

.mt-15 { margin-top: 15rpx; }
.mt-20 { margin-top: 20rpx; }

.page-header {
  margin-bottom: 15rpx;
  .header-title {
    font-size: 32rpx;
    font-weight: 600;
    color: #303133;
  }
  .update-time {
    font-size: 20rpx;
    color: #909399;
    display: block;
    margin-top: 5rpx;
  }
  .water-change-time {
    font-size: 20rpx;
    color: #3a9ec2;
  }
}

.data-card {
  background-color: #fff;
  border-radius: 10rpx;
  padding: 20rpx 15rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.03);
  height: 120rpx;
  display: flex;
  flex-direction: column;
  justify-content: space-between;

  .data-label {
    font-size: 24rpx;
    color: #606266;
    line-height: 1.2;
  }

  .value-wrap {
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  .data-value {
    font-size: 34rpx;
    font-weight: 600;
    color: #303133;
    line-height: 1;
  }

  .data-trend {
    font-size: 22rpx;
    width: 36rpx;
    height: 36rpx;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
  }

  .trend-up { background-color: #fef0f0; color: #f56c6c; }
  .trend-down { background-color: #f0f9ff; color: #409eff; }
  .trend-stable { background-color: #f5f5f5; color: #909399; }
}

.chart-section {
  background-color: #fff;
  border-radius: 10rpx;
  padding: 15rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.03);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10rpx;
  > .chart-tabs {
    display: flex;
    align-items: center;
    height: 28px;
  }
}

.section-title {
  font-size: 26rpx;
  color: #303133;
  font-weight: 500;
  display: block;
  line-height: 28px;
}

.chart-tabs {
  display: flex;
  gap: 4px;
  background: #f5f5f5;
  border-radius: 16px;
  padding: 2px;
  box-sizing: border-box;
  align-items: center;
  flex-shrink: 0;

  &.time-tabs {
    margin-left: 8px;
  }
}

.tab-item {
  padding: 0 8px;
  font-size: 10px;
  color: #606266;
  border-radius: 14px;
  cursor: pointer;
  transition: all 0.2s ease;
  white-space: nowrap;
  height: 24px;
  line-height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-sizing: border-box;
  flex-shrink: 0;

  &.active {
    color: #fff;
    background-color: #1890FF;
    box-shadow: 0 1px 2px rgba(24, 144, 255, 0.2);
  }
}

.line-box {
  width: 100%;
  height: 300px;
  user-select: none;
  -webkit-user-select: none;
  box-sizing: border-box;
}

.arcbar-item {
  background-color: #fff;
  border-radius: 10rpx;
  padding: 15rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.03);
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.arcbar-title {
  font-size: 26rpx;
  color: #303133;
  font-weight: 500;
  display: block;
  margin-bottom: 10rpx;
  line-height: 1;
}

.arcbar-box {
  width: 100%;
  height: 200px;
  box-sizing: border-box;
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