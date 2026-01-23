<template>
  <view class="update-popup-mask" v-if="showPopup" @click="handleMaskClick">
    <view class="update-popup-container" @click.stop>
      <!-- 弹窗头部 -->
      <view class="popup-header">
        <text class="popup-title">{{ updateInfo.updateTitle || '发现新版本' }}</text>
        <text class="popup-version">v{{ updateInfo.version }}</text>
        <view class="close-btn" v-if="!isForceUpdate" @click="closePopup">
          <text class="iconfont">✕</text>
        </view>
      </view>

      <!-- 更新日志 -->
      <view class="update-log-container">
        <text class="log-title">更新日志</text>
        <view class="log-content">
          <text v-for="(item, index) in updateLogList" :key="index" class="log-item">• {{ item }}</text>
        </view>
      </view>

      <!-- 下载进度（下载中显示） -->
      <view class="progress-container" v-if="showProgress">
        <text class="progress-text">{{ downloadProgress }}%</text>
        <view class="progress-bar">
          <view class="progress-fill" :style="{ width: downloadProgress + '%' }"></view>
        </view>
      </view>

      <!-- 操作按钮 -->
      <view class="popup-footer" v-if="!showProgress">
        <view class="cancel-btn" v-if="!isForceUpdate" @click="closePopup">
          <text>稍后更新</text>
        </view>
        <view class="confirm-btn" @click="startUpdate">
          <text>{{ isForceUpdate ? '立即更新' : '前往更新' }}</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  name: 'UpdatePopup',
  props: {
    // 更新信息，使用你提供的数据结构
    updateInfo: {
      type: Object,
      required: true
    },
    // 是否显示弹窗
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
      packageUrl: '' // 更新包下载地址
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
      });
    },
    // 安装更新包
    installPackage(tempFilePath) {
      uni.showLoading({
        title: '安装中...',
        mask: true
      });

      const packageType = this.updateInfo.packageType;
      const isForceUpdate = this.isForceUpdate; // 复用已有的强制更新判断

      // WGT热更新包安装（替换为官方统一的plus.runtime.install）
      if (packageType === 1) {
        plus.runtime.install(
            tempFilePath,
            { force: false }, // 官方默认：不强制安装
            () => {
              uni.hideLoading();
              uni.showToast({
                title: '更新成功，即将重启',
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
      // APK整包安装（保留你的原有逻辑，仅新增非强制更新关闭弹窗）
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
              // 非强制更新：关闭弹窗（系统接管APK安装）
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
/* 遮罩层 */
.update-popup-mask {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
}

/* 弹窗容器 */
.update-popup-container {
  width: 85%;
  max-width: 400px;
  background-color: #ffffff;
  border-radius: 16rpx;
  box-shadow: 0 8rpx 30rpx rgba(0, 0, 0, 0.15);
  overflow: hidden;
}

/* 弹窗头部 */
.popup-header {
  position: relative;
  padding: 30rpx;
  background: linear-gradient(135deg, #409eff, #66b1ff);
  color: #ffffff;
}

.popup-title {
  font-size: 36rpx;
  font-weight: 600;
}

.popup-version {
  font-size: 24rpx;
  opacity: 0.8;
  margin-left: 20rpx;
}

.close-btn {
  position: absolute;
  top: 20rpx;
  right: 20rpx;
  width: 48rpx;
  height: 48rpx;
  display: flex;
  justify-content: center;
  align-items: center;
  border-radius: 50%;
  background-color: rgba(255, 255, 255, 0.2);
  font-size: 24rpx;
}

/* 更新日志 */
.update-log-container {
  padding: 30rpx;
}

.log-title {
  font-size: 30rpx;
  font-weight: 600;
  color: #333333;
  margin-bottom: 20rpx;
  display: block;
}

.log-content {
  font-size: 28rpx;
  color: #666666;
  line-height: 1.6;
}

.log-item {
  display: block;
  margin-bottom: 10rpx;
}

/* 进度条 */
.progress-container {
  padding: 0 30rpx 30rpx;
}

.progress-text {
  font-size: 26rpx;
  color: #666666;
  margin-bottom: 10rpx;
  display: block;
  text-align: center;
}

.progress-bar {
  height: 12rpx;
  background-color: #f0f0f0;
  border-radius: 6rpx;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #409eff, #66b1ff);
  border-radius: 6rpx;
  transition: width 0.3s ease;
}

/* 底部按钮 */
.popup-footer {
  display: flex;
  padding: 20rpx 30rpx 30rpx;
  gap: 20rpx;
}

.cancel-btn, .confirm-btn {
  flex: 1;
  height: 88rpx;
  border-radius: 44rpx;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 32rpx;
}

.cancel-btn {
  background-color: #f5f5f5;
  color: #666666;
}

.confirm-btn {
  background: linear-gradient(90deg, #409eff, #66b1ff);
  color: #ffffff;
}
</style>