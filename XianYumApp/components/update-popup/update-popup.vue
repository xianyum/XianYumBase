<template>
  <view class="mask flex-center" v-if="showPopup">
    <view class="content botton-radius" @click.stop>
      <!-- 顶部背景图和标题 -->
      <view class="content-top">
        <text class="content-top-text">XianYumApp通知</text>
        <!-- 替换为你提供的顶部背景图路径 -->
        <image class="content-top-bg" width="100%" height="100%" src="/static/images/update/bg_top.png"></image>
      </view>

      <!-- 头部间距 -->
      <view class="content-header"></view>

      <!-- 主体内容 -->
      <view class="content-body">
        <!-- 版本标题 -->
        <view class="title">
          <text>发现新版本</text>
          <text class="content-body-version">v{{ updateInfo.version }}</text>
        </view>

        <!-- 更新日志滚动区域 -->
        <view class="body">
          <scroll-view class="box-des-scroll" scroll-y="true">
            <text class="box-des">
              <text v-for="(item, index) in updateLogList" :key="index" class="log-item">{{ item }}\n</text>
            </text>
          </scroll-view>
        </view>

        <!-- 下载进度 -->
        <view class="progress-box flex-column" v-if="showProgress">
          <progress class="progress" :percent="downloadProgress" activeColor="#3DA7FF" show-info stroke-width="10" />
          <view style="width: 100%; font-size: 28rpx; display: flex; justify-content: space-around">
            <text>安装包下载中，请稍后</text>
            <text>({{ downloadedSize }}/{{ packageFileSize }}M)</text>
          </view>
        </view>

        <!-- 操作按钮 -->
        <view class="footer flex-center" v-if="!showProgress">
          <view class="content-button cancel-btn" v-if="!isForceUpdate" @click="closePopup">
            <text>稍后更新</text>
          </view>
          <view class="content-button confirm-btn" @click="startUpdate">
            <text>{{ isForceUpdate ? '立即更新' : '前往更新' }}</text>
          </view>
        </view>
      </view>

      <!-- 关闭按钮 -->
      <image v-if="!isForceUpdate" class="close-img" src="/static/images/update/app_update_close.png"
      @click.stop="closePopup"></image>
    </view>
  </view>
</template>

<script>
export default {
  name: 'UpdatePopup',
  props: {
    updateInfo: {
      type: Object,
      required: true
    },
    showPopup: {
      type: Boolean,
      default: false
    }
  },
  computed: {
    // 是否强制更新
    isForceUpdate() {
      return this.updateInfo.isForceUpdate === 0;
    },
    // 格式化更新日志为数组
    updateLogList() {
      if (!this.updateInfo.updateLog) return ['优化体验，提升稳定性'];
      // 将换行符分割成数组
      return this.updateInfo.updateLog.split(/[\n\r]/).filter(item => item.trim());
    }
  },
  data() {
    return {
      showProgress: false, // 是否显示下载进度
      downloadProgress: 0, // 下载进度百分比
      packageUrl: '', // 更新包下载地址
      downloadedSize: 0, // 已下载大小
      packageFileSize: 0 // 包总大小
    };
  },
  methods: {
    // 关闭弹窗
    closePopup() {
      this.$emit('close');
    },
    // 点击遮罩层（强制更新时不关闭）
    handleMaskClick() {
      if (!this.isForceUpdate) {
        this.closePopup();
      }
    },
    // 初始化下载地址
    initPackageUrl() {
      if (this.updateInfo.fileInfo && this.updateInfo.fileInfo.fileUrl) {
        this.packageUrl = this.updateInfo.fileInfo.fileUrl;
        return true;
      } else {
        uni.showToast({
          title: '更新包地址为空',
          icon: 'none'
        });
        return false;
      }
    },
    // 开始更新
    startUpdate() {
      // 初始化下载地址
      if (!this.initPackageUrl()) return;

      // 显示进度条
      this.showProgress = true;
      this.downloadProgress = 0;

      // 开始下载更新包
      this.downloadPackage();
    },
    // 下载更新包
    downloadPackage() {
      const downloadTask = uni.downloadFile({
        url: this.packageUrl,
        success: (res) => {
          this.showProgress = false;

          if (res.statusCode === 200) {
            // 下载成功，安装更新包
            this.installPackage(res.tempFilePath);
          } else {
            this.handleUpdateError('更新包下载失败');
          }
        },
        fail: (err) => {
          this.showProgress = false;
          this.handleUpdateError(`下载失败：${err.errMsg}`);
        }
      });

      // 监听下载进度
      downloadTask.onProgressUpdate((progress) => {
        this.downloadProgress = progress.progress;
        // 计算文件大小
        this.downloadedSize = (progress.totalBytesWritten / Math.pow(1024, 2)).toFixed(2);
        this.packageFileSize = (progress.totalBytesExpectedToWrite / Math.pow(1024, 2)).toFixed(2);
      });
    },
    // 安装更新包
    installPackage(tempFilePath) {
      uni.showLoading({
        title: '安装中...',
        mask: true
      });

      const packageType = this.updateInfo.packageType;
      const isForceUpdate = this.isForceUpdate;

      // WGT热更新包安装
      if (packageType === 1) {
        plus.runtime.install(
            tempFilePath,
            { force: false },
            () => {
              uni.hideLoading();
              uni.showToast({
                title: '重启中',
                icon: 'success'
              });
              setTimeout(() => {
                plus.runtime.restart();
              }, 1500);
            },
            (err) => {
              uni.hideLoading();
              this.handleUpdateError(`安装失败：${err.message}`);
            }
        );
      }
      // APK整包安装
      else if (packageType === 2) {
        plus.runtime.install(
            tempFilePath,
            { force: false },
            () => {
              uni.hideLoading();
              uni.showToast({
                title: '安装包已准备好',
                icon: 'success'
              });
              // 非强制更新：关闭弹窗
              if (!isForceUpdate) {
                this.closePopup();
              }
            },
            (err) => {
              uni.hideLoading();
              this.handleUpdateError(`安装失败：${err.message}`);
            }
        );
      }
    },
    // 处理更新错误
    handleUpdateError(msg) {
      uni.showModal({
        title: '更新失败',
        content: msg,
        showCancel: !this.isForceUpdate,
        confirmText: '重新更新',
        success: (res) => {
          if (res.confirm) {
            this.startUpdate();
          } else if (!this.isForceUpdate) {
            this.closePopup();
          }
        }
      });
    }
  }
};
</script>

<style scoped>
page {
  background: transparent;
}

.flex-center {
  display: flex;
  justify-content: center;
  align-items: center;
}

/* 遮罩层 */
.mask {
  position: fixed;
  left: 0;
  top: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.65);
  z-index: 9999;
}

.botton-radius {
  border-bottom-left-radius: 30rpx;
  border-bottom-right-radius: 30rpx;
}

/* 弹窗容器 */
.content {
  position: relative;
  top: 0;
  width: 600rpx;
  background-color: #fff;
  box-sizing: border-box;
  padding: 0 50rpx;
  font-family: Source Han Sans CN;
}

/* 顶部区域 */
.content-top {
  position: absolute;
  top: -195rpx;
  left: 0;
  width: 600rpx;
  height: 270rpx;
}

.content-top-text {
  font-size: 45rpx;
  font-weight: bold;
  color: #f8f8fa;
  position: absolute;
  top: 120rpx;
  left: 50rpx;
  z-index: 1;
}

.content-top-bg {
  top: 0;
  width: 100%;
  height: 100%;
}

.content-header {
  height: 70rpx;
}

/* 主体内容 */
.content-body {
  width: 100%;
}

.title {
  font-size: 33rpx;
  font-weight: bold;
  color: #3da7ff;
  line-height: 38px;
}

.content-body-version {
  padding-left: 20rpx;
  color: #fff;
  font-size: 20rpx;
  margin-left: 10rpx;
  padding: 4rpx 8rpx;
  border-radius: 20rpx;
  background: #50aefd;
}

/* 更新日志区域 */
.body {
  width: 100%;
}

.box-des-scroll {
  box-sizing: border-box;
  padding: 0 40rpx;
  height: 200rpx;
  text-align: left;
}

.box-des {
  font-size: 26rpx;
  color: #000000;
  line-height: 50rpx;
}

.log-item {
  display: block;
}

/* 进度条区域 */
.progress-box {
  width: 100%;
}

.flex-column {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.progress {
  width: 90%;
  height: 40rpx;
}

/* 底部按钮区域 */
.footer {
  height: 150rpx;
  display: flex;
  align-items: center;
  justify-content: space-around;
}

/* 按钮样式 */
.content-button {
  text-align: center;
  flex: 1;
  font-size: 30rpx;
  font-weight: 400;
  border-radius: 40rpx;
  margin: 0 18rpx;
  height: 80rpx;
  line-height: 80rpx;
  display: flex;
  justify-content: center;
  align-items: center;
}

.cancel-btn {
  background: #f5f5f5;
  color: #666666;
}

.confirm-btn {
  background: linear-gradient(to right, #1785ff, #3da7ff);
  color: #ffffff;
}

/* 关闭按钮 */
.close-img {
  width: 70rpx;
  height: 70rpx;
  z-index: 1000;
  position: absolute;
  bottom: -120rpx;
  left: calc(50% - 70rpx / 2);
}
</style>