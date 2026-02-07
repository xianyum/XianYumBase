<template>
  <view class="container">
    <!-- 头部标题 -->
    <view class="header">
      <image class="logo" src="/static/logo.png" mode="widthFix"></image>
      <view class="title">XianYumApp</view>
      <view class="subtitle">XianYum - 一站式服务平台</view>
      <view class="version">版本号：{{ appVersion }}</view>
    </view>

    <view class="qrcode-wrap">
      <uv-qrcode
          ref="qrcode"
          :value="downLoadUrl"
          :options="qrcodeOptions"
          class="qrcode-component"
      ></uv-qrcode>
      <view class="tip">
        扫码下载/体验应用
        <!-- 新增二维码过期时间 -->
        <view class="expire-tip">（二维码有效期：{{ expireTime }}）</view>
      </view>
    </view>

    <!-- 应用描述区域 -->
    <view class="app-desc">
      <view class="desc-title">应用介绍</view>
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
export default {
  name: 'QrcodePage',
  data() {
    return {
      downLoadUrl: 'zhangwei',
      appVersion: 'v1.0.0',
      expireTime: '2026-03-07 23:59:59',
      qrcodeOptions: {
        size: 200,
        useDynamicSize: false,
        errorCorrectLevel: 'H',
        margin: 10,
        // areaColor: "#F95F6B",
        backgroundColor: "#ffffff",
        foregroundImageSrc: '/static/logo.png',
        // 确保logo居中（避免logo偏移）
        foregroundImageSize: 0.2
      }
    }
  },
  // 可选：如果需要实时倒计时显示剩余时间，可添加以下生命周期
  onLoad() {
    // 示例：如需动态计算过期时间（比如生成后24小时过期）
    // const now = new Date();
    // const expireDate = new Date(now.getTime() + 24 * 60 * 60 * 1000);
    // this.expireTime = expireDate.toLocaleString('zh-CN', {
    //   year: 'numeric',
    //   month: '2-digit',
    //   day: '2-digit',
    //   hour: '2-digit',
    //   minute: '2-digit',
    //   second: '2-digit'
    // });
  }
}
</script>

<style scoped>
/* 整体容器 */
.container {
  padding: 40rpx 30rpx;
  text-align: center;
  background-color: #ffffff;
  min-height: 100vh;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  align-items: center;
}

/* 头部区域 */
.header {
  margin-bottom: 80rpx;
}

.logo {
  width: 80rpx;
  height: 80rpx;
  margin-bottom: 20rpx;
}

.title {
  font-size: 36rpx;
  font-weight: bold;
  color: #333333;
  margin-bottom: 10rpx;
}

.subtitle {
  font-size: 24rpx;
  color: #999999;
  margin-bottom: 8rpx;
}

.version {
  font-size: 22rpx;
  color: #666666;
  font-family: monospace;
}

/* 二维码区域（修复偏移核心样式） */
.qrcode-wrap {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 60rpx;
}

/* 调整组件容器：确保内部二维码居中，去掉多余内边距干扰 */
.qrcode-component {
  width: 200rpx; /* 与options中size一致 */
  height: 200rpx; /* 与options中size一致 */
  padding: 20rpx; /* 外层容器的内边距（对应截图的红色边框） */
  background: #F95F6B; /* 与areaColor一致，实现截图的红色底色 */
  border-radius: 16rpx;
  margin-bottom: 20rpx;
  /* 强制内部元素居中 */
  display: flex;
  justify-content: center;
  align-items: center;
}

.tip {
  margin-top: 18rpx;
  font-size: 24rpx;
  color: #666666;
  /* 调整为flex布局，让提示文字和过期时间换行对齐 */
  display: flex;
  flex-direction: column;
  align-items: center;
}

/* 新增过期时间样式 */
.expire-tip {
  font-size: 20rpx;
  color: #ff6666; /* 红色突出过期提示 */
  margin-top: 8rpx;
  font-weight: 500;
}

/* 应用描述区域 */
.app-desc {
  width: 100%;
  max-width: 600rpx;
  padding: 30rpx;
  background-color: #f8f8f8;
  border-radius: 12rpx;
  text-align: left;
}

.desc-title {
  font-size: 28rpx;
  font-weight: 600;
  color: #333;
  margin-bottom: 15rpx;
}

.desc-content {
  font-size: 24rpx;
  color: #666;
  line-height: 1.6;
}
</style>