<template>
  <view class="about-container">
    <view class="header-section text-center">
      <image style="width: 150rpx;height: 150rpx;" src="/static/logo.png" mode="widthFix">
      </image>
      <uni-title type="h2" title="XianYum移动端"></uni-title>
    </view>

    <view class="content-section">
      <view class="menu-list">
        <view class="list-cell list-cell-arrow">
          <view class="menu-item-box">
            <view>版本信息</view>
            <view class="text-right">v{{version}}</view>
          </view>
        </view>
        <view class="list-cell list-cell-arrow">
          <view class="menu-item-box">
            <view>官方邮箱</view>
            <view class="text-right">80616059@qq.com</view>
          </view>
        </view>
        <view class="list-cell list-cell-arrow">
          <view class="menu-item-box">
            <view>联系我们</view>
            <view class="text-right">Wchat:80616059</view>
          </view>
        </view>
        <view class="list-cell list-cell-arrow">
          <view class="menu-item-box">
            <view>公司网站</view>
            <view class="text-right">
              <uni-link :href="url" :text="url" showUnderLine="false"></uni-link>
            </view>
          </view>
        </view>
      </view>
    </view>

    <view class="copyright">
      <view>Copyright &copy; 2026 xianyum.cn All Rights Reserved.</view>
    </view>
  </view>
</template>

<script>
  export default {
    data() {
      return {
        url: getApp().globalData.config.appInfo.site_url,
        version: ''
      }
    },
    onShow() {
      this.getVersion();
    },
    methods: {
      /**
       * 根据运行环境获取对应版本号
       */
      getVersion() {
          const systemInfo = uni.getSystemInfoSync();
          const me = this;

          // App端（注意条件编译指令是APP-PLUS，不是APP）
          // #ifdef APP-PLUS
          // 优先使用系统信息中的应用版本号覆盖初始值
          if (systemInfo.appWgtVersion) {
            me.version = systemInfo.appWgtVersion;
          }
          // #endif

          // H5端
          // #ifdef H5
          // H5端用systemInfo.appVersion（浏览器版本）或全局变量兜底
          me.version = systemInfo.appVersion || me.version;
          // #endif
      }
    }
  }
</script>

<style lang="scss" scoped>
  page {
    background-color: #f8f8f8;
  }

  .copyright {
    margin-top: 50rpx;
    text-align: center;
    line-height: 60rpx;
    color: #999;
  }

  .header-section {
    display: flex;
    padding: 30rpx 0 0;
    flex-direction: column;
    align-items: center;
  }
</style>
