<template>
  <view class="ai-container">
    <!-- AI 使用情况卡片 -->
    <view class="usage-card">
      <view class="card-title">AI 令牌使用统计</view>

      <!-- 模型下拉选择 -->
      <view class="model-select-wrap">
        <view class="select-label">当前使用模型</view>
        <picker :range="modelList" @change="changeModel">
          <view class="select-box">
            {{ currentModel }}
            <text class="arrow">▼</text>
          </view>
        </picker>
      </view>

      <view class="charts-box">
        <qiun-data-charts
            type="arcbar"
            :opts="opts"
            :chartData="chartData"
        />
      </view>

      <!-- 用量统计行 -->
      <view class="usage-row">
        <view class="usage-item">
          <text class="label">总额度</text>
          <text class="value">{{ currModelUsage.totalGranted }}</text>
        </view>
        <view class="usage-item">
          <text class="label">已使用</text>
          <text class="value text-used">{{ currModelUsage.totalUsed }}</text>
        </view>
        <view class="usage-item">
          <text class="label">剩余</text>
          <text class="value text-available">{{ currModelUsage.totalAvailable }}</text>
        </view>
      </view>
    </view>

    <!-- AI 使用日志 -->
    <view class="log-section">
      <view class="section-title">AI 使用日志</view>

      <view class="log-item" v-for="item in logList" :key="item.id">
        <!-- 头部：模型 + 流式/非流 + 时间 -->
        <view class="log-header">
          <view class="header-left">
            <text class="model-tag">
              {{ item.modelName }}
            </text>
            <text class="stream-tag" v-if="item.isStream">流式</text>
            <text class="stream-tag non-stream" v-else>非流</text>
          </view>
          <text class="time">{{ formatTime(item.createdAt) }}</text>
        </view>

        <!-- 内容 -->
        <view class="log-content" v-if="item.content">
          <text class="content-text">{{ item.content }}</text>
        </view>

        <!-- 1. 输入 输出 耗时 同一行 -->
        <view class="log-row">
          <text>输入：{{ item.promptTokens }} ｜ 输出：{{ item.completionTokens }} ｜ 耗时：{{ item.useTime }}s</text>
        </view>

        <!-- 2. 实际模型 单独一行 -->
        <view class="log-row real-model" v-if="getUpstreamModel(item.other).upstream_model_name">
          实际模型：{{ getUpstreamModel(item.other).upstream_model_name }}
        </view>

        <!-- 3. 花费 单独一行 -->
        <view class="log-row cost-text">
          花费：${{ (getUpstreamModel(item.other).model_price || 0).toFixed(6) }}
        </view>
      </view>

      <view class="empty" v-if="logList.length === 0">暂无使用日志</view>
    </view>
  </view>
</template>

<script>
import { getAiModels, getAiTokenUsage, getAiTokenLog } from '@/api/tools/ai/aiStatistics.js'
import { formatTime } from '@/utils/dateFormat.js'
export default {
  data() {
    return {
      modelList: [],
      currentModel: 'auto-model',
      currModelUsage: {},
      chartData: {},
      opts: {
        color: ["#18ff52","#91CB74","#FAC858","#EE6666","#73C0DE","#3CA272","#FC8452","#9A60B4","#ea7ccc"],
        padding: undefined,
        title: { name: "0%", fontSize: 35, color: "#1890FF" },
        subtitle: { name: "使用率", fontSize: 25, color: "#666666" },
        extra: {
          arcbar: { type: "default", width: 12, backgroundColor: "#E9E9E9", startAngle: 0.75, endAngle: 0.25, gap: 2 }
        }
      },
      logList: []
    };
  },
  onReady() { this.initModelUsage() },
  onLoad(options) {
    if (options.pageTitle) uni.setNavigationBarTitle({ title: options.pageTitle });
    this.initData();
  },
  onPullDownRefresh() {
    this.initData();
  },
  methods: {
    async initData() {
      const modelRes = await getAiModels();
      if (modelRes.code === 200 && modelRes.data) this.modelList = modelRes.data.map(item => item.id);

      await this.sleep(500);
      await this.loadTokenUsage();

      await this.sleep(500);
      await this.loadTokenLog();
    },
    sleep(ms) { return new Promise(resolve => setTimeout(resolve, ms)) },
    async loadTokenUsage() {
      const res = await getAiTokenUsage();
      if (res.code === 200) { this.currModelUsage = res.data; this.updateArcChart() }
    },
    async loadTokenLog() {
      const res = await getAiTokenLog();
      if (res.code === 200) this.logList = res.data;
    },
    initModelUsage() { this.updateArcChart() },
    changeModel(e) {
      this.currentModel = this.modelList[e.detail.value];
      this.updateArcChart();
    },
    updateArcChart() {
      const used = this.currModelUsage.totalUsed || 0;
      const total = this.currModelUsage.totalGranted || 0;
      const percent = total ? Math.min(used / total, 1) : 0;
      this.opts.title.name = `${Math.round(percent * 100)}%`;
      this.chartData = { series: [{ name: "使用率", color: "#1890FF", data: percent }] };
    },
    formatTime,
    getUpstreamModel(otherStr) {
      try { return JSON.parse(otherStr || '{}') } catch (e) { return {} }
    }
  }
};
</script>

<style scoped>
/* 整体容器 */
.ai-container {
  padding: 20rpx;
  background-color: #f5f7fa;
  min-height: 100vh;
}

/* 统计卡片 */
.usage-card {
  background: #fff;
  border-radius: 20rpx;
  padding: 30rpx;
  margin-bottom: 24rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.05);
}
.card-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 24rpx;
}
.model-select-wrap {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 30rpx;
}
.select-label {
  font-size: 28rpx;
  color: #333;
}
.select-box {
  background: #f5f7fa;
  padding: 12rpx 20rpx;
  border-radius: 30rpx;
  font-size: 26rpx;
  color: #666;
  display: flex;
  align-items: center;
  gap: 8rpx;
}
.arrow {
  font-size: 22rpx;
  color: #999;
}
.charts-box {
  width: 100%;
  height: 200px;
  margin: 0 auto;
}
.usage-row {
  display: flex;
  justify-content: space-around;
  margin: 30rpx 10rpx 10rpx;
  gap: 20rpx;
}
.usage-item {
  flex: 1;
  text-align: center;
  padding: 15rpx 10rpx;
  background: #f8f9fa;
  border-radius: 12rpx;
}
.label {
  display: block;
  font-size: 26rpx;
  color: #666;
  margin-bottom: 12rpx;
}
.value {
  font-size: 38rpx;
  font-weight: bold;
  color: #333;
}
.text-used { color: #fa9f47; }
.text-available { color: #4cd964; }

/* 日志区域 */
.log-section {
  background: #fff;
  border-radius: 20rpx;
  padding: 30rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.05);
}
.section-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 24rpx;
}

/* 日志项 */
.log-item {
  padding: 24rpx;
  border-radius: 16rpx;
  background: #fafbfc;
  margin-bottom: 16rpx;
}
.log-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16rpx;
}
.header-left {
  display: flex;
  align-items: center;
  gap: 12rpx;
}
.model-tag {
  background-color: #f3e8ff; /* 浅紫背景 */
  color: #9333ea; /* 深紫文字 */
  padding: 6rpx 14rpx;
  border-radius: 20rpx;
  font-size: 22rpx;
}
.stream-tag {
  background-color: #f0f9eb;
  color: #67c23a;
  padding: 4rpx 10rpx;
  border-radius: 16rpx;
  font-size: 20rpx;
}
.stream-tag.non-stream {
  background-color: #fdf6ec;
  color: #e6a23c;
}
.time {
  font-size: 24rpx;
  color: #999;
}

/* 内容 */
.log-content {
  margin-bottom: 16rpx;
}
.content-text {
  font-size: 26rpx;
  color: #333;
  line-height: 1.5;
}

/* 日志行 */
.log-row {
  font-size: 24rpx;
  color: #666;
  margin-bottom: 10rpx;
  line-height: 1.5;
}
.real-model {
  color: #999;
  font-size: 23rpx;
}
.cost-text {
  color: #ff4d4f;
  font-weight: bold;
}

.empty {
  text-align: center;
  padding: 60rpx 0;
  color: #999;
  font-size: 26rpx;
}
</style>