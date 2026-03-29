<template>
  <view class="container">
    <!-- OTP列表 -->
    <view class="otp-list">
      <view v-if="otpList.length === 0" class="empty-state">
        <view class="empty-icon">🔐</view>
        <text class="empty-title">暂无记录</text>
        <text class="empty-hint">点击右下角按钮扫描二维码添加</text>
      </view>
      <view v-else class="otp-item" v-for="(item, index) in otpList" :key="index" @click="editOTP(item)">
        <view class="otp-item-header">
          <view class="otp-issuer-info">
            <text class="otp-issuer">{{ item.systemName }}</text>
            <text v-if="item.account" class="otp-account">{{ item.account }}</text>
          </view>
          <button type="default" @click.stop="deleteOTP(item)" class="delete-btn">
            <text class="delete-icon">×</text>
          </button>
        </view>
        <view class="otp-item-content">
          <text class="otp-code">{{ item.code }}</text>
          <view class="expiry-bar">
            <view class="expiry-progress" :style="{ width: item.progress + '%' }"></view>
          </view>
        </view>
      </view>
    </view>

    <!-- 扫描二维码按钮 -->
    <button type="primary" @click="scanQRCode" class="scan-btn">
      <text class="scan-icon">+</text>
      <text class="scan-text">添加</text>
    </button>

    <!-- 编辑弹框 -->
    <uni-popup ref="editPopup" type="center" :mask-click="false">
      <view class="popup-content">
        <view class="popup-title">修改系统备注</view>
        <uni-easyinput v-model="editForm.systemName" placeholder="请输入系统备注" @input="handleInput" />
        <view class="popup-buttons">
          <button type="default" @click="closePopup" class="cancel-btn">取消</button>
          <button type="primary" @click="saveEdit" class="save-btn">保存</button>
        </view>
      </view>
    </uni-popup>

  </view>
</template>

<script>
import * as OTPAuth from "@/uni_modules/otpauth/index.js";
import { saveOtpNetworkAuth, getOtpNetworkAuthList, deleteOtpNetworkAuthList, updateOtpNetworkAuth } from '@/api/tools/otp/otpApi.js';

export default {
  data() {
    return {
      otpList: [],
      otpTimer: null, // 保存定时器 ID
      editForm: {
        systemName: '',
        id: ''
      }
    };
  },
  beforeUnmount() {
    // 清除定时器，避免内存泄漏
    if (this.otpTimer) {
      clearInterval(this.otpTimer);
      this.otpTimer = null;
    }
  },
  mounted() {
    // 加载 OTP 列表
    this.loadOTPList();
    // 启动定时器更新OTP
    this.startOTPTimer();
  },
  methods: {
    // 加载 OTP 列表
    async loadOTPList() {
      const res = await getOtpNetworkAuthList();
      if (res.code === 200 && res.data) {
        this.otpList = res.data.map(item => {
          try {
            // 解析 otpAuthUri
            const otpInfo = OTPAuth.URI.parse(item.otpAuthUri);
            return {
              id: item.id,
              issuer: otpInfo.issuer,
              account: otpInfo.label,
              code: otpInfo.generate(),
              expiryTime: otpInfo.remaining() / 1000,
              progress: (otpInfo.remaining() / 1000 / otpInfo.period) * 100,
              totp: otpInfo,
              secret: otpInfo.secret,
              algorithm: otpInfo.algorithm,
              period: otpInfo.period,
              digits: otpInfo.digits,
              systemName: item.systemName || otpInfo.issuer
            };
          } catch (parseError) {
            return null;
          }
        }).filter(Boolean);
      }
    },
    // 扫描二维码
    scanQRCode() {
      uni.scanCode({
        scanType: ['qrCode'],
        success: (res) => {
          const scanResult = res.result || '';
          // 解析otpauth URL
          try {
            const otpInfo = OTPAuth.URI.parse(scanResult);
            // 显示确认保存弹窗
          uni.showModal({
            title: '确认保存',
            content: `是否保存【${otpInfo.issuer}】信息？`,
            success: (res) => {
              if (res.confirm) {
                // 保存OTP
                this.saveOTP(otpInfo);
              }
            }
          });
          }catch (error) {
            uni.showToast({
              title: '请扫描有效的二维码',
              icon: 'none'
            });
            return;
          }
        },
        fail: (err) => {
          // 区分取消扫码和扫码失败
          if (err.errMsg !== 'scanCode:fail cancel') {
            uni.showToast({
              title: '扫码失败，请重试',
              icon: 'none'
            });
          }
        }
      });
    },
    // 保存OTP
    async saveOTP(otpInfo) {
      // 生成 otpAuthUri
      const otpAuthUri = OTPAuth.URI.stringify(otpInfo);

      // 准备保存数据
      const saveData = {
        otpAuthUri: otpAuthUri,
        systemName: otpInfo.issuer
      };

      // 调用后端 API 保存
      const res = await saveOtpNetworkAuth(saveData);
      if (res.code === 200) {
        uni.showToast({
          title: '保存成功',
          icon: 'success'
        });
        await this.loadOTPList();
      }
    },
    
    // 删除OTP
    async deleteOTP(item) {
      uni.showModal({
        title: '确认删除',
        content: `确定要删除【${item.issuer}】吗？`,
        success: async (res) => {
          if (res.confirm) {
            // 调用后端 API 删除
            const result = await deleteOtpNetworkAuthList(item.id);
            if (result.code === 200) {
              await this.loadOTPList();
              uni.showToast({
                title: '删除成功',
                icon: 'success'
              });
            }
          }
        }
      });
    },
    
    // 处理输入
    handleInput() {
      if (this.editForm.systemName) {
        this.editForm.systemName = this.editForm.systemName.trim();
      }
    },
    
    // 编辑OTP
    editOTP(item) {
      this.editForm.id = item.id;
      this.editForm.systemName = item.systemName;
      this.$refs.editPopup.open();
    },
    
    // 关闭弹框
    closePopup() {
      this.$refs.editPopup.close();
    },
    
    // 保存编辑
    saveEdit() {
      const systemName = this.editForm.systemName.trim();
      if (systemName) {
        this.updateOTP(this.editForm.id, systemName);
        this.$refs.editPopup.close();
      } else {
        this.$modal.msg("系统备注不能为空");
      }
    },
    
    // 更新OTP
    async updateOTP(id, systemName) {
      const updateData = {
        id: id,
        systemName: systemName
      };
      const res = await updateOtpNetworkAuth(updateData);
      if (res.code === 200) {
        this.$modal.msgSuccess("修改成功");
        await this.loadOTPList();
      }
    },
    // 启动OTP定时器
    startOTPTimer() {
      // 清除之前的定时器，避免重复创建
      if (this.otpTimer) {
        clearInterval(this.otpTimer);
        this.otpTimer = null;
      }
      // 立即更新一次
      this.refreshOTPCodes();
      // 每秒更新一次
      this.otpTimer = setInterval(() => {
        this.refreshOTPCodes();
      }, 1000);
    },

    // 刷新OTP验证码和倒计时
    refreshOTPCodes() {
      // 只在有 OTP 项时更新
      if (this.otpList.length === 0) return;
      
      this.otpList.forEach(item => {
        // 使用保存的 TOTP 对象更新信息
        if (item.totp) {
          try {
            item.code = item.totp.generate();
            item.expiryTime = item.totp.remaining() / 1000;
            item.progress = (item.totp.remaining() / 1000 / item.period) * 100;
          } catch (error) {
            console.error('刷新 OTP 验证码失败:', error);
          }
        }
      });
    }
  }
};
</script>

<style scoped>
.container {
  padding: 0;
  min-height: 100vh;
  background-color: #f8f9fa;
  position: relative;
  overflow-x: hidden;
}

.otp-list {
  padding: 30rpx;
  padding-bottom: 150rpx;
}

.empty-state {
  text-align: center;
  padding: 200rpx 0;
  color: #999;
  animation: fadeIn 0.6s ease-in-out;
}

.empty-icon {
  font-size: 120rpx;
  margin-bottom: 40rpx;
  animation: pulse 2s infinite;
}

.empty-title {
  display: block;
  font-size: 36rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 24rpx;
  font-family: 'PingFang SC', 'Helvetica Neue', Arial, sans-serif;
}

.empty-hint {
  display: block;
  font-size: 30rpx;
  color: #666;
  line-height: 1.5;
}

.otp-item {
  background-color: #fff;
  border-radius: 20rpx;
  padding: 36rpx;
  margin-bottom: 28rpx;
  box-shadow: 0 6rpx 20rpx rgba(0, 0, 0, 0.08);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
  cursor: pointer;
}

.otp-item:hover {
  box-shadow: 0 12rpx 30rpx rgba(0, 0, 0, 0.15);
  transform: translateY(-4rpx);
  background-color: #f8f9fa;
}

.otp-item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 28rpx;
}

.otp-issuer-info {
  flex: 1;
}

.otp-issuer {
  display: block;
  font-size: 36rpx;
  font-weight: bold;
  color: #333;
  font-family: 'PingFang SC', 'Helvetica Neue', Arial, sans-serif;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-bottom: 8rpx;
}

.otp-account {
  display: block;
  font-size: 28rpx;
  color: #666;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}



.delete-btn {
  width: 56rpx;
  height: 56rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0;
  border: 1rpx solid #e8e8e8;
  background-color: #f5f5f5;
  opacity: 0.7;
  margin-left: 20rpx;
  /* 修复app端点击效果 */
  -webkit-tap-highlight-color: transparent;
}

.delete-icon {
  font-size: 36rpx;
  color: #999;
  line-height: 1;
  font-weight: bold;
}

.otp-item-content {
  position: relative;
}

.otp-code {
  display: block;
  font-size: 56rpx;
  font-weight: bold;
  text-align: center;
  letter-spacing: 16rpx;
  margin-bottom: 28rpx;
  color: #1890ff;
  font-family: 'SF Mono', Monaco, 'Cascadia Code', 'Roboto Mono', Consolas, 'Courier New', monospace;
  text-shadow: 0 2rpx 4rpx rgba(24, 144, 255, 0.1);
  transition: all 0.3s ease;
}

.otp-item:hover .otp-code {
  transform: scale(1.02);
}

.expiry-bar {
  height: 16rpx;
  background-color: #f0f0f0;
  border-radius: 8rpx;
  overflow: hidden;
  margin-bottom: 20rpx;
  box-shadow: inset 0 2rpx 4rpx rgba(0, 0, 0, 0.05);
}

.expiry-progress {
  height: 100%;
  background: linear-gradient(90deg, #52c41a 0%, #73d13d 100%);
  border-radius: 8rpx;
  transition: width 1s linear;
  box-shadow: 0 0 8rpx rgba(82, 196, 26, 0.3);
}

/* 扫描二维码按钮 */
.scan-btn {
  position: fixed;
  bottom: 50rpx;
  right: 50rpx;
  width: 120rpx;
  height: 120rpx;
  border-radius: 50%;
  background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
  color: #fff;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8rpx 24rpx rgba(24, 144, 255, 0.4);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  z-index: 999;
  border: none;
}

.scan-btn:hover {
  transform: scale(1.1);
  box-shadow: 0 12rpx 32rpx rgba(24, 144, 255, 0.6);
}

.scan-icon {
  font-size: 56rpx;
  font-weight: bold;
  line-height: 1;
  margin-bottom: 8rpx;
}

.scan-text {
  font-size: 24rpx;
  font-weight: 500;
  line-height: 1;
}



/* 动画效果 */
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20rpx);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes pulse {
  0% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
  100% {
    transform: scale(1);
  }
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: scale(0.9);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

/* 弹框样式 */
.popup-content {
  background-color: #fff;
  border-radius: 16rpx;
  padding: 32rpx;
  width: 500rpx;
  max-width: 95%;
}

.popup-title {
  font-size: 32rpx;
  font-weight: bold;
  text-align: center;
  margin-bottom: 24rpx;
  color: #333;
}

.popup-content .uni-easyinput {
  margin-bottom: 32rpx;
}

.popup-content .uni-easyinput__content {
  background-color: #f8f9fa;
  border-radius: 12rpx;
  height: 80rpx;
  padding: 0 24rpx;

  input {
    font-size: 28rpx;
    color: #2c3e50;
    &::placeholder {
      color: #909399;
    }
  }
}

.popup-buttons {
  display: flex;
  gap: 16rpx;
}

.popup-buttons button {
  flex: 1;
  height: 80rpx;
  line-height: 80rpx;
  font-size: 28rpx;
  border-radius: 12rpx;
}

.cancel-btn {
  background-color: #f5f7fa;
  color: #606266;
  border: 2rpx solid #dcdfe6;
}

.save-btn {
  background-color: #409eff;
  color: #fff;
  box-shadow: 0 4rpx 12rpx rgba(64,158,255,0.2);
}

/* 响应式设计 */
@media screen and (max-width: 375px) {
  .otp-list {
    padding: 20rpx;
  }
  
  .otp-item {
    padding: 28rpx;
  }
  
  .otp-code {
    font-size: 48rpx;
    letter-spacing: 12rpx;
  }
  
  .scan-btn {
    bottom: 30rpx;
    right: 30rpx;
    width: 100rpx;
    height: 100rpx;
  }
  
  .scan-icon {
    font-size: 48rpx;
  }
  
  .scan-text {
    font-size: 20rpx;
  }
  
  .popup-content {
    width: 95%;
    padding: 24rpx;
  }
  
  .popup-title {
    font-size: 28rpx;
  }
  
  .popup-content .uni-easyinput__content {
    height: 72rpx;
  }
  
  .popup-buttons button {
    height: 72rpx;
    line-height: 72rpx;
    font-size: 26rpx;
  }
}
</style>