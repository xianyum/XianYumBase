<template>
  <view class="share-page">
    <!-- 应用信息 -->
    <view class="app-header">
      <image class="logo" src="/static/logo.png" mode="aspectFit"></image>
      <text class="app-name">XianYumApp</text>
      <text class="app-slogan">XianYum · 一站式服务平台</text>
      <view class="version-pill">v{{ appVersion }}</view>
    </view>

    <!-- 下载二维码 -->
    <view class="qr-section">
      <text class="qr-title">扫码下载体验</text>
      <text class="qr-subtitle">使用手机相机或扫码工具扫描</text>
      <view class="qr-tile">
        <uv-qrcode
            ref="qrcode"
            :value="downLoadUrl"
            :options="qrcodeOptions"
            class="qrcode-component"
        ></uv-qrcode>
      </view>
      <view class="qr-meta" v-if="expireTime">
        <uni-icons type="info" size="13" color="#9aa4b8"></uni-icons>
        <text>有效期至 {{ formatTime(expireTime) }}</text>
      </view>
    </view>

    <!-- 应用功能 -->
    <view class="feature-card">
      <view class="panel-head">
        <view class="panel-bar"></view>
        <text class="panel-title">应用功能</text>
      </view>
      <view class="feature-row">
        <view class="feature-item" v-for="item in features" :key="item.name">
          <view class="feature-icon" :style="{ backgroundColor: item.bgColor }">
            <uni-icons :type="item.icon" size="20" :color="item.iconColor"></uni-icons>
          </view>
          <text class="feature-name">{{ item.name }}</text>
        </view>
      </view>
    </view>

    <view class="platform-note">
      <view class="note-line"></view>
      <text>支持 iOS / Android / H5 / 小程序</text>
      <view class="note-line"></view>
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
      features: [
        { name: '行驶记录', icon: 'map-filled', bgColor: '#eaf1ff', iconColor: '#4f7cff' },
        { name: '报表统计', icon: 'tune-filled', bgColor: '#f1ecff', iconColor: '#7c5cff' },
        { name: '设备管理', icon: 'gear-filled', bgColor: '#e8f9f1', iconColor: '#23b26d' },
        { name: '服务监控', icon: 'notification-filled', bgColor: '#fff4e5', iconColor: '#f59f0a' }
      ],
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

<style lang="scss">
page {
  background-color: #f4f6fb;
}
</style>

<style lang="scss" scoped>
.share-page {
  min-height: 100vh;
  padding: 56rpx 30rpx 60rpx;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  align-items: center;
  background: linear-gradient(180deg, #e9effc 0%, #f4f6fb 42%, #f4f6fb 100%);
}

// 应用信息
.app-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 44rpx;

  .logo {
    width: 120rpx;
    height: 120rpx;
    border-radius: 28rpx;
    border: 3rpx solid rgba(255, 255, 255, 0.9);
    box-shadow: 0 10rpx 24rpx rgba(23, 43, 99, 0.12);
  }

  .app-name {
    margin-top: 24rpx;
    font-size: 42rpx;
    font-weight: 700;
    color: #1f2430;
    letter-spacing: 1rpx;
  }

  .app-slogan {
    margin-top: 8rpx;
    font-size: 24rpx;
    color: #8a93a6;
  }

  .version-pill {
    margin-top: 18rpx;
    padding: 4rpx 18rpx;
    border-radius: 20rpx;
    background: rgba(79, 124, 255, 0.08);
    color: #4f7cff;
    font-size: 22rpx;
  }
}

// 二维码
.qr-section {
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;

  .qr-title {
    font-size: 30rpx;
    font-weight: 600;
    color: #1f2430;
  }

  .qr-subtitle {
    margin-top: 8rpx;
    font-size: 23rpx;
    color: #9aa4b8;
  }

  .qr-tile {
    margin-top: 28rpx;
    padding: 16rpx;
    background: #ffffff;
    border-radius: 24rpx;
    box-shadow: 0 8rpx 24rpx rgba(23, 43, 99, 0.07);
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .qrcode-component {
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .qr-meta {
    margin-top: 24rpx;
    display: flex;
    align-items: center;

    text {
      margin-left: 8rpx;
      font-size: 22rpx;
      color: #9aa4b8;
    }
  }
}

// 应用功能
.feature-card {
  width: 100%;
  margin-top: 28rpx;
  padding: 32rpx;
  background: #ffffff;
  border-radius: 32rpx;
  border: 1rpx solid #eef1f7;
  box-shadow: 0 6rpx 24rpx rgba(23, 43, 99, 0.04);

  .panel-head {
    display: flex;
    align-items: center;
  }

  .panel-bar {
    width: 6rpx;
    height: 26rpx;
    border-radius: 3rpx;
    background: linear-gradient(180deg, #4f7cff, #7c5cff);
    margin-right: 12rpx;
  }

  .panel-title {
    font-size: 28rpx;
    font-weight: 600;
    color: #1f2430;
  }

  .feature-row {
    display: flex;
    margin-top: 28rpx;
  }

  .feature-item {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
  }

  .feature-icon {
    width: 72rpx;
    height: 72rpx;
    border-radius: 22rpx;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .feature-name {
    margin-top: 14rpx;
    font-size: 24rpx;
    color: #5b6478;
  }
}

.platform-note {
  display: flex;
  align-items: center;
  margin-top: 32rpx;

  text {
    font-size: 22rpx;
    color: #a3abbb;
  }

  .note-line {
    width: 40rpx;
    height: 1rpx;
    background: #dfe4ee;
    margin: 0 16rpx;
  }
}
</style>
