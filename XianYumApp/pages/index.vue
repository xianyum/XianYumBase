<template>
  <view class="content">
    <view class="text-area">
      <text class="title">Hello {{this.$store.state.user.name}}</text>
    </view>
  </view>
</template>

<style scoped>
  .content {
    display: flex;
    flex-direction: column;
    align-items: center; /* 水平居中 */
    justify-content: center; /* 垂直居中 */
    height: 100vh; /* 关键：设置高度为视口高度，否则垂直居中无效 */
    width: 100%; /* 宽度占满 */
    box-sizing: border-box; /* 防止内边距撑大容器 */
  }

  .logo {
    height: 200rpx;
    width: 200rpx;
    margin-top: 200rpx;
    margin-left: auto;
    margin-right: auto;
    margin-bottom: 50rpx;
  }

  /* 文字容器 - 确保文字本身也居中 */
  .text-area {
    display: flex;
    justify-content: center; /* 水平居中 */
    align-items: center; /* 垂直居中 */
    text-align: center; /* 兜底：文字本身的居中 */
  }
  .title {
    font-size: 36rpx;
    color: #8f8f94;
  }
</style>
