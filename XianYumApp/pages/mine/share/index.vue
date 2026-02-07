<template>
  <view class="container">

    <view class="header">
      <image class="logo" src="/static/logo.png" mode="widthFix"></image>
      <view class="title">XianYumApp</view>
      <view class="subtitle">XianYum - 一站式服务平台</view>
      <view class="version">版本号：v{{ appVersion }}</view>
    </view>

    <view class="qrcode-wrap">
      <view class="qrcode-card">
        <uv-qrcode
            ref="qrcode"
            :value="downLoadUrl"
            :options="qrcodeOptions"
            class="qrcode-component"
        ></uv-qrcode>
      </view>
      <view class="tip">
        扫码下载/体验应用
        <view class="expire-tip" v-if="expireTime">（二维码有效期：{{ formatTime(expireTime) }}）</view>
      </view>
    </view>

    <view class="app-desc-card">
      <view class="desc-title">
        <text class="desc-icon">📌</text>
        应用介绍
      </view>
      <view class="desc-content">
        这是一款基于uni-app开发的跨端管理应用，支持iOS、Android、H5、小程序等多端部署，
        具备轻量、高效、易扩展的特点，聚焦新能源车辆管理、物联网设备控制、系统状态监控等场景，
        提供行驶记录查询、报表统计、智能设备管理、服务监控等实用功能，
        助力用户便捷高效地完成各类业务管理与系统运维工作。
      </view>
    </view>
  </view>
</template>

<script>
import {getLastApkApp} from "@/api/app/appVersionControl";
import { formatTime } from '@/utils/dateFormat.js'

export default {
  name: 'QrcodePage',
  data() {
    return {
      downLoadUrl: '',
      appVersion: 'v1.0.0',
      expireTime: undefined,
      formatTime,
      qrcodeOptions: {
        size: 200,
        useDynamicSize: false,
        errorCorrectLevel: 'H',
        margin: 10,
        backgroundColor: "#ffffff",
        foregroundImageSrc: '/static/logo.png',
        foregroundImageSize: 0.2
      }
    }
  },
  onLoad() {
    this.getLastApkApp()
  },
  methods: {
    getLastApkApp(){
      const systemInfo = uni.getSystemInfoSync();
      // #ifdef APP-PLUS
      this.appVersion = systemInfo.appWgtVersion;
      // #endif

      // #ifdef H5
      this.appVersion = systemInfo.appVersion
      // #endif

      getLastApkApp().then(res => {
        this.downLoadUrl = res.data.fileInfo.fileUrl
        this.expireTime = res.data.fileInfo.expireTime
      });
    }

  }
}
</script>

<style scoped>

page {
  background-color: #f5f7fa;
}

/* 整体容器 */
.container {
  padding: 60rpx 30rpx;
  text-align: center;
  min-height: 100vh;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 40rpx; /* 统一模块间距 */
}

.header {
  margin-bottom: 20rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.logo {
  width: 100rpx;
  height: 100rpx;
  margin-bottom: 24rpx;
  transition: transform 0.3s ease;
}

.logo:hover {
  transform: rotate(5deg);
}

.title {
  font-size: 42rpx;
  font-weight: 700;
  color: #2d3748;
  margin-bottom: 12rpx;
  letter-spacing: 3rpx;
}

.subtitle {
  font-size: 26rpx;
  color: #718096;
  margin-bottom: 12rpx;
}

.version {
  font-size: 22rpx;
  color: #a0aec0;
  font-family: monospace;
  background: #f7fafc;
  padding: 6rpx 16rpx;
  border-radius: 12rpx;
}

/* 二维码区域（保留优化） */
.qrcode-wrap {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.qrcode-card {
  padding: 24rpx;
  background: linear-gradient(135deg, #f95f6b 0%, #e84393 100%);
  border-radius: 24rpx;
  box-shadow: 0 8rpx 30rpx rgba(249, 95, 107, 0.2);
  transition: transform 0.2s ease;
}

.qrcode-card:hover {
  transform: translateY(-4rpx);
}

.qrcode-component {
  width: 200rpx;
  height: 200rpx;
  background: #ffffff;
  border-radius: 16rpx;
  display: flex;
  justify-content: center;
  align-items: center;
}

.tip {
  margin-top: 24rpx;
  font-size: 26rpx;
  color: #4a5568;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.expire-tip {
  font-size: 22rpx;
  color: #e53e3e;
  margin-top: 10rpx;
  font-weight: 500;
  background: #fef7fb;
  padding: 4rpx 12rpx;
  border-radius: 8rpx;
}

/* 应用描述区域（保留优化） */
.app-desc-card {
  width: 100%;
  max-width: 650rpx;
  padding: 40rpx;
  background: #ffffff;
  border-radius: 20rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);
  text-align: left;
}

.desc-title {
  font-size: 30rpx;
  font-weight: 600;
  color: #2d3748;
  margin-bottom: 20rpx;
  display: flex;
  align-items: center;
  gap: 10rpx;
}

.desc-icon {
  font-size: 28rpx;
}

.desc-content {
  font-size: 26rpx;
  color: #4a5568;
  line-height: 1.8;
  text-indent: 40rpx;
}
</style>