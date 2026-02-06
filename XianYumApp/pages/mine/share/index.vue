<template>
  <view class="share-page">
    <!-- 分享页主容器 -->
    <view class="share-container">
      <!-- 顶部Logo展示区 -->
      <view class="logo-container">
        <image class="page-logo" src="/static/logo.png" mode="aspectFit"></image>
      </view>

      <!-- 二维码核心区域 -->
      <view class="qrcode-container">
        <uv-qrcode ref="qrcode" value="https://h5.uvui.cn" :options="options2"></uv-qrcode>
      </view>

      <!-- 二维码过期时间 -->
      <view class="expire-container">
        <text class="expire-text">二维码过期时间：{{ expireTime }}</text>
      </view>

      <!-- 版本号展示区 -->
      <view class="version-container">
        <text class="version-text">版本号：{{ versionNum }}</text>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      // 完全沿用你提供的二维码配置
      options2: {
        data: 'https://www.uvui.cn',
        size: 300,
        useDynamicSize: false,
        errorCorrectLevel: 'Q',
        margin: 10,
        areaColor: "#F95F6B",
        backgroundColor: "#3c9cff",
        foregroundImageSrc: '/static/logo.png',
      },
      // 版本号
      versionNum: 'v2.1.8',
      // 二维码过期时间
      expireTime: '2026-08-01 00:00:00'
    }
  },
  onReady() {
    // 确保页面渲染完成后二维码正常生成
    this.$nextTick(() => {
      if (this.$refs.qrcode) {
        this.$refs.qrcode.makeCode()
      }
    })
  }
}
</script>

<style scoped lang="scss">
// 页面整体样式
.share-page {
  width: 100%;
  min-height: 100vh;
  background-color: #f8f8f8;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 30rpx 0;
  box-sizing: border-box;
}

// 分享页容器
.share-container {
  width: 700rpx;
  background: #ffffff;
  border-radius: 16rpx;
  padding: 60rpx 40rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  box-shadow: 0 6rpx 20rpx rgba(0, 0, 0, 0.05);
}

// Logo样式
.logo-container {
  margin-bottom: 50rpx;
}

.page-logo {
  width: 140rpx;
  height: 140rpx;
}

// 二维码容器
.qrcode-container {
  margin-bottom: 40rpx;
  // 适配二维码尺寸，保证显示完整
  width: 300px;
  height: 300px;
  display: flex;
  justify-content: center;
  align-items: center;
}

// 过期时间样式
.expire-container {
  margin-bottom: 30rpx;
}

.expire-text {
  font-size: 28rpx;
  color: #ff6b6b;
  font-weight: 500;
}

// 版本号样式
.version-container {
  margin-top: 10rpx;
}

.version-text {
  font-size: 26rpx;
  color: #666666;
}
</style>