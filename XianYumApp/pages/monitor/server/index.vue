<template>
  <view class="container">
    <!-- 页面标题 -->
    <view class="page-header">
      <text class="title">服务器监控</text>
    </view>

    <!-- CPU信息卡片 -->
    <view class="card">
      <view class="card-header">
        <view class="icon-box" :style="{ backgroundColor: '#943ce6' }">
          <uni-icons custom-prefix="iconfont" type="icon-cpu2" size="24" color="#fff"></uni-icons>
        </view>
        <text class="card-title">CPU</text>
      </view>
      <view class="card-content">
        <view class="info-row">
          <text class="label">核心数</text>
          <text class="value">{{ server.cpu && server.cpu.cpuNum ? server.cpu.cpuNum : '-' }}</text>
        </view>
        <view class="info-row">
          <text class="label">用户使用率</text>
          <text class="value">{{ server.cpu && server.cpu.used ? server.cpu.used + '%' : '-' }}</text>
        </view>
        <view class="info-row">
          <text class="label">系统使用率</text>
          <text class="value">{{ server.cpu && server.cpu.sys ? server.cpu.sys + '%' : '-' }}</text>
        </view>
        <view class="info-row">
          <text class="label">当前空闲率</text>
          <text class="value">{{ server.cpu && server.cpu.free ? server.cpu.free + '%' : '-' }}</text>
        </view>
      </view>
    </view>

    <!-- 内存+JVM并排卡片 -->
    <view class="card-row">
      <!-- 物理内存卡片 -->
      <view class="card card-col">
        <view class="card-header">
          <view class="icon-box" :style="{ backgroundColor: '#67c23a' }">
            <uni-icons custom-prefix="iconfont" type="icon-neicun-copy" size="24" color="#fff"></uni-icons>
          </view>
          <text class="card-title">物理内存</text>
        </view>
        <view class="card-content">
          <view class="info-row">
            <text class="label">总内存</text>
            <text class="value">{{ server.mem && server.mem.total ? server.mem.total + ' GB' : '-' }}</text>
          </view>
          <view class="info-row">
            <text class="label">已用内存</text>
            <text class="value">{{ server.mem && server.mem.used ? server.mem.used + ' GB' : '-' }}</text>
          </view>
          <view class="info-row">
            <text class="label">剩余内存</text>
            <text class="value">{{ server.mem && server.mem.free ? server.mem.free + ' GB' : '-' }}</text>
          </view>
          <view class="info-row">
            <text class="label">使用率</text>
            <text :class="['value', (server.mem && server.mem.usage > 80) ? 'text-danger' : '']">
              {{ server.mem && server.mem.usage ? server.mem.usage + '%' : '-' }}
            </text>
          </view>
        </view>
      </view>

      <!-- JVM内存卡片 -->
      <view class="card card-col">
        <view class="card-header">
          <view class="icon-box" :style="{ backgroundColor: '#909399' }">
            <uni-icons custom-prefix="iconfont" type="icon-jvm" size="24" color="#fff"></uni-icons>
          </view>
          <text class="card-title">JVM内存</text>
        </view>
        <view class="card-content">
          <view class="info-row">
            <text class="label">总内存</text>
            <text class="value">{{ server.jvm && server.jvm.total ? server.jvm.total + ' MB' : '-' }}</text>
          </view>
          <view class="info-row">
            <text class="label">已用内存</text>
            <text class="value">{{ server.jvm && server.jvm.used ? server.jvm.used + ' MB' : '-' }}</text>
          </view>
          <view class="info-row">
            <text class="label">剩余内存</text>
            <text class="value">{{ server.jvm && server.jvm.free ? server.jvm.free + ' MB' : '-' }}</text>
          </view>
          <view class="info-row">
            <text class="label">使用率</text>
            <text :class="['value', (server.jvm && server.jvm.usage > 80) ? 'text-danger' : '']">
              {{ server.jvm && server.jvm.usage ? server.jvm.usage + '%' : '-' }}
            </text>
          </view>
        </view>
      </view>
    </view>

    <!-- 服务器信息卡片 -->
    <view class="card">
      <view class="card-header">
        <view class="icon-box" :style="{ backgroundColor: '#e6a23c' }">
          <uni-icons custom-prefix="iconfont" type="icon-fuwuqi" size="24" color="#fff"></uni-icons>
        </view>
        <text class="card-title">服务器信息</text>
      </view>
      <view class="card-content">
        <view class="info-row">
          <text class="label">API版本</text>
          <text class="value">{{ server.sys && server.sys.version ? server.sys.version : '-' }}</text>
        </view>
        <view class="info-row">
          <text class="label">服务器名称</text>
          <text class="value">{{ server.sys && server.sys.computerName ? server.sys.computerName : '-' }}</text>
        </view>
        <view class="info-row">
          <text class="label">操作系统</text>
          <text class="value">{{ server.sys && server.sys.osName ? server.sys.osName : '-' }}</text>
        </view>
        <view class="info-row">
          <text class="label">系统架构</text>
          <text class="value">{{ server.sys && server.sys.osArch ? server.sys.osArch : '-' }}</text>
        </view>
        <view class="info-row">
          <text class="label">服务器IP</text>
          <text class="value">{{ server.sys && server.sys.computerIp ? server.sys.computerIp : '-' }}</text>
        </view>
      </view>
    </view>

    <!-- Java虚拟机信息卡片 -->
    <view class="card">
      <view class="card-header">
        <view class="icon-box" :style="{ backgroundColor: '#ffd240' }">
          <uni-icons custom-prefix="iconfont" type="icon-jvm1" size="24" color="#fff"></uni-icons>
        </view>
        <text class="card-title">Java虚拟机信息</text>
      </view>
      <view class="card-content">
        <view class="info-row">
          <text class="label">Java名称</text>
          <text class="value">{{ server.jvm && server.jvm.name ? server.jvm.name : '-' }}</text>
        </view>
        <view class="info-row">
          <text class="label">Java版本</text>
          <text class="value">{{ server.jvm && server.jvm.version ? server.jvm.version : '-' }}</text>
        </view>
        <view class="info-row">
          <text class="label">启动时间</text>
          <text class="value">{{ server.jvm && server.jvm.startTime ? server.jvm.startTime : '-' }}</text>
        </view>
        <view class="info-row">
          <text class="label">运行时长</text>
          <text class="value">{{ server.jvm && server.jvm.runTime ? server.jvm.runTime : '-' }}</text>
        </view>
        <view class="info-row">
          <text class="label">安装路径</text>
          <text class="value long-text">{{ server.jvm && server.jvm.home ? server.jvm.home : '-' }}</text>
        </view>
        <view class="info-row">
          <text class="label">项目路径</text>
          <text class="value long-text">{{ server.sys && server.sys.userDir ? server.sys.userDir : '-' }}</text>
        </view>
        <view class="info-row">
          <text class="label">运行参数</text>
          <uni-tooltip
              :content="server.jvm && server.jvm.inputArgs ? server.jvm.inputArgs : '-'"
              placement="top"
              :disabled="!(server.jvm && server.jvm.inputArgs)"
          >
            <text class="value long-text">
              {{ server.jvm && server.jvm.inputArgs ? server.jvm.inputArgs : '-' }}
            </text>
          </uni-tooltip>
        </view>
      </view>
    </view>

    <!-- 磁盘状态卡片 -->
    <view class="card">
      <view class="card-header">
        <view class="icon-box" :style="{ backgroundColor: '#f56c6c' }">
          <uni-icons custom-prefix="iconfont" type="icon-cipanzhuangtai" size="24" color="#fff"></uni-icons>
        </view>
        <text class="card-title">磁盘状态</text>
      </view>
      <view class="card-content">
        <view v-if="server.sysFiles && server.sysFiles.length" class="disk-list">
          <view v-for="(sysFile, index) in server.sysFiles" :key="index" class="disk-item">
            <text class="disk-path">{{ sysFile.dirName }}</text>
            <view class="progress-bar">
              <view class="progress-fill" :style="{width: sysFile.usage + '%', backgroundColor: sysFile.usage > 80 ? '#f56c6c' : '#409eff'}"></view>
            </view>
            <text class="disk-info">已用: {{ sysFile.used }} / {{ sysFile.total }} ({{ sysFile.usage }}%)</text>
          </view>
        </view>
        <view v-else class="empty-text">暂无磁盘数据</view>
      </view>
    </view>
  </view>
</template>

<script>
import { getServer } from '@/api/monitor/server.js'
export default {
  name: 'ServerMonitor',
  data() {
    return {
      server: {}
    }
  },
  onPullDownRefresh() {
    this.refreshData();
  },
  onLoad() {
    this.getServerInfo()
  },
  methods: {
    refreshData(){
      try {
        this.getServerInfo();
        uni.stopPullDownRefresh();
        this.$modal.msgSuccess("刷新成功")
      }catch (error) {
        uni.stopPullDownRefresh();
        this.$modal.msgError("刷新失败")
      }
    },
    async getServerInfo() {
      uni.showLoading({ title: '加载中...' })
      try {
        const res = await getServer()
        this.server = res.data || {}
      } catch (e) {
        uni.showToast({ title: '数据加载失败', icon: 'none' })
        console.error('服务器监控数据加载失败：', e)
      } finally {
        uni.hideLoading()
      }
    }
  }
}
</script>

<style lang="scss" scoped>
// 基础变量
$base-padding: 20rpx;
$card-margin: 24rpx 0;
$card-gap: 20rpx;
$radius: 12rpx;
$font-sm: 24rpx;
$font-base: 28rpx;
$font-lg: 32rpx;
$font-xl: 36rpx;
$color-main: #409eff;
$color-success: #67c23a;
$color-warning: #e6a23c;
$color-danger: #f56c6c;
$color-gray: #909399;
$color-text: #303133;
$color-text-light: #606266;
$color-text-placeholder: #909399;
$color-border: #ebeef5;
$color-bg: #f5f7fa;
$color-card: #fff;

.container {
  padding: $base-padding;
  background: $color-bg;
  min-height: 100vh;

  // 页面标题
  .page-header {
    margin-bottom: 30rpx;
    text-align: center;
    .title {
      font-size: $font-xl;
      font-weight: bold;
      color: $color-text;
    }
  }

  // 并排卡片容器
  .card-row {
    display: flex;
    gap: $card-gap;
    margin: $card-margin;
    .card-col {
      flex: 1;
      margin: 0 !important;
    }
  }

  // 图标背景盒（核心样式）
  .icon-box {
    width: 40rpx;
    height: 40rpx;
    border-radius: 8rpx;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  // 通用卡片样式
  .card {
    background: $color-card;
    border-radius: $radius;
    margin: $card-margin;
    box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.08);
    overflow: hidden;

    // 卡片头部
    .card-header {
      display: flex;
      align-items: center;
      padding: 24rpx 30rpx;
      border-bottom: 1rpx solid $color-border;
      background: #fafafa;
      .card-title {
        margin-left: 12rpx;
        font-size: $font-lg;
        font-weight: 500;
        color: $color-text;
      }
    }

    // 卡片内容
    .card-content {
      padding: 30rpx;

      // 信息行
      .info-row {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 12rpx 0;
        border-bottom: 1rpx solid #f0f2f5;
        &:last-child {
          border: none;
        }
        .label {
          min-width: 120rpx;
          font-size: $font-base;
          color: $color-text-light;
        }
        &:last-child {
          border: none;
        }
        .value {
          font-size: $font-base;
          color: $color-text;
          font-weight: 500;
          &.text-danger {
            color: $color-danger;
          }
          &.long-text {
            max-width: 500rpx;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
          }
        }
      }

      // 磁盘列表
      .disk-list {
        margin-top: 10rpx;
        .disk-item {
          margin-bottom: 24rpx;
          .disk-path {
            display: block;
            font-size: $font-base;
            color: $color-text;
            margin-bottom: 12rpx;
            font-weight: 500;
          }
          .progress-bar {
            width: 100%;
            height: 8rpx;
            background: $color-border;
            border-radius: 4rpx;
            overflow: hidden;
            margin-bottom: 8rpx;
            .progress-fill {
              height: 100%;
              border-radius: 4rpx;
              transition: width 0.3s ease;
            }
          }
          .disk-info {
            display: block;
            font-size: $font-sm;
            color: $color-text-placeholder;
            text-align: left;
            line-height: 1.4;
          }
        }
      }

      // 空数据提示
      .empty-text {
        text-align: center;
        padding: 40rpx 0;
        color: #c0c4cc;
        font-size: $font-base;
      }
    }
  }
}
</style>