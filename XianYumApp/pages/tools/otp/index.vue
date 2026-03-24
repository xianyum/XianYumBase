<template>
  <view class="container">
    <!-- OTP列表 -->
    <view class="otp-list">
      <view v-if="otpList.length === 0" class="empty-state">
        <view class="empty-icon">🔐</view>
        <text class="empty-title">暂无记录</text>
        <text class="empty-hint">点击右下角按钮扫描二维码添加</text>
      </view>
      <view v-else class="otp-item" v-for="(item, index) in otpList" :key="index">
        <view class="otp-item-header">
          <view class="otp-issuer-info">
            <text class="otp-issuer">{{ item.issuer }}</text>
            <text v-if="item.account" class="otp-account">{{ item.account }}</text>
          </view>
          <button type="default" @click="deleteOTP(item)" class="delete-btn">
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


  </view>
</template>

<script>
import { generateTOTP, generateTOTPSync, getRemainingTime, getProgress } from '@/utils/otpUtil.js';

export default {
  data() {
    return {
      otpList: [
        {
          issuer: 'GitHub',
          account: 'zhang.wei',
          code: '654321',
          expiryTime: 15,
          progress: 25,
          secret: 'ABCDEFGHIJKLMNOP',
          algorithm: 'SHA1',
          period: 30,
          digits: 6
        }
      ]
    };
  },
  mounted() {
    // 启动定时器更新OTP
    this.startOTPTimer();
  },
  methods: {
    // 扫描二维码
    scanQRCode() {
      uni.scanCode({
        scanType: ['qrCode'],
        success: (res) => {
          const scanResult = res.result || '';
          // 解析otpauth URL
          const otpInfo = this.parseOtpAuthUrl(scanResult);
          // 检查解析是否成功
          if (!otpInfo.secret) {
            this.$modal.msg('请扫描有效的OTP二维码');
            return;
          }
          
          // 显示确认保存弹窗
          this.$modal.confirm(`是否保存【${otpInfo.issuer}】信息？`, '确认保存').then((confirm) => {
            if (confirm) {
              // 保存OTP
              this.saveOTP(otpInfo);
            }
          });
        },
        fail: (err) => {
          // 区分取消扫码和扫码失败
          if (err.errMsg !== 'scanCode:fail cancel') {
            this.$modal.msg('扫码失败，请重试');
          }
        }
      });
    },
    
    // 解析otpauth URL
    parseOtpAuthUrl(url) {
      try {
        let issuer = '';
        let account = '';
        let secret = '';
        let algorithm = 'SHA1';
        let digits = 6;
        let period = 30;
        
        // 提取路径部分（issuer和account）
        const pathMatch = url.match(/otpauth:\/\/totp\/([^?]+)/);
        if (pathMatch && pathMatch[1]) {
          const pathParts = pathMatch[1].split(':');
          if (pathParts.length > 1) {
            issuer = pathParts[0];
            account = pathParts.slice(1).join(':');
          } else {
            issuer = pathParts[0];
          }
        }
        
        // 提取查询参数
        const paramsMatch = url.match(/\?(.*)$/);
        if (paramsMatch && paramsMatch[1]) {
          const params = paramsMatch[1].split('&');
          params.forEach(param => {
            const [key, value] = param.split('=');
            if (key === 'issuer') {
              issuer = decodeURIComponent(value || '');
            } else if (key === 'secret') {
              secret = decodeURIComponent(value || '');
            } else if (key === 'algorithm') {
              algorithm = decodeURIComponent(value || 'SHA1');
            } else if (key === 'digits') {
              digits = parseInt(value || '6');
            } else if (key === 'period') {
              period = parseInt(value || '30');
            }
          });
        }

        return {
          issuer,
          account,
          secret,
          algorithm,
          digits,
          period
        };
      } catch (error) {
        console.error('解析OTP URL失败:', error);
        return {
          issuer: '',
          account: '',
          secret: '',
          algorithm: 'SHA1',
          digits: 6,
          period: 30
        };
      }
    },
    
    // 保存OTP
    saveOTP(otpInfo) {
      // 生成初始OTP
      const newOTP = {
        issuer: otpInfo.issuer,
        account: otpInfo.account,
        code: this.generateOTP(otpInfo.secret, otpInfo.digits, otpInfo.period),
        expiryTime: getRemainingTime(otpInfo.period),
        progress: getProgress(otpInfo.period),
        secret: otpInfo.secret,
        algorithm: otpInfo.algorithm,
        period: otpInfo.period,
        digits: otpInfo.digits
      };
      
      this.otpList.push(newOTP);
      this.$modal.msgSuccess('保存成功');
    },
    
    // 删除OTP
    async deleteOTP(item) {
      const confirm = await this.$modal.confirm(`确定要删除【${item.issuer}】吗？`, '确认删除');
      if (confirm) {
        this.otpList.splice(this.otpList.indexOf(item), 1);
        this.$modal.msgSuccess('删除成功');
      }
    },

    // 生成OTP
    generateOTP(secret, digits = 6, period = 30) {
      try {
        return generateTOTP(secret, digits, period);
      } catch (error) {
        this.$modal.msgError('生成验证码失败');
        return '---';
      }
    },
    
    // 启动OTP定时器
    startOTPTimer() {
      // 立即更新一次
      this.updateOTPList();
      
      // 每秒更新一次
      setInterval(() => {
        this.updateOTPList();
      }, 1000);
    },

    // 更新OTP列表
    updateOTPList() {
      this.otpList.forEach(item => {
        // 更新剩余时间和进度
        item.expiryTime = getRemainingTime(item.period);
        item.progress = getProgress(item.period);
        item.code = this.generateOTP(item.secret, item.digits, item.period);
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
  padding-bottom: 150rpx; /* 为底部按钮留出空间 */
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
}

.otp-item:hover {
  box-shadow: 0 12rpx 30rpx rgba(0, 0, 0, 0.15);
  transform: translateY(-4rpx);
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
  transition: all 0.3s ease;
  margin-left: 20rpx;
}

.delete-btn:hover {
  opacity: 1;
  background-color: #ff4d4f;
  border-color: #ff4d4f;
  transform: scale(1.1);
}

.delete-icon {
  font-size: 36rpx;
  color: #999;
  line-height: 1;
  font-weight: bold;
  transition: color 0.3s ease;
}

.delete-btn:hover .delete-icon {
  color: #fff;
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
}
</style>