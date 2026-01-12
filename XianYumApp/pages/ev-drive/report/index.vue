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
            <view class="item-value">{{ stats.mileage.month }}<span class="unit">公里</span></view>
          </view>
          <view class="split-line"></view>
          <view class="data-item">
            <view class="item-label">电耗总量</view>
            <view class="item-value">{{ stats.power.month }}<span class="unit">度</span></view>
          </view>
          <view class="split-line"></view>
          <view class="data-item">
            <view class="item-label">平均电耗</view>
            <view class="item-value">{{ stats.avgPower.month }}<span class="unit">度/公里</span></view>
          </view>
        </view>
      </view>

      <!-- 上月卡片 -->
      <view class="stats-card">
        <view class="card-header">上月数据</view>
        <view class="card-content">
          <view class="data-item">
            <view class="item-label">行驶里程</view>
            <view class="item-value">{{ stats.mileage.lastMonth }}<span class="unit">公里</span></view>
          </view>
          <view class="split-line"></view>
          <view class="data-item">
            <view class="item-label">电耗总量</view>
            <view class="item-value">{{ stats.power.lastMonth }}<span class="unit">度</span></view>
          </view>
          <view class="split-line"></view>
          <view class="data-item">
            <view class="item-label">平均电耗</view>
            <view class="item-value">{{ stats.avgPower.lastMonth }}<span class="unit">度/公里</span></view>
          </view>
        </view>
      </view>

      <!-- 近一年卡片 -->
      <view class="stats-card">
        <view class="card-header">近一年数据</view>
        <view class="card-content">
          <view class="data-item">
            <view class="item-label">行驶里程</view>
            <view class="item-value">{{ stats.mileage.year }}<span class="unit">公里</span></view>
          </view>
          <view class="split-line"></view>
          <view class="data-item">
            <view class="item-label">电耗总量</view>
            <view class="item-value">{{ stats.power.year }}<span class="unit">度</span></view>
          </view>
          <view class="split-line"></view>
          <view class="data-item">
            <view class="item-label">平均电耗</view>
            <view class="item-value">{{ stats.avgPower.year }}<span class="unit">度/公里</span></view>
          </view>
        </view>
      </view>
    </view>

    <!-- 折线图区域：沿用极简版uCharts模板结构 -->
    <view class="chart-container">
      <view class="chart-title">近7天行驶数据趋势</view>
      <view class="charts-box">
        <qiun-data-charts
            type="line"
            :opts="opts"
            :chartData="chartData"
        />
      </view>
    </view>
  </view>
</template>

<script>
export default {
  name: 'DrivingRecord',
  data() {
    return {
      // 统计数据
      stats: {
        mileage: { year: 12500, month: 1200, lastMonth: 1050 },
        power: { year: 1875, month: 180, lastMonth: 157.5 },
        avgPower: { year: 0.15, month: 0.15, lastMonth: 0.15 }
      },
      // 图表数据（极简版结构）
      chartData: {},
      // 沿用极简版opts配置，仅调整少量业务相关样式
      opts: {
        color: ["#1890FF","#EE6666","#52c41a"], // 对应：行驶公里数、消耗电量、平均电耗
        padding: [15,10,0,15],
        enableScroll: false,
        legend: {},
        xAxis: {
          disableGrid: true
        },
        yAxis: {
          gridType: "dash",
          dashLength: 2,
          min: 0 // 新增：y轴从0开始，符合业务数据逻辑
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
    this.getServerData();
  },
  methods: {
    // 沿用极简版数据获取结构，替换为近7天业务数据
    getServerData() {
      //模拟从服务器获取数据时的延时
      setTimeout(() => {
        const days = 7;
        const categories = []; // x轴：近7天日期
        const mileageData = []; // 行驶公里数
        const powerData = []; // 消耗电量
        const avgPowerData = []; // 平均电耗

        // 生成近7天日期和mock数据
        for (let i = days - 1; i >= 0; i--) {
          const date = new Date();
          date.setDate(date.getDate() - i);
          const dayStr = `${date.getMonth() + 1}/${date.getDate()}`;
          categories.push(dayStr);

          // 随机生成行驶公里数（30-50公里）
          const mileage = Math.floor(Math.random() * 20) + 30;
          mileageData.push(mileage);

          // 消耗电量 = 行驶公里数 * 平均电耗（0.14-0.16）
          const avgPower = (Math.random() * 0.02 + 0.14).toFixed(2) * 1;
          const power = (mileage * avgPower).toFixed(1) * 1;
          powerData.push(power);
          avgPowerData.push(avgPower);
        }

        // 拼接极简版要求的data格式
        let res = {
          categories: categories,
          series: [
            {
              name: "行驶公里数",
              data: mileageData
            },
            {
              name: "消耗电量",
              data: powerData
            },
            {
              name: "平均电耗",
              data: avgPowerData
            }
          ]
        };
        this.chartData = JSON.parse(JSON.stringify(res));
      }, 500);
    }
  }
};
</script>

<style scoped>
/* 页面基础样式：紧凑版 */
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
.chart-title {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
  padding-left: 5px;
  line-height: 1.2;
}

/* 极简版uCharts容器样式（沿用） */
.charts-box {
  width: 100%;
  height: 260px; /* 紧凑版高度 */
}
</style>