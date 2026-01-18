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

    <!-- 折线图：4条折线 + 时间范围切换 -->
    <uni-row :gutter="15" class="mt-20">
      <uni-col :span="24">
        <view class="chart-section">
          <!-- 标题 + 切换按钮 -->
          <view class="section-header">
            <text class="section-title">环境趋势对比</text>
            <view class="chart-tabs">
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
                近7天
              </text>
            </view>
          </view>

          <view class="charts-box line-box">
            <qiun-data-charts
                type="line"
                :opts="lineOpts"
                :chartData="lineChartData"
                :ontouch="true"
                @touchstart.stop="handleChartTouch"
                ref="lineChartRef"
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
import { queryLatestData,getReportLineData } from '@/api/iot/fish'

export default {
  data() {
    return {
      realTimeData: [],
      lineChartData: {},
      humArcbarData: {},
      tdsArcbarData: {},
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
      // TDS值颜色配置规则
      tdsColorConfig: [
        { min: 0, max: 50, mainColor: "#1890FF", gradientColor: "#40a9ff", desc: "优质" },
        { min: 51, max: 100, mainColor: "#91CB74", gradientColor: "#2fc25b", desc: "良好" },
        { min: 101, max: 200, mainColor: "#FAC858", gradientColor: "#ffab91", desc: "一般" },
        { min: 201, max: 300, mainColor: "#EE6666", gradientColor: "#4d6bff", desc: "较差" },
        { min: 301, max: Infinity, mainColor: "#9A60B4", gradientColor: "#d12e2e", desc: "极差" }
      ],
      // 折线图配置（移除统一Y轴单位，适配多维度数据）
      lineOpts: {
        color: ["#1890FF","#91CB74","#FAC858","#EE6666"],
        padding: [15, 10, 0, 15],
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
          dashLength: 2
        },
        extra: {
          line: { type: "straight", width: 2, activeType: "hollow" },
          scroll: {
            type: 'horizontal',
            threshold: 10
          }
        }
      },
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
    lineChartRef: null
  },
  onReady() {
    // 首次加载数据
    this.fetchEnvData();
    this.getLineData();

    // 设置定时器，每10秒更新一次数据
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

          // 转换为图表所需格式
          this.lineChartData = {
            categories: formattedCategories,
            series: [
              { name: "室内温度", data: this.backendLineData.indoorTempList },
              { name: "鱼缸水温", data: this.backendLineData.fishTankTempList },
              { name: "室内湿度", data: this.backendLineData.indoorHumidityList },
              { name: "鱼缸TDS", data: this.backendLineData.fishTankTdsList }
            ]
          };

          // 滚动到最新数据
          this.$nextTick(() => {
            if (this.$refs.lineChartRef && this.$refs.lineChartRef.scrollTo) {
              this.$refs.lineChartRef.scrollTo({
                x: this.lineChartData.categories.length - 8,
                y: 0,
                animate: false
              });
            }
          });
        }
      } catch (error) {
        console.error('获取趋势数据失败：', error);
        uni.showToast({
          title: '趋势数据加载失败',
          icon: 'none',
          duration: 2000
        });
      }
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

// 标题+切换按钮容器
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10rpx;
}

.section-title {
  font-size: 26rpx;
  color: #303133;
  font-weight: 500;
  display: block;
}

// 时间范围切换按钮
.chart-tabs {
  display: flex;
  background: #f5f7fa;
  border-radius: 6rpx;
  padding: 2rpx;
}

.tab-item {
  font-size: 22rpx;
  padding: 6rpx 16rpx;
  border-radius: 4rpx;
  color: #606266;
  transition: all 0.3s;
  &.active {
    background: #1890FF;
    color: #fff;
  }
}

.line-box {
  width: 100%;
  height: 300px;
  user-select: none;
  -webkit-user-select: none;
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
}
</style>