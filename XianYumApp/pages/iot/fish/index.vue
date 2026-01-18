<template>
  <view class="env-monitor-page">
    <!-- 顶部标题 -->
    <view class="page-header">
      <text class="header-title">环境监测中心</text>
      <text class="update-time">更新于：{{ updateTime }}</text>
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
  </view>
</template>

<script>
import { formatTime } from '@/utils/dateFormat'
import { queryLatestData, getReportLineData } from '@/api/iot/fish'

export default {
  data() {
    return {
      realTimeData: [],
      // 图表类型切换（0-温度数据，1-湿度&TDS）
      chartType: '0',
      // 时间范围切换状态（24h/7d）
      timeRange: '0',
      // 后端返回的原始趋势数据
      backendLineData: {
        xaxisDataList: [],
        indoorTempList: [],
        fishTankTempList: [],
        indoorHumidityList: [],
        fishTankTdsList: []
      },
      // 第一组：温度折线图（独立变量）
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
          data: [
            {
              min: 0,  // 动态计算
              max: 0   // 动态计算
            }
          ]
        },
        extra: {
          line: { type: "straight", width: 2, activeType: "hollow" },
          scroll: {
            type: 'horizontal',
            threshold: 10
          }
        }
      },
      // 第二组：湿度&TDS折线图（独立变量）
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
          data: [
            {
              min: 0,  // 动态计算
              max: 0   // 动态计算
            }
          ]
        },
        extra: {
          line: { type: "straight", width: 2, activeType: "hollow" },
          scroll: {
            type: 'horizontal',
            threshold: 10
          }
        }
      },
      // 环形图相关
      humArcbarData: {},
      tdsArcbarData: {},
      // TDS值颜色配置规则
      tdsColorConfig: [
        { min: 0, max: 50, mainColor: "#1890FF", gradientColor: "#40a9ff", desc: "优质" },
        { min: 51, max: 100, mainColor: "#91CB74", gradientColor: "#2fc25b", desc: "良好" },
        { min: 101, max: 200, mainColor: "#FAC858", gradientColor: "#ffab91", desc: "一般" },
        { min: 201, max: 300, mainColor: "#EE6666", gradientColor: "#4d6bff", desc: "较差" },
        { min: 301, max: Infinity, mainColor: "#9A60B4", gradientColor: "#d12e2e", desc: "极差" }
      ],
      // 环形图配置
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
      updateTime: "",
      timer: null
    };
  },
  refs: {
    tempLineChartRef: null,
    humTdsLineChartRef: null
  },
  onReady() {
    // 首次加载数据
    this.fetchEnvData();
    this.getLineData();

    // 设置定时器，每20秒更新一次数据
    this.timer = setInterval(() => {
      this.fetchEnvData();
      this.getLineData();
    }, 20000);
  },
  onUnload() {
    // 页面卸载时清除定时器
    if (this.timer) {
      clearInterval(this.timer);
      this.timer = null;
    }
  },
  methods: {
    /**
     * 处理图表触摸事件，阻止穿透
     */
    handleChartTouch() {
      return false;
    },
    /**
     * 切换图表类型（温度数据/湿度&TDS）
     */
    switchChartType(type) {
      this.chartType = type;
      // 切换后重新滚动到最新数据
      this.$nextTick(() => {
        this.scrollToLatestData();
      });
    },
    /**
     * 切换时间范围（24h/7d）
     */
    switchTimeRange(range) {
      this.timeRange = range;
      this.getLineData();
    },
    /**
     * 格式化X轴标签文本
     * @param {String} label 原始时间标签
     * @param {String} range 时间范围 0-24小时 1-7天
     * @returns {String} 格式化后的标签
     */
    formatXAxisLabel(label, range) {
      if (!label) return '';

      if (range === '0') {
        // 24小时模式：提取小时，格式化为 "17时"
        const hourMatch = label.match(/\s(\d{1,2})$/);
        if (hourMatch && hourMatch[1]) {
          return `${hourMatch[1]}时`;
        }
        // 兼容其他格式，提取小时部分
        const timeParts = label.split(/\s|:/);
        if (timeParts.length >= 2) {
          return `${timeParts[1]}时`;
        }
      } else if (range === '1') {
        // 7天模式：提取月/日，格式化为 "01/17"
        const dateMatch = label.match(/(\d{4})-(\d{2})-(\d{2})/);
        if (dateMatch && dateMatch[2] && dateMatch[3]) {
          return `${dateMatch[2]}/${dateMatch[3]}`;
        }
      }
      // 兜底返回原始值
      return label;
    },
    /**
     * 计算Y轴的最小值和最大值（带5%余量）
     * @param {Array} dataLists 多个数据系列的数组集合
     * @returns {Object} { min: 最小值, max: 最大值 }
     */
    calculateYAxisRange(dataLists) {
      // 合并所有数据并过滤掉无效值
      const allData = dataLists.flat().filter(item => {
        return item !== null && item !== undefined && !isNaN(Number(item));
      });

      if (allData.length === 0) {
        return { min: 0, max: 100 }; // 默认值
      }

      // 计算原始极值
      const minVal = Math.min(...allData);
      const maxVal = Math.max(...allData);
      const range = maxVal - minVal;

      // 计算5%的余量（避免数据贴边）
      const padding = range * 0.05;

      // 处理range为0的情况（所有数据相同）
      let finalMin = minVal;
      let finalMax = maxVal;

      if (range > 0) {
        finalMin = minVal - padding;
        finalMax = maxVal + padding;
      } else {
        // 所有数据相同，手动设置范围
        finalMin = minVal - 1;
        finalMax = maxVal + 1;
      }

      // 保留一位小数，避免精度问题
      return {
        min: Number(finalMin.toFixed(1)),
        max: Number(finalMax.toFixed(1))
      };
    },
    /**
     * 从接口获取环境监测实时数据
     */
    async fetchEnvData() {
      try {
        const response = await queryLatestData();
        if (response.code === 200 && response.data) {
          const data = response.data;

          // 更新实时数据
          this.realTimeData = [
            { label: '室内温度', value: data.indoorTemp, unit: '℃', trend: data.indoorTempTrend },
            { label: '室内湿度', value: data.indoorHumidity, unit: '%RH', trend: data.indoorHumidityTrend },
            { label: '鱼缸水温', value: data.fishTankTemp, unit: '℃', trend: data.fishTankTempTrend },
            { label: 'TDS值', value: data.fishTankTds, unit: 'ppm', trend: data.fishTankTdsTrend }
          ];

          // 更新更新时间（转换UTC时间为本地时间）
          const createTime = new Date(data.createTime);
          this.updateTime = formatTime(createTime);

          // 更新环形图数据
          this.updateArcbarData();
        }
      } catch (error) {
        console.error('获取环境监测数据失败：', error);
        uni.showToast({
          title: '数据加载失败',
          icon: 'none',
          duration: 2000
        });
      }
    },
    /**
     * 更新环形图数据
     */
    updateArcbarData() {
      // 室内湿度环形图
      const humValue = this.realTimeData[1].value;
      this.humArcbarOpts.title.name = `${humValue}%`;
      this.humArcbarData = {
        series: [{ name: "室内湿度", color: "#91CB74", data: humValue / 100 }]
      };

      // TDS值环形图
      const tdsValue = this.realTimeData[3].value;
      const tdsConfig = this.getTdsConfigByValue(tdsValue);

      // 更新TDS环形图配置
      this.tdsArcbarOpts.title.name = `${tdsValue}ppm`;
      this.tdsArcbarOpts.title.color = tdsConfig.mainColor;
      this.tdsArcbarOpts.color = [tdsConfig.mainColor];
      this.tdsArcbarOpts.subtitle.name = `${tdsConfig.desc}`;
      this.tdsArcbarOpts.subtitle.color = tdsConfig.mainColor;
      this.tdsArcbarOpts.extra.arcbar.linearColor = [[0, tdsConfig.mainColor], [1, tdsConfig.gradientColor]];

      // 计算环形图占比（最大值设为300，超过300按100%显示）
      const tdsRatio = Math.min(tdsValue / 300, 1);
      this.tdsArcbarData = {
        series: [{ name: "TDS值", color: tdsConfig.mainColor, data: tdsRatio }]
      };
    },
    /**
     * 根据TDS值获取对应的颜色配置
     */
    getTdsConfigByValue(value) {
      for (const config of this.tdsColorConfig) {
        if (value >= config.min && value <= config.max) {
          return config;
        }
      }
      return this.tdsColorConfig[this.tdsColorConfig.length - 1];
    },
    /**
     * 获取折线图趋势数据（适配后端格式）
     */
    async getLineData() {
      try {
        // 传递时间范围参数给后端
        const response = await getReportLineData({ dateType: this.timeRange });
        if (response.code === 200 && response.data) {
          this.backendLineData = response.data;

          // 格式化X轴分类标签
          const formattedCategories = this.backendLineData.xaxisDataList.map(label => {
            return this.formatXAxisLabel(label, this.timeRange);
          });

          // 第一组：温度数据（室内温度 + 鱼缸水温）
          this.tempLineChartData = {
            categories: formattedCategories,
            series: [
              { name: "室内温度", data: this.backendLineData.indoorTempList },
              { name: "鱼缸水温", data: this.backendLineData.fishTankTempList }
            ]
          };

          // 第二组：湿度&TDS（室内湿度 + 鱼缸TDS）
          this.humTdsLineChartData = {
            categories: formattedCategories,
            series: [
              { name: "室内湿度", data: this.backendLineData.indoorHumidityList },
              { name: "鱼缸TDS", data: this.backendLineData.fishTankTdsList }
            ]
          };

          // ========== 核心修改：动态计算Y轴范围 ==========
          // 计算温度组Y轴范围
          const tempRange = this.calculateYAxisRange([
            this.backendLineData.indoorTempList,
            this.backendLineData.fishTankTempList
          ]);
          // 更新温度图表Y轴配置
          this.tempLineOpts.yAxis.data[0].min = tempRange.min;
          this.tempLineOpts.yAxis.data[0].max = tempRange.max;

          // 计算湿度&TDS组Y轴范围
          const humTdsRange = this.calculateYAxisRange([
            this.backendLineData.indoorHumidityList,
            this.backendLineData.fishTankTdsList
          ]);
          // 更新湿度&TDS图表Y轴配置
          this.humTdsLineOpts.yAxis.data[0].min = humTdsRange.min;
          this.humTdsLineOpts.yAxis.data[0].max = humTdsRange.max;

          // 滚动到最新数据
          this.scrollToLatestData();
        }
      } catch (error) {
        console.error('获取趋势数据失败：', error);
        uni.showToast({
          title: '趋势数据加载失败',
          icon: 'none',
          duration: 2000
        });
      }
    },
    /**
     * 滚动到图表最新数据
     */
    scrollToLatestData() {
      this.$nextTick(() => {
        const categoryLength = this.backendLineData.xaxisDataList.length;
        // 根据当前选中的图表类型滚动对应的图表
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

// 标题+切换按钮容器 - 核心：给容器加align-items: center 强制子元素垂直居中
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10rpx;
  // 外层按钮容器：固定行高，和内部按钮高度一致
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
  line-height: 28px; // 和按钮容器高度一致，整体更协调
}

// 按钮容器 - 统一尺寸，强制子元素垂直居中
.chart-tabs {
  display: flex;
  gap: 4px;
  background: #f5f5f5;
  border-radius: 16px;
  padding: 2px;
  box-sizing: border-box;
  align-items: center; // 强制内部按钮垂直居中
  flex-shrink: 0; // 防止容器被挤压变形

  &.time-tabs {
    margin-left: 8px;
  }
}

// 按钮样式 - 统一高度，文字绝对居中，四个按钮尺寸完全一致
.tab-item {
  padding: 0 8px;
  font-size: 10px;
  color: #606266;
  border-radius: 14px;
  cursor: pointer;
  transition: all 0.2s ease;
  white-space: nowrap;
  width: auto;
  height: 24px; // 固定高度，四个按钮高度完全一致
  line-height: 24px; // 行高匹配高度
  display: flex;
  align-items: center;
  justify-content: center; // 文字水平+垂直绝对居中
  box-sizing: border-box;
  flex-shrink: 0; // 防止按钮被挤压

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
</style>