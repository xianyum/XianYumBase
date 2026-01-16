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

    <!-- 折线图：Y轴数值带℃单位 -->
    <uni-row :gutter="15" class="mt-20">
      <uni-col :span="24">
        <view class="chart-section">
          <text class="section-title">温度趋势对比</text>
          <view class="charts-box line-box">
            <qiun-data-charts
                type="line"
                :opts="lineOpts"
                :chartData="lineChartData"
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
import { queryLatestData } from '@/api/iot/fish'

export default {
  data() {
    return {
      realTimeData: [],
      lineChartData: {},
      humArcbarData: {},
      tdsArcbarData: {},

      // 折线图配置：Y轴数值带℃单位
      lineOpts: {
        color: ["#1890FF","#91CB74","#FAC858","#EE6666","#73C0DE","#3CA272","#FC8452","#9A60B4","#ea7ccc"],
        padding: [15,10,0,15],
        enableScroll: false,
        legend: {},
        xAxis: { disableGrid: true },
        yAxis: {
          gridType: "dash",
          dashLength: 2,
          // 自定义Y轴格式化函数，给数值加℃
          format: function (val) {
            return val + "℃";
          }
        },
        extra: {
          line: { type: "straight", width: 2, activeType: "hollow" }
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
  onReady() {
    // 首次加载数据
    this.fetchEnvData();
    this.getLineData();

    // 设置定时器，每10秒更新一次数据
    this.timer = setInterval(() => {
      this.fetchEnvData();
      this.getLineData();
    }, 10000);
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
     * 从接口获取环境监测数据
     */
    async fetchEnvData() {
      try {
        const response = await queryLatestData();
        // 接口请求成功，更新数据
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
        // 可以添加错误提示逻辑
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
      this.tdsArcbarOpts.title.name = `${tdsValue}ppm`;
      this.tdsArcbarData = {
        series: [{ name: "TDS值", color: "#EE6666", data: tdsValue / 150 }]
      };
    },

    /**
     * 获取折线图数据（可根据实际需求从接口获取）
     */
    getLineData() {
      setTimeout(() => {
        this.lineChartData = {
          categories: ["0:00","6:00","12:00","18:00","21:00"],
          series: [
            { name: "室内温度", data: [19.2,19.5,20.1,19.7,19.4] },
            { name: "鱼缸水温", data: [28.8,29.0,29.4,29.2,29.1] }
          ]
        };
      }, 500);
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

.section-title {
  font-size: 26rpx;
  color: #303133;
  font-weight: 500;
  display: block;
  margin-bottom: 10rpx;
}

.line-box {
  width: 100%;
  height: 300px;
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